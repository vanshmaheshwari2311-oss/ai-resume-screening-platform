package com.vansh.resume_screening;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * ============================================================
 * INTERVIEW ENTITY
 *
 * Stores interview scheduling information for candidates.
 *
 * Status values:
 *
 * SCHEDULED
 * COMPLETED
 * CANCELLED
 * ============================================================
 */

@Entity
public class Interview {


    /*
     * ============================================================
     * ID
     * ============================================================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * ============================================================
     * CANDIDATE ID
     *
     * We are keeping this as Long for now so it works with
     * your existing Candidate entity without changing the
     * existing database relationships.
     * ============================================================
     */

    private Long candidateId;


    /*
     * ============================================================
     * JOB ID
     *
     * Identifies the job for which the interview is scheduled.
     * ============================================================
     */

    private Long jobId;


    /*
     * ============================================================
     * INTERVIEW DATE
     *
     * Stored as String initially to keep form handling simple.
     *
     * Example:
     * 2026-08-25
     * ============================================================
     */

    private String interviewDate;


    /*
     * ============================================================
     * INTERVIEW TIME
     *
     * Example:
     * 10:30
     * ============================================================
     */

    private String interviewTime;


    /*
     * ============================================================
     * INTERVIEW MODE
     *
     * Examples:
     *
     * Online
     * Offline
     * Phone
     * ============================================================
     */

    private String interviewMode;


    /*
     * ============================================================
     * MEETING LINK
     *
     * Used for online interviews.
     * ============================================================
     */

    private String meetingLink;


    /*
     * ============================================================
     * INTERVIEWER
     * ============================================================
     */

    private String interviewer;


    /*
     * ============================================================
     * NOTES
     * ============================================================
     */

    private String notes;


    /*
     * ============================================================
     * STATUS
     *
     * Default:
     * SCHEDULED
     * ============================================================
     */

    private String status = "SCHEDULED";


    /*
     * ============================================================
     * DEFAULT CONSTRUCTOR
     *
     * Required by JPA.
     * ============================================================
     */

    public Interview() {
    }


    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

    public Interview(
            Long candidateId,
            Long jobId,
            String interviewDate,
            String interviewTime,
            String interviewMode,
            String meetingLink,
            String interviewer,
            String notes) {

        this.candidateId = candidateId;
        this.jobId = jobId;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.interviewMode = interviewMode;
        this.meetingLink = meetingLink;
        this.interviewer = interviewer;
        this.notes = notes;

        this.status = "SCHEDULED";
    }


    /*
     * ============================================================
     * ID
     * ============================================================
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    /*
     * ============================================================
     * CANDIDATE ID
     * ============================================================
     */

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }


    /*
     * ============================================================
     * JOB ID
     * ============================================================
     */

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }


    /*
     * ============================================================
     * INTERVIEW DATE
     * ============================================================
     */

    public String getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(String interviewDate) {
        this.interviewDate = interviewDate;
    }


    /*
     * ============================================================
     * INTERVIEW TIME
     * ============================================================
     */

    public String getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
    }


    /*
     * ============================================================
     * INTERVIEW MODE
     * ============================================================
     */

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }


    /*
     * ============================================================
     * MEETING LINK
     * ============================================================
     */

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }


    /*
     * ============================================================
     * INTERVIEWER
     * ============================================================
     */

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }


    /*
     * ============================================================
     * NOTES
     * ============================================================
     */

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    /*
     * ============================================================
     * STATUS
     * ============================================================
     */

    public String getStatus() {

        if (status == null || status.isBlank()) {
            return "SCHEDULED";
        }

        return status;
    }


    public void setStatus(String status) {

        if (status == null || status.isBlank()) {

            this.status = "SCHEDULED";

        } else {

            this.status = status.toUpperCase();
        }
    }


    /*
     * ============================================================
     * HELPER METHODS
     * ============================================================
     */

    public boolean isScheduled() {

        return "SCHEDULED".equalsIgnoreCase(getStatus());
    }


    public boolean isCompleted() {

        return "COMPLETED".equalsIgnoreCase(getStatus());
    }


    public boolean isCancelled() {

        return "CANCELLED".equalsIgnoreCase(getStatus());
    }
}