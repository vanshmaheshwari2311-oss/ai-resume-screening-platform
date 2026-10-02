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
public class InterviewController {

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;


    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

    public InterviewController(
            InterviewRepository interviewRepository,
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            EmailService emailService) {

        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }


    /*
     * ============================================================
     * INTERVIEW MANAGEMENT PAGE
     *
     * URL:
     * http://localhost:8080/interviews
     * ============================================================
     */

    @GetMapping("/interviews")
    public String interviews(Model model) {

        model.addAttribute(
                "interviews",
                interviewRepository.findAll()
        );

        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );

        model.addAttribute(
                "jobs",
                jobRepository.findAll()
        );

        return "interviews";
    }


    /*
     * ============================================================
     * SCHEDULE INTERVIEW
     *
     * URL:
     * POST /interviews/save
     *
     * FLOW:
     *
     * Validate candidate
     *        ↓
     * Validate job
     *        ↓
     * Create interview
     *        ↓
     * Save interview
     *        ↓
     * Send email
     *        ↓
     * Redirect
     * ============================================================
     */

    @PostMapping("/interviews/save")
    public String saveInterview(

            @RequestParam("candidateId")
            Long candidateId,

            @RequestParam("jobId")
            Long jobId,

            @RequestParam("interviewDate")
            String interviewDate,

            @RequestParam("interviewTime")
            String interviewTime,

            @RequestParam("interviewMode")
            String interviewMode,

            @RequestParam(
                    value = "meetingLink",
                    required = false)
            String meetingLink,

            @RequestParam("interviewer")
            String interviewer,

            @RequestParam(
                    value = "notes",
                    required = false)
            String notes) {


        /*
         * ========================================================
         * FIND CANDIDATE
         * ========================================================
         */

        Candidate candidate =
                candidateRepository
                        .findById(candidateId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found with ID: "
                                                + candidateId
                                )
                        );


        /*
         * ========================================================
         * FIND JOB
         * ========================================================
         */

        Job job =
                jobRepository
                        .findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found with ID: "
                                                + jobId
                                )
                        );


        /*
         * ========================================================
         * CREATE INTERVIEW
         * ========================================================
         */

        Interview interview =
                new Interview(
                        candidateId,
                        jobId,
                        interviewDate,
                        interviewTime,
                        interviewMode,
                        meetingLink,
                        interviewer,
                        notes
                );


        /*
         * ========================================================
         * NEW INTERVIEW = SCHEDULED
         * ========================================================
         */

        interview.setStatus("SCHEDULED");


        /*
         * ========================================================
         * SAVE INTERVIEW
         * ========================================================
         */

        interviewRepository.save(interview);


        /*
         * ========================================================
         * SEND INTERVIEW EMAIL
         *
         * The EmailService receives the saved Interview.
         *
         * It uses:
         *
         * candidateId → CandidateRepository
         * jobId       → JobRepository
         *
         * ========================================================
         */

        try {

            emailService.sendInterviewScheduledEmail(
                    interview
            );

        } catch (Exception e) {

            /*
             * Email failure should NOT delete the interview.
             *
             * The interview has already been successfully
             * saved in the database.
             */

            System.out.println(
                    "Interview saved, but email could not be sent."
            );

            System.out.println(
                    "Email error: "
                            + e.getMessage()
            );
        }


        /*
         * ========================================================
         * REDIRECT
         * ========================================================
         */

        return "redirect:/interviews";
    }


    /*
     * ============================================================
     * EDIT INTERVIEW
     *
     * URL:
     * /interviews/edit/{id}
     * ============================================================
     */

    @GetMapping("/interviews/edit/{id}")
    public String editInterview(
            @PathVariable("id") Long id,
            Model model) {

        Interview interview =
                interviewRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        model.addAttribute(
                "interview",
                interview
        );

        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );

        model.addAttribute(
                "jobs",
                jobRepository.findAll()
        );

        return "interview-edit";
    }


    /*
     * ============================================================
     * UPDATE INTERVIEW
     *
     * URL:
     * POST /interviews/update
     * ============================================================
     */

    @PostMapping("/interviews/update")
    public String updateInterview(

            @RequestParam("id")
            Long id,

            @RequestParam("candidateId")
            Long candidateId,

            @RequestParam("jobId")
            Long jobId,

            @RequestParam("interviewDate")
            String interviewDate,

            @RequestParam("interviewTime")
            String interviewTime,

            @RequestParam("interviewMode")
            String interviewMode,

            @RequestParam(
                    value = "meetingLink",
                    required = false)
            String meetingLink,

            @RequestParam("interviewer")
            String interviewer,

            @RequestParam(
                    value = "notes",
                    required = false)
            String notes) {


        /*
         * ========================================================
         * FIND INTERVIEW
         * ========================================================
         */

        Interview interview =
                interviewRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );


        /*
         * ========================================================
         * VERIFY CANDIDATE
         * ========================================================
         */

        if (!candidateRepository.existsById(candidateId)) {

            throw new IllegalArgumentException(
                    "Candidate not found with ID: "
                            + candidateId
            );
        }


        /*
         * ========================================================
         * VERIFY JOB
         * ========================================================
         */

        if (!jobRepository.existsById(jobId)) {

            throw new IllegalArgumentException(
                    "Job not found with ID: "
                            + jobId
            );
        }


        /*
         * ========================================================
         * UPDATE FIELDS
         * ========================================================
         */

        interview.setCandidateId(candidateId);

        interview.setJobId(jobId);

        interview.setInterviewDate(
                interviewDate
        );

        interview.setInterviewTime(
                interviewTime
        );

        interview.setInterviewMode(
                interviewMode
        );

        interview.setMeetingLink(
                meetingLink
        );

        interview.setInterviewer(
                interviewer
        );

        interview.setNotes(
                notes
        );


        /*
         * ========================================================
         * SAVE UPDATED INTERVIEW
         * ========================================================
         */

        interviewRepository.save(interview);


        return "redirect:/interviews";
    }


    /*
     * ============================================================
     * MARK INTERVIEW COMPLETED
     * ============================================================
     */

    @PostMapping("/interviews/complete/{id}")
    public String completeInterview(
            @PathVariable("id") Long id) {

        Interview interview =
                interviewRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        interview.setStatus("COMPLETED");

        interviewRepository.save(interview);

        return "redirect:/interviews";
    }


    /*
     * ============================================================
     * CANCEL INTERVIEW
     * ============================================================
     */

    @PostMapping("/interviews/cancel/{id}")
    public String cancelInterview(
            @PathVariable("id") Long id) {

        Interview interview =
                interviewRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        interview.setStatus("CANCELLED");

        interviewRepository.save(interview);

        return "redirect:/interviews";
    }


    /*
     * ============================================================
     * DELETE INTERVIEW
     * ============================================================
     */

    @PostMapping("/interviews/delete/{id}")
    public String deleteInterview(
            @PathVariable("id") Long id) {

        Interview interview =
                interviewRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        interviewRepository.delete(interview);

        return "redirect:/interviews";
    }
}
