package com.vansh.resume_screening;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    /*
     * Find interviews for a specific candidate.
     */
    List<Interview> findByCandidateId(Long candidateId);


    /*
     * Find interviews for a specific job.
     */
    List<Interview> findByJobId(Long jobId);


    /*
     * Find interviews by status.
     *
     * Example:
     * SCHEDULED
     * COMPLETED
     * CANCELLED
     */
    List<Interview> findByStatus(String status);
}