package com.vansh.resume_screening;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner createDefaultAdmin(
            ApplicantUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (!userRepository.existsByUsernameIgnoreCase("admin")) {

                ApplicantUser admin = new ApplicantUser();

                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setFullName("Administrator");
                admin.setEmail("admin@resume-screening.local");

                userRepository.save(admin);
            }
        };
    }
}
