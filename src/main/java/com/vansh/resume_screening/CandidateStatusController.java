package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CandidateStatusController {

    private final CandidateRepository candidateRepository;


    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

    public CandidateStatusController(
            CandidateRepository candidateRepository) {

        this.candidateRepository = candidateRepository;
    }


    /*
     * ============================================================
     * SHORTLIST CANDIDATE
     *
     * Example:
     *
     * /candidate-status/shortlist?candidateId=9&jobId=1
     *
     * ============================================================
     */

    @GetMapping("/candidate-status/shortlist")
    public String shortlistCandidate(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("jobId") Long jobId) {


        Candidate candidate =
                candidateRepository.findById(candidateId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found"
                                )
                        );


        /*
         * Change candidate status
         */

        candidate.setStatus("SHORTLISTED");


        /*
         * Save to database
         */

        candidateRepository.save(candidate);


        /*
         * Return to ranking page
         */

        return "redirect:/candidate-ranking?jobId=" + jobId;
    }


    /*
     * ============================================================
     * REJECT CANDIDATE
     *
     * Example:
     *
     * /candidate-status/reject?candidateId=9&jobId=1
     *
     * ============================================================
     */

    @GetMapping("/candidate-status/reject")
    public String rejectCandidate(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("jobId") Long jobId) {


        Candidate candidate =
                candidateRepository.findById(candidateId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found"
                                )
                        );


        /*
         * Change candidate status
         */

        candidate.setStatus("REJECTED");


        /*
         * Save to database
         */

        candidateRepository.save(candidate);


        /*
         * Return to ranking page
         */

        return "redirect:/candidate-ranking?jobId=" + jobId;
    }


    /*
     * ============================================================
     * RESET CANDIDATE STATUS
     *
     * This allows recruiter to move a candidate back to PENDING.
     *
     * ============================================================
     */

    @GetMapping("/candidate-status/reset")
    public String resetCandidateStatus(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("jobId") Long jobId) {


        Candidate candidate =
                candidateRepository.findById(candidateId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found"
                                )
                        );


        /*
         * Reset status
         */

        candidate.setStatus("PENDING");


        /*
         * Save
         */

        candidateRepository.save(candidate);


        /*
         * Return to ranking
         */

        return "redirect:/candidate-ranking?jobId=" + jobId;
    }
}
