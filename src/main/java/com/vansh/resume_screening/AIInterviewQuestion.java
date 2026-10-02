package com.vansh.resume_screening;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_interview_question")
public class AIInterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Lob
    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "difficulty")
    private String difficulty;

    @Lob
    @Column(name = "reason")
    private String reason;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    // Interviewer notes
    @Lob
    @Column(name = "interviewer_notes")
    private String interviewerNotes;

    public AIInterviewQuestion() {
    }

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getInterviewerNotes() {
        return interviewerNotes;
    }

    public void setInterviewerNotes(String interviewerNotes) {
        this.interviewerNotes = interviewerNotes;
    }
}