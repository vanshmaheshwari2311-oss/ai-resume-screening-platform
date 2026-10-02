package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/job-management")
public class JobManagementController {

    private final JobRepository jobRepository;

    public JobManagementController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /*
     * ============================================================
     * JOB MANAGEMENT PAGE
     *
     * URL:
     * http://localhost:8080/job-management
     * ============================================================
     */
    @GetMapping
    public String jobManagement(Model model) {

        var jobs = jobRepository.findAll();

        long totalJobs = jobs.size();

        long activeJobs = jobs.stream()
                .filter(job -> job.isActive())
                .count();

        long inactiveJobs = jobs.stream()
                .filter(job -> job.isInactive())
                .count();

        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("inactiveJobs", inactiveJobs);

        return "job-management";
    }
}
