package com.uasdisprog.backend.config;

import com.uasdisprog.backend.entity.*;
import com.uasdisprog.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeder idempotent: aman dijalankan ulang di server.
 *
 * Masalah yang diperbaiki:
 * 1. Dulu hanya jalan kalau users kosong (count == 0). Kalau DB server sudah ada
 *    isi lama (mis. hasil import SQL dump dengan password plain "1234"), seeder
 *    tidak jalan dan login selalu gagal karena BCrypt.matches(plain, plain) = false.
 * 2. Sekarang: user dicek per-username, dibuat kalau belum ada, dan password
 *    plain lama otomatis di-repair menjadi BCrypt.
 * 3. Baris Admin dan Customer dilengkapi kalau belum ada.
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            User u1 = ensureCustomer(
                    "Fiona", "Fi", "1234", "F@gmail.com",
                    LocalDate.of(2001, 10, 1), "2020", 0.0);
            User u2 = ensureCustomer(
                    "Abel", "Ab", "5678", "A@gmail.com",
                    LocalDate.of(1995, 6, 20), "2021", 0.0);
            User u3 = ensureCustomer(
                    "Daniel", "Dan", "abcd", "D@gmail.com",
                    LocalDate.of(2005, 2, 14), "2019", 1000.0);
            ensureAdmin(
                    "a", "b", "c", "admin@uasdisprog.local",
                    LocalDate.of(2025, 6, 21), "2023");

            ensureTickets();

            System.out.println("Data seeding check completed!");
        };
    }

    private User ensureCustomer(String fullname, String username, String rawPassword,
                                String email, LocalDate dob, String memberSince,
                                Double balance) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            // Email unik: kalau email sudah dipakai user lain (DB lama kotor),
            // pakai email turunan agar tidak kena constraint.
            String safeEmail = email;
            if (userRepository.findByEmail(safeEmail).isPresent()) {
                safeEmail = username + "+seed@uasdisprog.local";
            }
            user = userRepository.save(User.builder()
                    .fullname(fullname)
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .email(safeEmail)
                    .dateOfBirth(dob)
                    .memberSince(memberSince)
                    .role(UserRole.CUSTOMER)
                    .build());
        } else {
            repairPasswordIfPlain(user, rawPassword);
        }

        final Integer userId = user.getId();
        boolean hasCustomer = customerRepository.findByUserId(userId).isPresent();
        if (!hasCustomer) {
            customerRepository.save(Customer.builder()
                    .user(user)
                    .balance(balance)
                    .build());
        }
        return user;
    }

    private User ensureAdmin(String fullname, String username, String rawPassword,
                             String email, LocalDate dob, String memberSince) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            String safeEmail = email;
            if (userRepository.findByEmail(safeEmail).isPresent()) {
                safeEmail = username + "+admin@uasdisprog.local";
            }
            user = userRepository.save(User.builder()
                    .fullname(fullname)
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .email(safeEmail)
                    .dateOfBirth(dob)
                    .memberSince(memberSince)
                    .role(UserRole.ADMIN)
                    .build());
        } else {
            repairPasswordIfPlain(user, rawPassword);
        }

        final Integer userId = user.getId();
        boolean hasAdmin = adminRepository.findByUserId(userId).isPresent();
        if (!hasAdmin) {
            adminRepository.save(Admin.builder()
                    .user(user)
                    .build());
        }
        return user;
    }

    /**
     * DB lama (SQL dump) menyimpan password plain seperti "1234".
     * Kode login memakai BCrypt.matches, jadi password plain tidak akan pernah cocok.
     * Kalau hash tersimpan bukan BCrypt, encode ulang dari password seed.
     */
    private void repairPasswordIfPlain(User user, String expectedRawPassword) {
        String stored = user.getPassword();
        if (stored == null || !(stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            user.setPassword(passwordEncoder.encode(expectedRawPassword));
            userRepository.save(user);
            System.out.println("Repaired plain-text password for user: " + user.getUsername());
        }
    }

    private void ensureTickets() {
        if (ticketRepository.count() > 0) {
            return;
        }

        ticketRepository.save(Ticket.builder()
                .judul("Burger Time")
                .genre("Comedy")
                .creator("Abel")
                .stock(86)
                .deskripsi("Once upon ...")
                .durasi(180)
                .imagePath("burger.jpg")
                .tanggalTayang(LocalDateTime.of(2027, 7, 12, 0, 0, 0))
                .price(210.0)
                .build());

        ticketRepository.save(Ticket.builder()
                .judul("Mie manis")
                .genre("Slice of life")
                .creator("Daniel")
                .stock(86)
                .deskripsi("Hello...")
                .durasi(210)
                .imagePath("mie.jpg")
                .tanggalTayang(LocalDateTime.of(2027, 8, 12, 0, 0, 0))
                .price(210.0)
                .build());

        ticketRepository.save(Ticket.builder()
                .judul("Bakery")
                .genre("Comedy")
                .creator("Mira")
                .stock(86)
                .deskripsi("Laugh...")
                .durasi(160)
                .imagePath("Bake.jpg")
                .tanggalTayang(LocalDateTime.of(2027, 10, 12, 0, 0, 0))
                .price(210.0)
                .build());

        ticketRepository.save(Ticket.builder()
                .judul("Meme")
                .genre("Horror")
                .creator("Herlina")
                .stock(86)
                .deskripsi("With love...")
                .durasi(120)
                .imagePath("Meme.jpg")
                .tanggalTayang(LocalDateTime.of(2025, 6, 30, 0, 0, 0))
                .price(210.0)
                .build());
    }
}
