package com.vansh.resume_screening;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Job {

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
     * JOB TITLE
     * ============================================================
     */

    private String title;


    /*
     * ============================================================
     * JOB DESCRIPTION
     * ============================================================
     */

    private String description;


    /*
     * ============================================================
     * REQUIRED SKILLS
     * ============================================================
     */

    private String requiredSkills;


    /*
     * ============================================================
     * PREFERRED SKILLS
     * ============================================================
     */

    private String preferredSkills;


    /*
     * ============================================================
     * EXPERIENCE
     *
     * Keep this as String because your existing JobController
     * already sends experience as String.
     * ============================================================
     */

    private String experience;

    /* Public-facing job information. These nullable fields keep existing jobs compatible. */
    private String recruiterName;

    private String recruiterEmail;

    private String location;


    /*
     * ============================================================
     * JOB STATUS
     *
     * ACTIVE
     * INACTIVE
     * ============================================================
     */

    private String status = "ACTIVE";


    /*
     * ============================================================
     * DEFAULT CONSTRUCTOR
     * Required by JPA
     * ============================================================
     */

    public Job() {
    }


    /*
     * ============================================================
     * EXISTING CONSTRUCTOR
     *
     * This matches your current JobController.
     * ============================================================
     */

    public Job(
            String title,
            String description,
            String requiredSkills,
            String preferredSkills,
            String experience) {

        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.preferredSkills = preferredSkills;
        this.experience = experience;

        this.status = "ACTIVE";
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
     * TITLE
     * ============================================================
     */

    public String getTitle() {

        return title;
    }


    public void setTitle(String title) {

        this.title = title;
    }


    /*
     * ============================================================
     * DESCRIPTION
     * ============================================================
     */

    public String getDescription() {

        return description;
    }


    public void setDescription(String description) {

        this.description = description;
    }


    /*
     * ============================================================
     * REQUIRED SKILLS
     * ============================================================
     */

    public String getRequiredSkills() {

        return requiredSkills;
    }


    public void setRequiredSkills(String requiredSkills) {

        this.requiredSkills = requiredSkills;
    }


    /*
     * ============================================================
     * PREFERRED SKILLS
     * ============================================================
     */

    public String getPreferredSkills() {

        return preferredSkills;
    }


    public void setPreferredSkills(String preferredSkills) {

        this.preferredSkills = preferredSkills;
    }


    /*
     * ============================================================
     * EXPERIENCE
     * ============================================================
     */

    public String getExperience() {

        return experience;
    }


    public void setExperience(String experience) {

        this.experience = experience;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    /*
     * ============================================================
     * STATUS
     * ============================================================
     */

    public String getStatus() {

        /*
         * Existing jobs may have NULL status because
         * this field was added later.
         *
         * Treat NULL as ACTIVE.
         */

        if (status == null || status.isBlank()) {

            return "ACTIVE";
        }

        return status;
    }


    public void setStatus(String status) {

        if (status == null || status.isBlank()) {

            this.status = "ACTIVE";

        } else {

            this.status =
                    status.toUpperCase();
        }
    }


    /*
     * ============================================================
     * HELPER METHODS
     * ============================================================
     */

    public boolean isActive() {

        return "ACTIVE".equalsIgnoreCase(
                getStatus()
        );
    }


    public boolean isInactive() {

        return "INACTIVE".equalsIgnoreCase(
                getStatus()
        );
    }

}
