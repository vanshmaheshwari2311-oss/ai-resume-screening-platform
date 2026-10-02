package com.vansh.resume_screening;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AIInterviewQuestionRepository
        extends JpaRepository<AIInterviewQuestion, Long> {

    List<AIInterviewQuestion> findByCandidateIdOrderByGeneratedAtDesc(
            Long candidateId
    );

    void deleteByCandidateId(Long candidateId);
}