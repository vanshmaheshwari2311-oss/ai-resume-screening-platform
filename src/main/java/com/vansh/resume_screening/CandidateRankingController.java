package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CandidateRankingController {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    public CandidateRankingController(
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            AIAnalysisRepository aiAnalysisRepository) {

        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
    }

    // =========================================================
    // CANDIDATE RANKING
    // =========================================================

    @GetMapping("/candidate-ranking")
    public String candidateRanking(
            @RequestParam(required = false) Long jobId,
            Model model) {

        // -----------------------------------------------------
        // LOAD ALL JOBS
        // -----------------------------------------------------

        List<Job> jobs =
                jobRepository.findAll();


        // -----------------------------------------------------
        // LOAD ALL CANDIDATES
        // -----------------------------------------------------

        List<Candidate> candidates =
                candidateRepository.findAll();


        // -----------------------------------------------------
        // CREATE RANKING RESULT LIST
        // -----------------------------------------------------

        List<RankingResult> results =
                new ArrayList<>();


        // -----------------------------------------------------
        // IF A JOB HAS BEEN SELECTED
        // -----------------------------------------------------

        if (jobId != null) {

            Job selectedJob =
                    jobRepository
                            .findById(jobId)
                            .orElse(null);

            // -------------------------------------------------
            // SEND SELECTED JOB TO THYMELEAF
            // -------------------------------------------------

            model.addAttribute(
                    "job",
                    selectedJob
            );


            if (selectedJob != null) {

                // ---------------------------------------------
                // ANALYSIS FOR EACH CANDIDATE
                // ---------------------------------------------

                for (Candidate candidate : candidates) {

                    AIAnalysis analysis =
                            aiAnalysisRepository
                                    .findTopByCandidateIdAndJobIdOrderByAnalyzedAtDesc(
                                            candidate.getId(),
                                            selectedJob.getId()
                                    );


                    RankingResult result =
                            new RankingResult(
                                    candidate,
                                    analysis
                            );


                    results.add(result);
                }


                // ---------------------------------------------
                // SORTING
                //
                // 1. Analyzed candidates first
                // 2. Highest ATS score first
                // 3. Not analyzed candidates afterwards
                // ---------------------------------------------

                results.sort(
                        Comparator
                                .comparing(
                                        RankingResult::hasAnalysis
                                )
                                .reversed()
                                .thenComparing(
                                        RankingResult::getAtsScore,
                                        Comparator.reverseOrder()
                                )
                );
            }

        } else {

            // -------------------------------------------------
            // NO JOB SELECTED
            // -------------------------------------------------

            model.addAttribute(
                    "job",
                    null
            );
        }


        // =====================================================
        // MODEL ATTRIBUTES
        // =====================================================

        model.addAttribute(
                "jobs",
                jobs
        );

        model.addAttribute(
                "candidates",
                candidates
        );

        model.addAttribute(
                "results",
                results
        );

        model.addAttribute(
                "totalCandidates",
                candidates.size()
        );


        // -----------------------------------------------------
        // IMPORTANT:
        // Send selected job ID separately to the HTML.
        // -----------------------------------------------------

        model.addAttribute(
                "selectedJobId",
                jobId
        );


        // =====================================================
        // RETURN PAGE
        // =====================================================

        return "candidate-ranking";
    }


    // =========================================================
    // RANKING RESULT DTO
    // =========================================================

    public static class RankingResult {

        private final Candidate candidate;

        private final AIAnalysis analysis;


        // -----------------------------------------------------
        // CONSTRUCTOR
        // -----------------------------------------------------

        public RankingResult(
                Candidate candidate,
                AIAnalysis analysis) {

            this.candidate = candidate;
            this.analysis = analysis;
        }


        // -----------------------------------------------------
        // GET CANDIDATE
        // -----------------------------------------------------

        public Candidate getCandidate() {

            return candidate;
        }


        // -----------------------------------------------------
        // GET AI ANALYSIS
        // -----------------------------------------------------

        public AIAnalysis getAnalysis() {

            return analysis;
        }


        // -----------------------------------------------------
        // CHECK WHETHER AI ANALYSIS EXISTS
        // -----------------------------------------------------

        public boolean hasAnalysis() {

            return analysis != null;
        }


        // -----------------------------------------------------
        // GET ATS SCORE
        // -----------------------------------------------------

        public int getAtsScore() {

            if (analysis == null ||
                    analysis.getAtsScore() == null) {

                return 0;
            }

            return analysis.getAtsScore();
        }


        // -----------------------------------------------------
        // GET MATCH LEVEL
        // -----------------------------------------------------

        public String getMatchLevel() {

            if (analysis == null ||
                    analysis.getMatchLevel() == null) {

                return "NOT ANALYZED";
            }

            return analysis.getMatchLevel();
        }


        // -----------------------------------------------------
        // GET RECOMMENDATION
        // -----------------------------------------------------

        public String getRecommendation() {

            if (analysis == null ||
                    analysis.getRecommendation() == null ||
                    analysis.getRecommendation().isBlank()) {

                return "Resume has not been analyzed for this job.";
            }

            return analysis.getRecommendation();
        }


        // -----------------------------------------------------
        // GET ANALYZED DATE/TIME
        // -----------------------------------------------------

        public String getAnalyzedAt() {

            if (analysis == null ||
                    analysis.getAnalyzedAt() == null) {

                return "-";
            }

            return analysis
                    .getAnalyzedAt()
                    .toString();
        }
    }
}
