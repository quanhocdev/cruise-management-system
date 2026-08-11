package com.project.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class PasswordGenerator {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode(password);

        System.out.println();
        System.out.println("=================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash:");
        System.out.println(hash);
        System.out.println("=================================");

        scanner.close();
    }
}
// INSERT INTO users (
// username,
// password,
// email,
// firebase_uid,
// role_id,
// provider,
// enabled,
// status,
// created_at,
// updated_at
// ) VALUES (
// 'admin',
// '$2a$10$pyRtq3XMOzkPVy4XBbBduwzEZI3etnqRZFwn4qaPZySkCwGkbZa',
// 'admin@cruisesystem.com',
// NULL,
// 1,
// 'LOCAL',
// TRUE,
// 'ACTIVE',
// CURRENT_TIMESTAMP,
// CURRENT_TIMESTAMP
// )
// ON CONFLICT (username) DO UPDATE SET
// password = EXCLUDED.password,
// enabled = TRUE,
// status = 'ACTIVE';