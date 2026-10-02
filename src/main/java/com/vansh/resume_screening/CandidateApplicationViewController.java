package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateApplicationViewController {

    private final ApplicantUserRepository userRepository;
    private final JobApplicationRepository applicationRepository;

    public CandidateApplicationViewController(
            ApplicantUserRepository userRepository,
            JobApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/candidate/applications")
    public String applications(Authentication authentication, Model model) {
        ApplicantUser user = currentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute(
                "applications",
                applicationRepository.findByApplicantUserIdOrderByAppliedAtDesc(user.getId())
        );
        return "candidate-applications";
    }

    private ApplicantUser currentUser(Authentication authentication) {
        return userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user was not found."));
    }
}
