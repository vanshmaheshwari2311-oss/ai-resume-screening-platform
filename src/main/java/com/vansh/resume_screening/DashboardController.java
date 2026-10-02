package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */
    public DashboardController(
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            JobApplicationRepository applicationRepository) {

        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }


    /*
     * ============================================================
     * DASHBOARD
     *
     * URL:
     * http://localhost:8080/dashboard
     * ============================================================
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        /*
         * --------------------------------------------------------
         * CANDIDATE COUNTS
         * --------------------------------------------------------
         */

        long totalCandidates =
                candidateRepository.count();

        long shortlistedCandidates =
                candidateRepository.findAll()
                        .stream()
                        .filter(Candidate::isShortlisted)
                        .count();

        long rejectedCandidates =
                candidateRepository.findAll()
                        .stream()
                        .filter(Candidate::isRejected)
                        .count();

        long pendingCandidates =
                candidateRepository.findAll()
                        .stream()
                        .filter(Candidate::isPending)
                        .count();


        /*
         * --------------------------------------------------------
         * JOB COUNTS
         * --------------------------------------------------------
         */

        long totalJobs =
                jobRepository.count();

        long activeJobs =
                jobRepository.findAll()
                        .stream()
                        .filter(Job::isActive)
                        .count();

        long inactiveJobs =
                jobRepository.findAll()
                        .stream()
                        .filter(Job::isInactive)
                        .count();


        /*
         * --------------------------------------------------------
         * SEND DATA TO HTML
         * --------------------------------------------------------
         */

        model.addAttribute(
                "totalCandidates",
                totalCandidates
        );

        model.addAttribute(
                "shortlistedCandidates",
                shortlistedCandidates
        );

        model.addAttribute(
                "rejectedCandidates",
                rejectedCandidates
        );

        model.addAttribute(
                "pendingCandidates",
                pendingCandidates
        );

        model.addAttribute(
                "totalJobs",
                totalJobs
        );

        model.addAttribute(
                "activeJobs",
                activeJobs
        );

        model.addAttribute(
                "inactiveJobs",
                inactiveJobs
        );

        model.addAttribute("totalApplications", applicationRepository.count());


        /*
         * --------------------------------------------------------
         * RETURN DASHBOARD HTML
         * --------------------------------------------------------
         */

        return "dashboard";
    }
}
