package com.vansh.resume_screening;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AIInterviewSummaryRepository
        extends JpaRepository<AIInterviewSummary, Long> {

    Optional<AIInterviewSummary> findTopByCandidateIdOrderByGeneratedAtDesc(
            Long candidateId
    );

    void deleteByCandidateId(Long candidateId);
}