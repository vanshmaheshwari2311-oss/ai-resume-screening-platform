package com.vansh.resume_screening;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateApplicationController {

    private final ApplicantUserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ResumeTextExtractor resumeTextExtractor;

    public CandidateApplicationController(
            ApplicantUserRepository userRepository,
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            JobApplicationRepository applicationRepository,
            ResumeTextExtractor resumeTextExtractor) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.resumeTextExtractor = resumeTextExtractor;
    }

    @GetMapping("/apply")
    public String applicationPage(
            @RequestParam(value = "jobId", required = false) Long jobId,
            Authentication authentication,
            Model model) {

        ApplicantUser user = currentUser(authentication);

        model.addAttribute("user", user);
        model.addAttribute(
                "jobs",
                jobRepository.findByStatusIgnoreCaseOrderByIdDesc("ACTIVE")
        );
        model.addAttribute("selectedJobId", jobId);

        return "candidate-apply";
    }

    @PostMapping("/apply/submit")
    public String submitApplication(
            @RequestParam("jobId") Long jobId,
            @RequestParam("skills") String skills,
            @RequestParam("resume") MultipartFile resume,
            Authentication authentication,
            Model model) {

        ApplicantUser user = currentUser(authentication);

        if (jobId == null) {
            return showError(model, "Please select a job.", jobId, user);
        }

        if (skills == null || skills.isBlank()) {
            return showError(model, "Please enter your skills.", jobId, user);
        }

        if (resume == null || resume.isEmpty()) {
            return showError(model, "Please upload your resume.", jobId, user);
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null || !job.isActive()) {
            return showError(model, "Selected job is not available.", jobId, user);
        }

        if (applicationRepository.existsByApplicantUserIdAndJobId(user.getId(), jobId)) {
            return showError(model, "You have already applied for this job.", jobId, user);
        }

        String resumeText;
        try {
            resumeText = resumeTextExtractor.extractText(resume);
        } catch (Exception e) {
            return showError(model, "Unable to read the uploaded resume. Please upload a valid PDF/DOC/DOCX file.", jobId, user);
        }

        // This creates the recruiter-visible candidate record through the server.
        // The candidate user has no endpoint that can edit/delete this record.
        Candidate candidate = new Candidate();
        candidate.setName(user.getFullName());
        candidate.setEmail(user.getEmail());
        candidate.setPhone(user.getPhone());
        candidate.setSkills(skills.trim());
        candidate.setJobId(jobId);
        candidate.setResumeFileName(resume.getOriginalFilename());
        candidate.setResumeContentType(resume.getContentType());
        candidate.setResumeFile(getBytes(resume));
        candidate.setResumeText(resumeText);
        candidate.setStatus("PENDING");
        candidate = candidateRepository.save(candidate);

        JobApplication application = new JobApplication();
        application.setApplicantUser(user);
        application.setJob(job);
        application.setCandidate(candidate);
        application.setStatus("SUBMITTED");
        applicationRepository.save(application);

        model.addAttribute("candidateName", user.getFullName());
        model.addAttribute("jobTitle", job.getTitle());

        return "application-success";
    }

    private byte[] getBytes(MultipartFile resume) {
        try {
            return resume.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read uploaded resume.", e);
        }
    }

    private String showError(Model model, String error) {
        return showError(model, error, null, null);
    }

    private String showError(Model model, String error, Long jobId, ApplicantUser user) {
        model.addAttribute("error", error);
        model.addAttribute("jobs", jobRepository.findByStatusIgnoreCaseOrderByIdDesc("ACTIVE"));
        model.addAttribute("selectedJobId", jobId);
        model.addAttribute("user", user);
        return "candidate-apply";
    }

    private ApplicantUser currentUser(Authentication authentication) {
        return userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user was not found."));
    }
}
