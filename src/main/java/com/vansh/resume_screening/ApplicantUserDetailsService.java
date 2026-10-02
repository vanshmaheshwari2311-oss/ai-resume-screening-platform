package com.vansh.resume_screening;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class ApplicantUserDetailsService implements UserDetailsService {

    private final ApplicantUserRepository userRepository;

    public ApplicantUserDetailsService(ApplicantUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
