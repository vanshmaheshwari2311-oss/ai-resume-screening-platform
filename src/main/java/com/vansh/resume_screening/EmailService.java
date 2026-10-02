package com.vansh.resume_screening;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * ============================================================
 * EMAIL SERVICE
 *
 * Handles email notifications for:
 *
 * 1. Candidate shortlisted
 * 2. Candidate rejected
 * 3. Interview scheduled
 * ============================================================
 */

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final CandidateRepository candidateRepository;

    private final JobRepository jobRepository;


    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

    public EmailService(
            JavaMailSender mailSender,
            CandidateRepository candidateRepository,
            JobRepository jobRepository) {

        this.mailSender = mailSender;
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
    }


    /*
     * ============================================================
     * GENERIC SEND EMAIL
     * ============================================================
     */

    public void sendEmail(
            String to,
            String subject,
            String message) {

        if (to == null || to.isBlank()) {

            System.out.println(
                    "Email not sent: recipient email is empty."
            );

            return;
        }

        try {

            SimpleMailMessage mail =
                    new SimpleMailMessage();

            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);

            mailSender.send(mail);

            System.out.println(
                    "Email sent successfully to: " + to
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to send email to: " + to
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );
        }
    }


    /*
     * ============================================================
     * SHORTLISTED EMAIL
     * ============================================================
     */

    public void sendShortlistedEmail(
            Candidate candidate,
            Job job) {

        if (candidate == null || job == null) {
            return;
        }

        String subject =
                "Congratulations! You have been shortlisted";

        String message =
                "Dear " + candidate.getName() + ",\n\n"
                + "Congratulations!\n\n"
                + "We are pleased to inform you that you "
                + "have been shortlisted for the following position:\n\n"
                + "Job Position: " + job.getTitle() + "\n\n"
                + "Your resume has successfully passed "
                + "the initial screening process.\n\n"
                + "Our recruitment team will contact you "
                + "with the next steps.\n\n"
                + "Regards,\n"
                + "AI Resume Screening Team";

        sendEmail(
                candidate.getEmail(),
                subject,
                message
        );
    }


    /*
     * ============================================================
     * REJECTED EMAIL
     * ============================================================
     */

    public void sendRejectedEmail(
            Candidate candidate,
            Job job) {

        if (candidate == null || job == null) {
            return;
        }

        String subject =
                "Application Update - " + job.getTitle();

        String message =
                "Dear " + candidate.getName() + ",\n\n"
                + "Thank you for your interest in the position "
                + job.getTitle() + ".\n\n"
                + "After reviewing your application, "
                + "we regret to inform you that your application "
                + "has not been selected for the next stage "
                + "of the recruitment process.\n\n"
                + "We appreciate the time and effort you "
                + "put into your application and wish you "
                + "the very best in your future career.\n\n"
                + "Regards,\n"
                + "AI Resume Screening Team";

        sendEmail(
                candidate.getEmail(),
                subject,
                message
        );
    }


    /*
     * ============================================================
     * INTERVIEW SCHEDULED EMAIL
     *
     * IMPORTANT:
     *
     * Interview stores candidateId and jobId.
     *
     * It does NOT store Candidate or Job objects.
     *
     * Therefore:
     *
     * candidateId -> CandidateRepository
     * jobId       -> JobRepository
     * ============================================================
     */

    public void sendInterviewScheduledEmail(
            Interview interview) {

        if (interview == null) {
            return;
        }


        /*
         * ========================================================
         * FIND CANDIDATE
         * ========================================================
         */

        Candidate candidate =
                candidateRepository
                        .findById(
                                interview.getCandidateId()
                        )
                        .orElse(null);


        /*
         * ========================================================
         * FIND JOB
         * ========================================================
         */

        Job job =
                jobRepository
                        .findById(
                                interview.getJobId()
                        )
                        .orElse(null);


        if (candidate == null) {

            System.out.println(
                    "Interview email not sent: candidate not found."
            );

            return;
        }


        if (job == null) {

            System.out.println(
                    "Interview email not sent: job not found."
            );

            return;
        }


        /*
         * ========================================================
         * EMAIL SUBJECT
         * ========================================================
         */

        String subject =
                "Interview Scheduled - " + job.getTitle();


        /*
         * ========================================================
         * EMAIL MESSAGE
         * ========================================================
         */

        String message =
                "Dear " + candidate.getName() + ",\n\n"

                + "Your interview has been successfully scheduled.\n\n"

                + "Interview Details\n"
                + "========================\n\n"

                + "Job Position: "
                + job.getTitle()
                + "\n\n"

                + "Interview Date: "
                + interview.getInterviewDate()
                + "\n\n"

                + "Interview Time: "
                + interview.getInterviewTime()
                + "\n\n"

                + "Interview Mode: "
                + interview.getInterviewMode()
                + "\n\n"

                + "Interviewer: "
                + interview.getInterviewer()
                + "\n\n";


        /*
         * ========================================================
         * MEETING LINK
         * ========================================================
         */

        if (interview.getMeetingLink() != null
                && !interview.getMeetingLink().isBlank()) {

            message +=
                    "Meeting Link: "
                    + interview.getMeetingLink()
                    + "\n\n";
        }


        /*
         * ========================================================
         * NOTES
         * ========================================================
         */

        if (interview.getNotes() != null
                && !interview.getNotes().isBlank()) {

            message +=
                    "Interview Notes:\n"
                    + interview.getNotes()
                    + "\n\n";
        }


        /*
         * ========================================================
         * FINAL MESSAGE
         * ========================================================
         */

        message +=
                "Please be available at the scheduled "
                + "date and time.\n\n"

                + "We look forward to speaking with you.\n\n"

                + "Regards,\n"
                + "AI Resume Screening Team";


        /*
         * ========================================================
         * SEND
         * ========================================================
         */

        sendEmail(
                candidate.getEmail(),
                subject,
                message
        );
    }
}