package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationController {

    private final JobApplicationRepository applicationRepository;

    public AdminApplicationController(JobApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/applications")
    public String applications(Model model) {
        model.addAttribute("applications", applicationRepository.findAll());
        return "admin-applications";
    }

    @PostMapping("/applications/status/{id}")
    @Transactional
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        if (!java.util.Set.of("SUBMITTED", "UNDER_REVIEW", "SHORTLISTED", "REJECTED").contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Invalid application status.");
        }

        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));

        application.setStatus(status);
        applicationRepository.save(application);

        // Keep the existing recruiter candidate workflow synchronized.
        if (application.getCandidate() != null) {
            if ("SHORTLISTED".equalsIgnoreCase(status)) {
                application.getCandidate().setStatus("SHORTLISTED");
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                application.getCandidate().setStatus("REJECTED");
            } else {
                application.getCandidate().setStatus("PENDING");
            }
        }

        return "redirect:/applications";
    }
}
