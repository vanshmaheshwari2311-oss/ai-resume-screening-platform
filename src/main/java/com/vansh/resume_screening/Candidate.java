package com.vansh.resume_screening;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String skills;

    /* The job selected during the public application flow. */
    private Long jobId;

    private String resumeFileName;

    /*
     * ============================================================
     * RESUME CONTENT TYPE
     *
     * Example:
     * application/pdf
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document
     * ============================================================
     */
    private String resumeContentType;

    /*
     * ============================================================
     * ACTUAL RESUME FILE
     *
     * The uploaded PDF/DOC/DOCX file is stored here.
     * ============================================================
     */
    @Lob
    private byte[] resumeFile;

    /*
     * ============================================================
     * EXTRACTED RESUME TEXT
     * ============================================================
     */
    @Lob
    private String resumeText;

    /*
     * ============================================================
     * CANDIDATE STATUS
     *
     * PENDING
     * SHORTLISTED
     * REJECTED
     * ============================================================
     */
    private String status = "PENDING";


    /*
     * ============================================================
     * DEFAULT CONSTRUCTOR
     * Required by JPA
     * ============================================================
     */
    public Candidate() {
    }


    /*
     * ============================================================
     * EXISTING CONSTRUCTOR
     * ============================================================
     */
    public Candidate(
            String name,
            String email,
            String phone,
            String skills,
            String resumeFileName) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.resumeFileName = resumeFileName;
        this.status = "PENDING";
    }


    /*
     * ============================================================
     * CONSTRUCTOR WITH RESUME TEXT
     * ============================================================
     */
    public Candidate(
            String name,
            String email,
            String phone,
            String skills,
            String resumeFileName,
            String resumeText) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.resumeFileName = resumeFileName;
        this.resumeText = resumeText;
        this.status = "PENDING";
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
     * NAME
     * ============================================================
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /*
     * ============================================================
     * EMAIL
     * ============================================================
     */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    /*
     * ============================================================
     * PHONE
     * ============================================================
     */

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    /*
     * ============================================================
     * SKILLS
     * ============================================================
     */

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }


    /*
     * ============================================================
     * RESUME FILE NAME
     * ============================================================
     */

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }


    /*
     * ============================================================
     * RESUME CONTENT TYPE
     * ============================================================
     */

    public String getResumeContentType() {
        return resumeContentType;
    }

    public void setResumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
    }


    /*
     * ============================================================
     * RESUME FILE
     * ============================================================
     */

    public byte[] getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(byte[] resumeFile) {
        this.resumeFile = resumeFile;
    }


    /*
     * ============================================================
     * RESUME TEXT
     * ============================================================
     */

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }


    /*
     * ============================================================
     * STATUS
     * ============================================================
     */

    public String getStatus() {

        /*
         * Existing database records may have NULL status.
         * Treat NULL/blank as PENDING.
         */

        if (status == null || status.isBlank()) {
            return "PENDING";
        }

        return status;
    }


    public void setStatus(String status) {

        if (status == null || status.isBlank()) {

            this.status = "PENDING";

        } else {

            this.status = status.toUpperCase();
        }
    }


    /*
     * ============================================================
     * HELPER METHODS
     * ============================================================
     */

    public boolean isPending() {

        return "PENDING".equalsIgnoreCase(getStatus());
    }


    public boolean isShortlisted() {

        return "SHORTLISTED".equalsIgnoreCase(getStatus());
    }


    public boolean isRejected() {

        return "REJECTED".equalsIgnoreCase(getStatus());
    }


    /*
     * ============================================================
     * RESUME CHECK
     * ============================================================
     */

    public boolean hasResume() {

        return resumeFile != null
                && resumeFile.length > 0;
    }
}
