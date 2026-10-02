package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vansh.resume_screening.AIAnalysis;
import com.vansh.resume_screening.AIAnalysisRepository;
import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;
import com.vansh.resume_screening.Job;
import com.vansh.resume_screening.JobRepository;
import com.vansh.resume_screening.service.AIResumeAnalysisService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/ai")
public class AIAnalysisController {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final AIResumeAnalysisService aiResumeAnalysisService;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public AIAnalysisController(
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            AIAnalysisRepository aiAnalysisRepository,
            AIResumeAnalysisService aiResumeAnalysisService) {

        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.aiResumeAnalysisService = aiResumeAnalysisService;
    }

    // ============================================================
    // ANALYZE ONE CANDIDATE
    // ============================================================

    @GetMapping("/analyze/{candidateId}/{jobId}")
    public ResponseEntity<?> analyzeCandidate(
            @PathVariable Long candidateId,
            @PathVariable Long jobId) {

        try {

            Candidate candidate =
                    candidateRepository
                            .findById(candidateId)
                            .orElse(null);

            if (candidate == null) {
                return ResponseEntity.notFound().build();
            }

            Job job =
                    jobRepository
                            .findById(jobId)
                            .orElse(null);

            if (job == null) {
                return ResponseEntity.notFound().build();
            }

            if (candidate.getResumeText() == null ||
                    candidate.getResumeText().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Candidate resume text is empty. "
                                + "Please upload/process the resume first."
                        );
            }

            AIAnalysis analysis =
                    createAnalysis(candidate, job);

            AIAnalysis savedAnalysis =
                    aiAnalysisRepository.save(analysis);

            return ResponseEntity.ok(savedAnalysis);

        } catch (Exception e) {

            e.printStackTrace();

            if (isQuotaError(e)) {

                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(
                                "Gemini free-tier quota has been reached. "
                                + "Please wait for the quota to reset "
                                + "or use another properly configured project."
                        );
            }

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "AI analysis failed: "
                                    + e.getMessage()
                    );
        }
    }

    // ============================================================
    // ANALYZE ALL CANDIDATES
    // ============================================================

    @PostMapping("/analyze-all/{jobId}")
    public ResponseEntity<?> analyzeAllCandidates(
            @PathVariable Long jobId) {

        try {

            Job job =
                    jobRepository
                            .findById(jobId)
                            .orElse(null);

            if (job == null) {
                return ResponseEntity.notFound().build();
            }

            List<Candidate> candidates =
                    candidateRepository.findAll();

            int analyzed = 0;
            int skipped = 0;
            int failed = 0;

            boolean quotaStopped = false;

            List<String> errors =
                    new ArrayList<>();

            for (Candidate candidate : candidates) {

                try {

                    // ------------------------------------------------
                    // 1. SKIP IF RESUME TEXT DOES NOT EXIST
                    // ------------------------------------------------

                    if (candidate.getResumeText() == null ||
                            candidate.getResumeText().isBlank()) {

                        skipped++;
                        continue;
                    }

                    // ------------------------------------------------
                    // 2. SKIP IF ALREADY ANALYZED FOR THIS JOB
                    // ------------------------------------------------

                    AIAnalysis existingAnalysis =
                            aiAnalysisRepository
                                    .findTopByCandidateIdAndJobIdOrderByAnalyzedAtDesc(
                                            candidate.getId(),
                                            job.getId()
                                    );

                    if (existingAnalysis != null) {

                        skipped++;
                        continue;
                    }

                    // ------------------------------------------------
                    // 3. RUN GEMINI ANALYSIS
                    // ------------------------------------------------

                    AIAnalysis analysis =
                            createAnalysis(
                                    candidate,
                                    job
                            );

                    aiAnalysisRepository.save(analysis);

                    analyzed++;

                } catch (Exception e) {

                    // ------------------------------------------------
                    // 4. STOP IMMEDIATELY ON GEMINI QUOTA ERROR
                    // ------------------------------------------------

                    if (isQuotaError(e)) {

                        quotaStopped = true;

                        errors.add(
                                "Gemini quota reached. "
                                + "Bulk analysis stopped to prevent "
                                + "additional failed API requests."
                        );

                        break;
                    }

                    // ------------------------------------------------
                    // 5. OTHER ERRORS
                    // ------------------------------------------------

                    failed++;

                    errors.add(
                            candidate.getName()
                                    + ": "
                                    + e.getMessage()
                    );

                    e.printStackTrace();
                }
            }

            // --------------------------------------------------------
            // RESULT MESSAGE
            // --------------------------------------------------------

            String message;

            if (quotaStopped) {

                message =
                        "Analysis stopped because the Gemini "
                                + "free-tier quota was reached. "
                                + "Already completed analyses were kept.";

            } else {

                message =
                        "Analysis completed successfully.";
            }

            return ResponseEntity.ok(
                    new AnalyzeAllResponse(
                            message,
                            analyzed,
                            skipped,
                            failed,
                            quotaStopped,
                            errors
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Bulk AI analysis failed: "
                                    + e.getMessage()
                    );
        }
    }


    @GetMapping("/result/{candidateId}/{jobId}")
public ResponseEntity<?> getAnalysisResult(
        @PathVariable Long candidateId,
        @PathVariable Long jobId) {

    try {

        AIAnalysis analysis =
                aiAnalysisRepository
                        .findTopByCandidateIdAndJobIdOrderByAnalyzedAtDesc(
                                candidateId,
                                jobId
                        );

        if (analysis == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("AI analysis not found for this candidate and job.");
        }

        return ResponseEntity.ok(analysis);

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body(
                        "Failed to load AI analysis: "
                                + e.getMessage()
                );
    }
}

    // ============================================================
    // CREATE AI ANALYSIS
    // ============================================================

    private AIAnalysis createAnalysis(
            Candidate candidate,
            Job job) {

        String jobDescription = """
                Job Title:
                %s

                Job Description:
                %s

                Required Skills:
                %s

                Preferred Skills:
                %s

                Experience Required:
                %s
                """.formatted(
                job.getTitle(),
                job.getDescription(),
                job.getRequiredSkills(),
                job.getPreferredSkills(),
                job.getExperience()
        );

        String aiJson =
                aiResumeAnalysisService.analyzeResume(
                        candidate.getResumeText(),
                        jobDescription
                );

        try {

            JsonNode json =
                    objectMapper.readTree(aiJson);

            AIAnalysis analysis =
                    new AIAnalysis();

            analysis.setCandidateId(
                    candidate.getId()
            );

            analysis.setJobId(
                    job.getId()
            );

            analysis.setAtsScore(
                    json.path("atsScore").asInt()
            );

            analysis.setMatchLevel(
                    json.path("matchLevel").asText()
            );

            analysis.setMatchedSkills(
                    json.path("matchedSkills").toString()
            );

            analysis.setMissingSkills(
                    json.path("missingSkills").toString()
            );

            analysis.setExperienceAnalysis(
                    json.path("experienceAnalysis").asText()
            );

            analysis.setStrengths(
                    json.path("strengths").toString()
            );

            analysis.setGaps(
                    json.path("gaps").toString()
            );

            analysis.setRecommendation(
                    json.path("recommendation").asText()
            );

            analysis.setAnalyzedAt(
                    LocalDateTime.now()
            );

            return analysis;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid AI JSON response: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // ============================================================
    // DETECT GEMINI QUOTA ERROR
    // ============================================================

    private boolean isQuotaError(Exception e) {

        Throwable current = e;

        while (current != null) {

            String message =
                    current.getMessage();

            if (message != null) {

                String lower =
                        message.toLowerCase();

                if (lower.contains("429")
                        || lower.contains("resource_exhausted")
                        || lower.contains("quota exceeded")
                        || lower.contains("quota")) {

                    return true;
                }
            }

            current =
                    current.getCause();
        }

        return false;
    }

    // ============================================================
    // RESPONSE CLASS
    // ============================================================

    public static class AnalyzeAllResponse {

        private String message;
        private int analyzed;
        private int skipped;
        private int failed;
        private boolean quotaStopped;
        private List<String> errors;

        public AnalyzeAllResponse(
                String message,
                int analyzed,
                int skipped,
                int failed,
                boolean quotaStopped,
                List<String> errors) {

            this.message = message;
            this.analyzed = analyzed;
            this.skipped = skipped;
            this.failed = failed;
            this.quotaStopped = quotaStopped;
            this.errors = errors;
        }

        public String getMessage() {
            return message;
        }

        public int getAnalyzed() {
            return analyzed;
        }

        public int getSkipped() {
            return skipped;
        }

        public int getFailed() {
            return failed;
        }

        public boolean isQuotaStopped() {
            return quotaStopped;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
