package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateDashboardController {

    private final ApplicantUserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    public CandidateDashboardController(
            ApplicantUserRepository userRepository,
            JobRepository jobRepository,
            JobApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/candidate/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        ApplicantUser user = currentUser(authentication);

        model.addAttribute("user", user);
        model.addAttribute(
                "jobs",
                jobRepository.findByStatusIgnoreCaseOrderByIdDesc("ACTIVE")
        );
        model.addAttribute(
                "applications",
                applicationRepository.findByApplicantUserIdOrderByAppliedAtDesc(user.getId())
        );

        return "candidate-dashboard";
    }

    private ApplicantUser currentUser(Authentication authentication) {
        return userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user was not found."));
    }
}
