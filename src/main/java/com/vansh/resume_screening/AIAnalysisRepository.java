package com.vansh.resume_screening;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AIAnalysisRepository
        extends JpaRepository<AIAnalysis, Long> {

    List<AIAnalysis> findByCandidateId(Long candidateId);

    List<AIAnalysis> findByJobId(Long jobId);

    AIAnalysis findTopByCandidateIdAndJobIdOrderByAnalyzedAtDesc(
            Long candidateId,
            Long jobId
    );
}