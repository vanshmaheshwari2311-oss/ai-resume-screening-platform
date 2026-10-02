package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vansh.resume_screening.AIInterviewQuestionRepository;
import com.vansh.resume_screening.AIInterviewSummary;
import com.vansh.resume_screening.AIInterviewSummaryRepository;
import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;
import com.vansh.resume_screening.InterviewEvaluation;
import com.vansh.resume_screening.InterviewEvaluationRepository;
import com.vansh.resume_screening.service.AIInterviewSummaryService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/interview-summary")
public class AIInterviewSummaryController {

    private final AIInterviewSummaryService summaryService;
    private final AIInterviewSummaryRepository summaryRepository;
    private final AIInterviewQuestionRepository questionRepository;
    private final InterviewEvaluationRepository evaluationRepository;
    private final CandidateRepository candidateRepository;

    /*
     * ObjectMapper is created directly instead of being injected
     * as a Spring bean.
     */
    private final ObjectMapper objectMapper;

    public AIInterviewSummaryController(
            AIInterviewSummaryService summaryService,
            AIInterviewSummaryRepository summaryRepository,
            AIInterviewQuestionRepository questionRepository,
            InterviewEvaluationRepository evaluationRepository,
            CandidateRepository candidateRepository) {

        this.summaryService = summaryService;
        this.summaryRepository = summaryRepository;
        this.questionRepository = questionRepository;
        this.evaluationRepository = evaluationRepository;
        this.candidateRepository = candidateRepository;

        this.objectMapper = new ObjectMapper();
    }

    // =========================================================
    // GENERATE AI INTERVIEW SUMMARY
    // =========================================================

    @PostMapping("/generate/{candidateId}")
    public ResponseEntity<?> generateSummary(
            @PathVariable Long candidateId) {

        try {

            Candidate candidate =
                    candidateRepository.findById(candidateId).orElse(null);

            if (candidate == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Candidate not found.");
            }

            List<?> questions =
                    questionRepository
                            .findByCandidateIdOrderByGeneratedAtDesc(
                                    candidateId);

            if (questions == null || questions.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                "No interview questions found for this candidate."
                        );
            }

            List<InterviewEvaluation> evaluations =
                    evaluationRepository.findByCandidateId(candidateId);

            if (evaluations == null || evaluations.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                "Please evaluate at least one interview question before generating the AI summary."
                        );
            }

            // -------------------------------------------------
            // CALL GEMINI
            // -------------------------------------------------

            String aiResponse =
                    summaryService.generateSummary(candidateId);

            if (aiResponse == null || aiResponse.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("AI returned an empty response.");
            }

            // -------------------------------------------------
            // PARSE GEMINI JSON
            // -------------------------------------------------

            Map<String, Object> data =
                    objectMapper.readValue(
                            aiResponse,
                            new TypeReference<Map<String, Object>>() {
                            }
                    );

            // -------------------------------------------------
            // CREATE SUMMARY
            // -------------------------------------------------

            AIInterviewSummary summary =
                    new AIInterviewSummary();

            summary.setCandidateId(candidateId);

            // -------------------------------------------------
            // CALCULATE EXACT AVERAGE RATING FROM DATABASE
            // -------------------------------------------------

            double ratingTotal = 0;
            int ratingCount = 0;

            for (InterviewEvaluation evaluation : evaluations) {

                if (evaluation.getRating() != null) {
                    ratingTotal += evaluation.getRating();
                    ratingCount++;
                }
            }

            BigDecimal averageRating;

            if (ratingCount == 0) {

                averageRating = BigDecimal.ZERO;

            } else {

                averageRating = BigDecimal
                        .valueOf(ratingTotal / ratingCount)
                        .setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                        );
            }

            summary.setAverageRating(averageRating);

            summary.setEvaluatedQuestions(evaluations.size());

            summary.setTotalQuestions(questions.size());

            // -------------------------------------------------
            // AI GENERATED CONTENT
            // -------------------------------------------------

            summary.setStrengths(
                    listToText(data.get("strengths"))
            );

            summary.setAreasToClarify(
                    listToText(data.get("areasToClarify"))
            );

            summary.setTechnicalSummary(
                    safeObject(data.get("technicalSummary"))
            );

            summary.setResponseSummary(
                    safeObject(data.get("responseSummary"))
            );

            summary.setFollowUpQuestions(
                    listToText(data.get("followUpQuestions"))
            );

            summary.setOverallSummary(
                    safeObject(data.get("overallSummary"))
            );

            summary.setGeneratedAt(LocalDateTime.now());

            // -------------------------------------------------
            // SAVE SUMMARY
            // -------------------------------------------------

            AIInterviewSummary savedSummary =
                    summaryRepository.save(summary);

            return ResponseEntity.ok(savedSummary);

        } catch (Exception e) {

            e.printStackTrace();

            // -------------------------------------------------
            // GEMINI QUOTA ERROR
            // -------------------------------------------------

            if (isQuotaError(e)) {

                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(
                                "Gemini API quota has been exceeded. "
                                        + "Please try again after the quota resets."
                        );
            }

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Failed to generate AI interview summary: "
                                    + e.getMessage()
                    );
        }
    }

    // =========================================================
    // GET LATEST SUMMARY
    // =========================================================

    @GetMapping("/{candidateId}")
    public ResponseEntity<?> getSummary(
            @PathVariable Long candidateId) {

        try {

            return summaryRepository
                    .findTopByCandidateIdOrderByGeneratedAtDesc(
                            candidateId
                    )
                    .<ResponseEntity<?>>map(
                            ResponseEntity::ok
                    )
                    .orElseGet(
                            () -> ResponseEntity
                                    .status(HttpStatus.NOT_FOUND)
                                    .body(
                                            "No AI interview summary found."
                                    )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Failed to load AI interview summary."
                    );
        }
    }

    // =========================================================
    // DELETE SUMMARY
    // =========================================================

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<?> deleteSummary(
            @PathVariable Long candidateId) {

        try {

            summaryRepository.deleteByCandidateId(candidateId);

            return ResponseEntity.ok(
                    "AI interview summary deleted successfully."
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Failed to delete AI interview summary."
                    );
        }
    }

    // =========================================================
    // CONVERT AI JSON ARRAY TO READABLE TEXT
    // =========================================================

    private String listToText(Object value) {

        if (value == null) {
            return "";
        }

        if (value instanceof List<?> list) {

            StringBuilder result =
                    new StringBuilder();

            for (Object item : list) {

                if (item == null) {
                    continue;
                }

                String text =
                        item.toString().trim();

                if (!text.isBlank()) {

                    if (result.length() > 0) {
                        result.append("\n");
                    }

                    result.append("• ")
                            .append(text);
                }
            }

            return result.toString();
        }

        return value.toString();
    }

    // =========================================================
    // SAFE STRING CONVERSION
    // =========================================================

    private String safeObject(Object value) {

        if (value == null) {
            return "";
        }

        return value.toString().trim();
    }

    // =========================================================
    // GEMINI QUOTA ERROR CHECK
    // =========================================================

    private boolean isQuotaError(Throwable throwable) {

        Throwable current = throwable;

        while (current != null) {

            String message =
                    current.getMessage();

            if (message != null) {

                String lower =
                        message.toLowerCase();

                if (lower.contains("429")
                        || lower.contains("resource_exhausted")
                        || lower.contains("quota")
                        || lower.contains("rate limit")) {

                    return true;
                }
            }

            current = current.getCause();
        }

        return false;
    }
}
