package com.vansh.resume_screening;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewEvaluationRepository
        extends JpaRepository<InterviewEvaluation, Long> {

    List<InterviewEvaluation> findByCandidateId(Long candidateId);

    InterviewEvaluation findByQuestionId(Long questionId);

    void deleteByCandidateId(Long candidateId);
}