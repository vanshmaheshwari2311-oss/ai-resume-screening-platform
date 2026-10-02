package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vansh.resume_screening.AIInterviewQuestion;
import com.vansh.resume_screening.AIInterviewQuestionRepository;
import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;
import com.vansh.resume_screening.InterviewEvaluation;
import com.vansh.resume_screening.InterviewEvaluationRepository;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/interview-evaluation")
public class InterviewEvaluationController {

    private final InterviewEvaluationRepository evaluationRepository;
    private final AIInterviewQuestionRepository questionRepository;
    private final CandidateRepository candidateRepository;

    public InterviewEvaluationController(
            InterviewEvaluationRepository evaluationRepository,
            AIInterviewQuestionRepository questionRepository,
            CandidateRepository candidateRepository) {

        this.evaluationRepository = evaluationRepository;
        this.questionRepository = questionRepository;
        this.candidateRepository = candidateRepository;
    }

    // ============================================================
    // SAVE / UPDATE EVALUATION
    // ============================================================

    @PostMapping("/{questionId}")
    public ResponseEntity<?> saveEvaluation(
            @PathVariable Long questionId,
            @RequestParam String candidateAnswer,
            @RequestParam Integer rating,
            @RequestParam String interviewerFeedback) {

        try {

            if (rating == null || rating < 1 || rating > 5) {

                return ResponseEntity
                        .badRequest()
                        .body("Rating must be between 1 and 5.");
            }

            AIInterviewQuestion question =
                    questionRepository
                            .findById(questionId)
                            .orElse(null);

            if (question == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Interview question not found.");
            }

            Long candidateId = question.getCandidateId();

            Candidate candidate =
                    candidateRepository
                            .findById(candidateId)
                            .orElse(null);

            if (candidate == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Candidate not found.");
            }

            InterviewEvaluation evaluation =
                    evaluationRepository
                            .findByQuestionId(questionId);

            if (evaluation == null) {

                evaluation =
                        new InterviewEvaluation();

                evaluation.setQuestionId(questionId);
                evaluation.setCandidateId(candidateId);
            }

            evaluation.setCandidateAnswer(candidateAnswer);
            evaluation.setRating(rating);
            evaluation.setInterviewerFeedback(
                    interviewerFeedback
            );
            evaluation.setEvaluatedAt(
                    LocalDateTime.now()
            );

            InterviewEvaluation saved =
                    evaluationRepository.save(evaluation);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to save interview evaluation: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // GET EVALUATION FOR ONE QUESTION
    // ============================================================

    @GetMapping("/question/{questionId}")
    public ResponseEntity<?> getQuestionEvaluation(
            @PathVariable Long questionId) {

        try {

            InterviewEvaluation evaluation =
                    evaluationRepository
                            .findByQuestionId(questionId);

            if (evaluation == null) {

                return ResponseEntity.ok(null);
            }

            return ResponseEntity.ok(evaluation);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to load evaluation: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // GET ALL EVALUATIONS FOR CANDIDATE
    // ============================================================

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getCandidateEvaluations(
            @PathVariable Long candidateId) {

        try {

            Candidate candidate =
                    candidateRepository
                            .findById(candidateId)
                            .orElse(null);

            if (candidate == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Candidate not found.");
            }

            List<InterviewEvaluation> evaluations =
                    evaluationRepository
                            .findByCandidateId(candidateId);

            return ResponseEntity.ok(evaluations);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to load candidate evaluations: "
                        + e.getMessage()
                    );
        }
    }

    // ============================================================
    // DELETE ALL EVALUATIONS FOR CANDIDATE
    // ============================================================

    @DeleteMapping("/candidate/{candidateId}")
    public ResponseEntity<?> deleteCandidateEvaluations(
            @PathVariable Long candidateId) {

        try {

            evaluationRepository.deleteByCandidateId(candidateId);

            return ResponseEntity.ok(
                    "Interview evaluations deleted successfully."
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        "Failed to delete evaluations: "
                        + e.getMessage()
                    );
        }
    }
}
