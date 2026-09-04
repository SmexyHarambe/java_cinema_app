package com.uasdisprog.tcpserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uasdisprog.tcpserver.entity.Notifikasi;
import com.uasdisprog.tcpserver.entity.NotifUser;
import com.uasdisprog.tcpserver.entity.NotificationType;
import com.uasdisprog.tcpserver.repository.NotifikasiRepository;
import com.uasdisprog.tcpserver.repository.NotifUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class TCPServerService implements Runnable {

    @Autowired
    private NotifikasiRepository notifikasiRepository;
    
    @Autowired
    private NotifUserRepository notifUserRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${tcp.server.port:6002}")
    private int port;
    
    @Value("${tcp.server.thread-pool-size:10}")
    private int threadPoolSize;

    // TLS opsional (default MATI = plaintext). Aktifkan via env:
    // TCP_TLS_ENABLED=true + TCP_TLS_KEYSTORE_PATH/PASSWORD.
    // Keystore (PKCS12) berisi private key + sertifikat server,
    // dibuat via scripts/gen-tls-certs.sh.
    @Value("${tcp.tls.enabled:false}")
    private boolean tlsEnabled;

    @Value("${tcp.tls.keystore-path:}")
    private String tlsKeystorePath;

    @Value("${tcp.tls.keystore-password:}")
    private String tlsKeystorePassword;

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final Map<Socket, PrintWriter> clients = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    @jakarta.annotation.PostConstruct
    public void start() {
        log.info(tlsEnabled ? "TCP TLS ENABLED" : "TCP TLS DISABLED (plaintext)");
        running = true;
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        new Thread(this, "TCP-Server-Main").start();
        log.info("TCP Server started on port {}", port);
    }

    @jakarta.annotation.PreDestroy
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (executorService != null) {
                executorService.shutdown();
            }
            clients.clear();
            log.info("TCP Server stopped");
        } catch (IOException e) {
            log.error("Error stopping TCP Server", e);
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = createServerSocket();
            log.info("TCP Server listening on port {} ({})",
                    port, tlsEnabled ? "TLS" : "plaintext");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        log.error("Error accepting client connection", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("TCP Server error", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            clients.put(clientSocket, out);

            String message;
            while ((message = in.readLine()) != null) {
                log.debug("Received: {}", message);
                processMessage(clientSocket, message);
            }
        } catch (IOException e) {
            log.debug("Client disconnected: {}", clientSocket.getInetAddress());
        } finally {
            clients.remove(clientSocket);
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Error closing client socket", e);
            }
        }
    }

    /**
     * Bikin ServerSocket biasa atau SSLServerSocket sesuai flag tcp.tls.enabled.
     * Protokol baris di atasnya TIDAK berubah (tetap newline-delimited plaintext,
     * hanya dibungkus TLS di level transport).
     */
    private ServerSocket createServerSocket() throws IOException {
        if (!tlsEnabled) {
            return new ServerSocket(port);
        }
        if (tlsKeystorePath == null || tlsKeystorePath.isBlank()) {
            throw new IOException("tcp.tls.keystore-path kosong (isi via env TCP_TLS_KEYSTORE_PATH)");
        }
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream in = new FileInputStream(tlsKeystorePath)) {
                ks.load(in, tlsKeystorePassword.toCharArray());
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, tlsKeystorePassword.toCharArray());
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), null, new SecureRandom());
            SSLServerSocket ss = (SSLServerSocket) ctx.getServerSocketFactory()
                    .createServerSocket(port);
            ss.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
            return ss;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Gagal init TLS server: " + e.getMessage(), e);
        }
    }

    private void processMessage(Socket clientSocket, String message) {
        try {
            String[] parts = message.split("-", 5);
            if (parts.length < 2) {
                return;
            }

            String messageType = parts[1];

            switch (messageType) {
                case "broadcast":
                    handleBroadcast(parts[0]);
                    break;
                case "personal":
                    handlePersonal(parts[0], Integer.parseInt(parts[2]));
                    break;
                case "hitungnotif":
                    handleCountNotif(clientSocket, Integer.parseInt(parts[2]));
                    break;
                case "bacanotif":
                    handleReadNotif(clientSocket, Integer.parseInt(parts[2]));
                    break;
                case "hapusnotif":
                    handleDeleteNotif(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                    break;
                default:
                    log.debug("Unknown message type: {}", messageType);
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }

    private void handleBroadcast(String message) {
        Notifikasi notif = Notifikasi.builder()
                .message(message)
                .type(NotificationType.BROADCAST)
                .build();
        Notifikasi saved = notifikasiRepository.save(notif);

        NotifUser notifUser = NotifUser.builder()
                .notifId(saved.getId())
                .isRead(false)
                .build();
        notifUserRepository.save(notifUser);

        broadcastToAll("notification-" + message);
        log.info("Broadcast notification sent: {}", message);
    }

    private void handlePersonal(String message, Integer customerId) {
        Notifikasi notif = Notifikasi.builder()
                .message(message)
                .type(NotificationType.PERSONAL)
                .build();
        Notifikasi saved = notifikasiRepository.save(notif);

        NotifUser notifUser = NotifUser.builder()
                .notifId(saved.getId())
                .customerId(customerId)
                .isRead(false)
                .build();
        notifUserRepository.save(notifUser);

        sendToCustomer(customerId, "notification-" + message);
        log.info("Personal notification sent to customer {}: {}", customerId, message);
    }

    private void handleCountNotif(Socket clientSocket, Integer customerId) {
        Long count = notifUserRepository.countUnreadByCustomerId(customerId);
        sendToClient(clientSocket, "jumlahBelumBaca-" + count);
    }

    private void handleReadNotif(Socket clientSocket, Integer customerId) {
        List<NotifUser> notifUsers = notifUserRepository.findByCustomerIdOrderByIdDesc(customerId);
        List<Map<String, Object>> content = new ArrayList<>();
        
        for (NotifUser nu : notifUsers) {
            notifikasiRepository.findById(nu.getNotifId()).ifPresent(n -> {
                content.add(Map.of(
                    "id", nu.getId(),
                    "message", n.getMessage(),
                    "type", n.getType().name(),
                    "createdAt", n.getCreatedAt().toString(),
                    "isRead", nu.getIsRead()
                ));
            });
        }

        try {
            String json = objectMapper.writeValueAsString(content);
            sendToClient(clientSocket, "notifContent-" + json);
            
            notifUsers.forEach(nu -> {
                nu.setIsRead(true);
                notifUserRepository.save(nu);
            });
        } catch (Exception e) {
            log.error("Error serializing notification content", e);
        }
    }

    private void handleDeleteNotif(Integer notifId, Integer customerId) {
        notifUserRepository.deleteByNotifIdAndCustomerId(notifId, customerId);
        log.info("Notification {} deleted for customer {}", notifId, customerId);
    }

    public void broadcastToAll(String message) {
        clients.values().forEach(writer -> writer.println(message));
    }

    public void sendToCustomer(Integer customerId, String message) {
        broadcastToAll(message);
    }

    private void sendToClient(Socket clientSocket, String message) {
        PrintWriter writer = clients.get(clientSocket);
        if (writer != null) {
            writer.println(message);
        }
    }
}
