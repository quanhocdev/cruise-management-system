
package com.project.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class PasswordGenerator {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("=================================");
        System.out.println("       BCRYPT PASSWORD GENERATOR");
        System.out.println("Nhập 'exit' hoặc 'quit' để dừng.");
        System.out.println("=================================");

        while (true) {

            System.out.print("\nNhập mật khẩu: ");
            String password = scanner.nextLine();

            // Kiểm tra lệnh dừng
            if (password.equalsIgnoreCase("exit")
                    || password.equalsIgnoreCase("quit")) {
                System.out.println("Đã dừng chương trình!");
                break;
            }

            if (password.isBlank()) {
                System.out.println("Mật khẩu không được để trống!");
                continue;
            }

            String hash = encoder.encode(password);

            System.out.println("---------------------------------");
            System.out.println("Password: " + password);
            System.out.println("BCrypt Hash:");
            System.out.println(hash);
            System.out.println("---------------------------------");
        }

        scanner.close();
    }
}
