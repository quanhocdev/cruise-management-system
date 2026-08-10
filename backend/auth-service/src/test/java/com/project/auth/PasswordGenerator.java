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
// role,
// provider,
// enabled,
// status,
// created_at,
// updated_at
// )
// VALUES (
// 'admin',
// '$2a$10$H3c7kDnIv7e4AHvm3EbS4.mmqdqQiirP7Zls/UreFEZZ1RU.GFffe',
// 'admin@cruisesystem.com',
// NULL,
// 'ADMIN',
// 'LOCAL',
// TRUE,
// 'ACTIVE',
// CURRENT_TIMESTAMP,
// CURRENT_TIMESTAMP
// );