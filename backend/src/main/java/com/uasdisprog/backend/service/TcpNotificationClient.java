package com.uasdisprog.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

@Slf4j
@Service
public class TcpNotificationClient {

    @Value("${tcp.server.host:localhost}")
    private String tcpHost;

    @Value("${tcp.server.port:6002}")
    private int tcpPort;

    // TLS opsional (default MATI = plaintext). Aktifkan via env:
    // TCP_TLS_ENABLED=true + TCP_TLS_TRUSTSTORE_PATH/PASSWORD.
    // Truststore berisi sertifikat server (dibuat via scripts/gen-tls-certs.sh).
    @Value("${tcp.tls.enabled:false}")
    private boolean tlsEnabled;

    @Value("${tcp.tls.truststore-path:}")
    private String tlsTruststorePath;

    @Value("${tcp.tls.truststore-password:}")
    private String tlsTruststorePassword;

    public void sendPersonalNotification(Integer customerId, String message) {
        // Protocol format: [Message]-personal-[customerId]
        String payload = message + "-personal-" + customerId;
        sendTcpMessage(payload);
    }

    public void sendBroadcastNotification(String message) {
        // Protocol format: [Message]-broadcast-0
        String payload = message + "-broadcast-0";
        sendTcpMessage(payload);
    }

    private void sendTcpMessage(String payload) {
        try (Socket socket = createSocket();
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(payload);
            log.info("Sent notification via TCP{} Socket to {}:{}: {}",
                    tlsEnabled ? "/TLS" : "", tcpHost, tcpPort, payload);
        } catch (Exception e) {
            log.error("Failed to send TCP notification to {}:{}: {}", tcpHost, tcpPort, e.getMessage());
        }
    }

    private Socket createSocket() throws IOException {
        if (!tlsEnabled) {
            return new Socket(tcpHost, tcpPort);
        }
        if (tlsTruststorePath == null || tlsTruststorePath.isBlank()) {
            throw new IOException("tcp.tls.truststore-path kosong (isi via env TCP_TLS_TRUSTSTORE_PATH)");
        }
        try {
            KeyStore ts = KeyStore.getInstance("PKCS12");
            try (InputStream in = new FileInputStream(tlsTruststorePath)) {
                char[] pass = tlsTruststorePassword == null ? new char[0]
                        : tlsTruststorePassword.toCharArray();
                ts.load(in, pass);
            }
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), new SecureRandom());
            SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket(tcpHost, tcpPort);
            socket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
            socket.startHandshake();
            return socket;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Gagal init TLS client: " + e.getMessage(), e);
        }
    }
}
