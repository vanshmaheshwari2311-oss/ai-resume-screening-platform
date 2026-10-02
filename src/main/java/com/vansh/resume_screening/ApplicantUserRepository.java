package com.vansh.resume_screening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicantUserRepository extends JpaRepository<ApplicantUser, Long> {
    Optional<ApplicantUser> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
