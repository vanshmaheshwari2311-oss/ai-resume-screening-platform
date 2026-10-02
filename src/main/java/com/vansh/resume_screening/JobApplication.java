package com.vansh.resume_screening;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "job_application",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_job_application_user_job",
        columnNames = {"applicant_user_id", "job_id"}
    )
)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_user_id", nullable = false)
    private ApplicantUser applicantUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // Candidate record used by the existing recruiter/AI screening workflow.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @Column(nullable = false, length = 30)
    private String status = "SUBMITTED";

    private LocalDateTime appliedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ApplicantUser getApplicantUser() { return applicantUser; }
    public void setApplicantUser(ApplicantUser applicantUser) { this.applicantUser = applicantUser; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status == null || status.isBlank() ? "SUBMITTED" : status.toUpperCase();
    }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
