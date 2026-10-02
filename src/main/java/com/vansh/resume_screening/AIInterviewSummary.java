package com.vansh.resume_screening;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_interview_summary")
public class AIInterviewSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "evaluated_questions")
    private Integer evaluatedQuestions;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Lob
    @Column(name = "strengths")
    private String strengths;

    @Lob
    @Column(name = "areas_to_clarify")
    private String areasToClarify;

    @Lob
    @Column(name = "technical_summary")
    private String technicalSummary;

    @Lob
    @Column(name = "response_summary")
    private String responseSummary;

    @Lob
    @Column(name = "follow_up_questions")
    private String followUpQuestions;

    @Lob
    @Column(name = "overall_summary")
    private String overallSummary;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    public AIInterviewSummary() {
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getEvaluatedQuestions() {
        return evaluatedQuestions;
    }

    public void setEvaluatedQuestions(Integer evaluatedQuestions) {
        this.evaluatedQuestions = evaluatedQuestions;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getAreasToClarify() {
        return areasToClarify;
    }

    public void setAreasToClarify(String areasToClarify) {
        this.areasToClarify = areasToClarify;
    }

    public String getTechnicalSummary() {
        return technicalSummary;
    }

    public void setTechnicalSummary(String technicalSummary) {
        this.technicalSummary = technicalSummary;
    }

    public String getResponseSummary() {
        return responseSummary;
    }

    public void setResponseSummary(String responseSummary) {
        this.responseSummary = responseSummary;
    }

    public String getFollowUpQuestions() {
        return followUpQuestions;
    }

    public void setFollowUpQuestions(String followUpQuestions) {
        this.followUpQuestions = followUpQuestions;
    }

    public String getOverallSummary() {
        return overallSummary;
    }

    public void setOverallSummary(String overallSummary) {
        this.overallSummary = overallSummary;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}