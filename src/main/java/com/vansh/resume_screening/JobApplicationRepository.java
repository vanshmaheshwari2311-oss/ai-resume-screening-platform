package com.vansh.resume_screening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicantUserIdOrderByAppliedAtDesc(Long applicantUserId);
    boolean existsByApplicantUserIdAndJobId(Long applicantUserId, Long jobId);
    long countByStatusIgnoreCase(String status);
}
