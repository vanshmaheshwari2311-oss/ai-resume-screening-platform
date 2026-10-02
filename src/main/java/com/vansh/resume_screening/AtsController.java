package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AtsController {

    private final CandidateRepository candidateRepository;
    private final AtsScoringService atsScoringService;
    private final JobRepository jobRepository;

    public AtsController(
            CandidateRepository candidateRepository,
            AtsScoringService atsScoringService,
            JobRepository jobRepository) {

        this.candidateRepository = candidateRepository;
        this.atsScoringService = atsScoringService;
        this.jobRepository = jobRepository;
    }


    /*
     * ============================================================
     * SHOW AI SCREENING PAGE
     * ============================================================
     */
    @GetMapping("/ai-screening")
    public String aiScreening(Model model) {

        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );

        model.addAttribute(
                "jobs",
                jobRepository.findAll()
        );

        return "ai-screening";
    }


    /*
     * ============================================================
     * ANALYZE RESUME
     * ============================================================
     */
    @GetMapping("/ai-screening/analyze")
    public String analyzeResume(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("jobId") Long jobId,
            Model model) {


        /*
         * ========================================================
         * FIND CANDIDATE
         * ========================================================
         */
        Candidate candidate =
                candidateRepository.findById(candidateId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found"
                                )
                        );


        /*
         * ========================================================
         * FIND JOB
         * ========================================================
         */
        Job job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found"
                                )
                        );


        /*
         * ========================================================
         * GET RESUME TEXT
         * ========================================================
         */
        String resumeText =
                candidate.getResumeText();


        /*
         * ========================================================
         * GET JOB SKILLS
         * ========================================================
         */
        String requiredSkills =
                job.getRequiredSkills();

        String preferredSkills =
                job.getPreferredSkills();


        /*
         * ========================================================
         * CHECK RESUME TEXT
         * ========================================================
         *
         * Prevent analysis if the candidate does not have
         * extracted resume text.
         * ========================================================
         */
        if (resumeText == null || resumeText.isBlank()) {

            model.addAttribute(
                    "candidates",
                    candidateRepository.findAll()
            );

            model.addAttribute(
                    "jobs",
                    jobRepository.findAll()
            );

            model.addAttribute(
                    "candidate",
                    candidate
            );

            model.addAttribute(
                    "selectedCandidateId",
                    candidate.getId()
            );

            model.addAttribute(
                    "job",
                    job
            );

            model.addAttribute(
                    "error",
                    "No resume text was extracted for this candidate. Please upload the PDF/DOCX again."
            );

            return "ai-screening";
        }


        /*
         * ========================================================
         * WEIGHTED ATS ANALYSIS
         *
         * Required Skills  = 80%
         * Preferred Skills = 20%
         * ========================================================
         */
        AtsScoringService.AtsResult atsResult =
                atsScoringService.analyzeWeighted(
                        resumeText,
                        requiredSkills,
                        preferredSkills
                );


        /*
         * ========================================================
         * FINAL SCORE
         * ========================================================
         */
        int score =
                atsResult.getScore();


        /*
         * ========================================================
         * MATCHED REQUIRED SKILLS
         * ========================================================
         */
        List<String> matchedSkills =
                atsResult.getMatchedSkills();


        /*
         * ========================================================
         * MISSING REQUIRED SKILLS
         * ========================================================
         */
        List<String> missingSkills =
                atsResult.getMissingSkills();


        /*
         * ========================================================
         * MATCHED PREFERRED SKILLS
         * ========================================================
         */
        List<String> matchedPreferredSkills =
                atsResult.getMatchedPreferredSkills();


        /*
         * ========================================================
         * DETECT ALL SKILLS FROM RESUME
         * ========================================================
         */
        List<String> detectedSkills =
                atsScoringService.detectSkills(
                        resumeText
                );


        /*
         * ========================================================
         * MISSING PREFERRED SKILLS
         * ========================================================
         */
        List<String> missingPreferredSkills =
                atsScoringService.findMissingPreferredSkills(
                        resumeText,
                        preferredSkills
                );


        /*
         * ========================================================
         * SEND DATA TO THYMELEAF
         * ========================================================
         */

        // All candidates
        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );


        // All jobs
        model.addAttribute(
                "jobs",
                jobRepository.findAll()
        );


        // Selected candidate
        model.addAttribute(
                "candidate",
                candidate
        );


        // Keep selected candidate in dropdown
        model.addAttribute(
                "selectedCandidateId",
                candidate.getId()
        );


        // Selected job
        model.addAttribute(
                "job",
                job
        );


        // Final ATS score
        model.addAttribute(
                "score",
                score
        );


        // Required matched skills
        model.addAttribute(
                "matchedSkills",
                matchedSkills
        );


        // Required missing skills
        model.addAttribute(
                "missingSkills",
                missingSkills
        );


        // Preferred matched skills
        model.addAttribute(
                "matchedPreferredSkills",
                matchedPreferredSkills
        );


        // Preferred missing skills
        model.addAttribute(
                "missingPreferredSkills",
                missingPreferredSkills
        );


        // Required score out of 80
        model.addAttribute(
                "requiredScore",
                atsResult.getRequiredScore()
        );


        // Preferred score out of 20
        model.addAttribute(
                "preferredScore",
                atsResult.getPreferredScore()
        );


        // All detected skills
        model.addAttribute(
                "detectedSkills",
                detectedSkills
        );


        /*
         * ========================================================
         * RETURN TO ATS PAGE
         * ========================================================
         */
        return "ai-screening";
    }
}
