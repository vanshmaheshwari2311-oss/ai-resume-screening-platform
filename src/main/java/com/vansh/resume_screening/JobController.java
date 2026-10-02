package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }


    /*
     * ============================================================
     * SHOW JOB CREATION PAGE
     *
     * URL:
     * http://localhost:8080/jobs
     * ============================================================
     */
    @GetMapping("/jobs")
    public String jobs(Model model) {

        model.addAttribute(
                "jobs",
                jobRepository.findAll()
        );

        return "jobs";
    }


    /*
     * ============================================================
     * SAVE NEW JOB
     *
     * URL:
     * POST /jobs/save
     * ============================================================
     */
    @PostMapping("/jobs/save")
    public String saveJob(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("requiredSkills") String requiredSkills,
            @RequestParam("preferredSkills") String preferredSkills,
            @RequestParam("experience") String experience,
            @RequestParam(value = "recruiterName", required = false) String recruiterName,
            @RequestParam(value = "recruiterEmail", required = false) String recruiterEmail,
            @RequestParam(value = "location", required = false) String location) {

        Job job = new Job(
                title,
                description,
                requiredSkills,
                preferredSkills,
                experience
        );

        /*
         * New jobs are ACTIVE by default.
         */
        job.setStatus("ACTIVE");
        job.setRecruiterName(recruiterName);
        job.setRecruiterEmail(recruiterEmail);
        job.setLocation(location);

        jobRepository.save(job);

        return "redirect:/job-management";
    }


    /*
     * ============================================================
     * VIEW JOB
     *
     * URL:
     * /jobs/view/{id}
     *
     * Example:
     * /jobs/view/1
     * ============================================================
     */
    @GetMapping("/jobs/view/{id}")
    public String viewJob(
            @PathVariable("id") Long id,
            Model model) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: " + id
                                )
                        );

        model.addAttribute(
                "job",
                job
        );

        return "job-view";
    }


    /*
     * ============================================================
     * SHOW EDIT JOB PAGE
     *
     * URL:
     * /jobs/edit/{id}
     *
     * Example:
     * /jobs/edit/1
     * ============================================================
     */
    @GetMapping("/jobs/edit/{id}")
    public String editJob(
            @PathVariable("id") Long id,
            Model model) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: " + id
                                )
                        );

        model.addAttribute(
                "job",
                job
        );

        return "job-edit";
    }


    /*
     * ============================================================
     * UPDATE JOB
     *
     * URL:
     * POST /jobs/update
     * ============================================================
     */
    @PostMapping("/jobs/update")
    public String updateJob(
            @RequestParam("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("requiredSkills") String requiredSkills,
            @RequestParam("preferredSkills") String preferredSkills,
            @RequestParam("experience") String experience,
            @RequestParam(value = "recruiterName", required = false) String recruiterName,
            @RequestParam(value = "recruiterEmail", required = false) String recruiterEmail,
            @RequestParam(value = "location", required = false) String location) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: " + id
                                )
                        );


        /*
         * Update existing job fields.
         */
        job.setTitle(title);

        job.setDescription(description);

        job.setRequiredSkills(requiredSkills);

        job.setPreferredSkills(preferredSkills);

        job.setExperience(experience);
        job.setRecruiterName(recruiterName);
        job.setRecruiterEmail(recruiterEmail);
        job.setLocation(location);


        /*
         * Keep the existing ACTIVE/INACTIVE status.
         */
        jobRepository.save(job);


        return "redirect:/job-management";
    }


    /*
     * ============================================================
     * ACTIVATE JOB
     *
     * URL:
     * POST /jobs/activate/{id}
     * ============================================================
     */
    @PostMapping("/jobs/activate/{id}")
    public String activateJob(
            @PathVariable("id") Long id) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: " + id
                                )
                        );

        job.setStatus("ACTIVE");

        jobRepository.save(job);

        return "redirect:/job-management";
    }


    /*
     * ============================================================
     * DEACTIVATE JOB
     *
     * URL:
     * POST /jobs/deactivate/{id}
     * ============================================================
     */
    @PostMapping("/jobs/deactivate/{id}")
    public String deactivateJob(
            @PathVariable("id") Long id) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: " + id
                                )
                        );

        job.setStatus("INACTIVE");

        jobRepository.save(job);

        return "redirect:/job-management";
    }


    /*
     * ============================================================
     * DELETE JOB
     *
     * URL:
     * POST /jobs/delete/{id}
     * ============================================================
     */
    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(
            @PathVariable("id") Long id) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: " + id
                                )
                        );

        jobRepository.delete(job);

        return "redirect:/job-management";
    }
}
