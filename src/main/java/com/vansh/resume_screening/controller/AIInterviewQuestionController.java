package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vansh.resume_screening.AIInterviewQuestion;
import com.vansh.resume_screening.AIInterviewQuestionRepository;
import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;
import com.vansh.resume_screening.service.AIInterviewQuestionService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/interview-questions")
public class AIInterviewQuestionController {

    private final CandidateRepository candidateRepository;
    private final AIInterviewQuestionRepository questionRepository;
    private final AIInterviewQuestionService questionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIInterviewQuestionController(
            CandidateRepository candidateRepository,
            AIInterviewQuestionRepository questionRepository,
            AIInterviewQuestionService questionService) {

        this.candidateRepository = candidateRepository;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    // ============================================================
    // GENERATE AI INTERVIEW QUESTIONS
    // ============================================================

    @PostMapping("/generate/{candidateId}")
    public ResponseEntity<?> generateQuestions(
            @PathVariable Long candidateId,
            @RequestParam(defaultValue = "MIXED") String questionType,
            @RequestParam(defaultValue = "MEDIUM") String difficulty,
            @RequestParam(defaultValue = "10") int numberOfQuestions) {

        try {

            Candidate candidate =
                    candidateRepository.findById(candidateId)
                            .orElse(null);

            if (candidate == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Candidate not found.");
            }

            if (candidate.getResumeText() == null ||
                    candidate.getResumeText().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                            "Candidate resume text is empty. " +
                            "Please upload/process the resume first."
                        );
            }

            if (numberOfQuestions < 1 || numberOfQuestions > 20) {

                return ResponseEntity
                        .badRequest()
                        .body(
                            "Number of questions must be between 1 and 20."
                        );
            }

            questionType = questionType.toUpperCase();
            difficulty = difficulty.toUpperCase();

            String aiJson =
                    questionService.generateQuestions(
                            candidate.getResumeText(),
                            questionType,
                            difficulty,
                            numberOfQuestions
                    );

            JsonNode root =
                    objectMapper.readTree(aiJson);

            JsonNode questionsNode =
                    root.path("questions");

            if (!questionsNode.isArray()) {

                return ResponseEntity
                        .internalServerError()
                        .body(
                            "AI returned an invalid question format."
                        );
            }

            List<AIInterviewQuestion> savedQuestions =
                    new ArrayList<>();

            for (JsonNode questionNode : questionsNode) {

                AIInterviewQuestion question =
                        new AIInterviewQuestion();

                question.setCandidateId(candidate.getId());

                question.setQuestion(
                        questionNode
                                .path("question")
                                .asText()
                );

                question.setQuestionType(
                        questionNode
                                .path("questionType")
                                .asText()
                );

                question.setDifficulty(
                        questionNode
                                .path("difficulty")
                                .asText()
                );

                question.setReason(
                        questionNode
                                .path("reason")
                                .asText()
                );

                question.setGeneratedAt(
                        LocalDateTime.now()
                );

                // New questions start with empty interviewer notes
                question.setInterviewerNotes("");

                savedQuestions.add(
                        questionRepository.save(question)
                );
            }

            return ResponseEntity.ok(savedQuestions);

        } catch (Exception e) {

            e.printStackTrace();

            if (isQuotaError(e)) {

                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(
                            "Gemini free-tier quota has been reached. " +
                            "Please wait for the quota to reset."
                        );
            }

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "AI interview question generation failed: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // GET QUESTIONS FOR CANDIDATE
    // ============================================================

    @GetMapping("/{candidateId}")
    public ResponseEntity<?> getQuestions(
            @PathVariable Long candidateId) {

        try {

            Candidate candidate =
                    candidateRepository.findById(candidateId)
                            .orElse(null);

            if (candidate == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Candidate not found.");
            }

            List<AIInterviewQuestion> questions =
                    questionRepository
                            .findByCandidateIdOrderByGeneratedAtDesc(
                                    candidateId
                            );

            return ResponseEntity.ok(questions);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to load interview questions: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // SAVE / UPDATE INTERVIEWER NOTES
    // ============================================================

    @PutMapping("/{questionId}/notes")
    public ResponseEntity<?> updateNotes(
            @PathVariable Long questionId,
            @RequestParam String notes) {

        try {

            AIInterviewQuestion question =
                    questionRepository
                            .findById(questionId)
                            .orElse(null);

            if (question == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Interview question not found.");
            }

            question.setInterviewerNotes(notes);

            AIInterviewQuestion savedQuestion =
                    questionRepository.save(question);

            return ResponseEntity.ok(savedQuestion);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to save interviewer notes: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // DELETE ALL QUESTIONS FOR CANDIDATE
    // ============================================================

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<?> deleteQuestions(
            @PathVariable Long candidateId) {

        try {

            questionRepository.deleteByCandidateId(candidateId);

            return ResponseEntity.ok(
                    "Interview questions deleted successfully."
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to delete interview questions: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // GEMINI QUOTA ERROR CHECK
    // ============================================================

    private boolean isQuotaError(Exception e) {

        Throwable current = e;

        while (current != null) {

            String message = current.getMessage();

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

            current = current.getCause();
        }

        return false;
    }
}
