package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.vansh.resume_screening.AIAnalysis;
import com.vansh.resume_screening.AIAnalysisRepository;
import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;
import com.vansh.resume_screening.JobRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CandidateManagementController {

    private final CandidateRepository candidateRepository;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final JobRepository jobRepository;

    public CandidateManagementController(
            CandidateRepository candidateRepository,
            AIAnalysisRepository aiAnalysisRepository,
            JobRepository jobRepository) {

        this.candidateRepository = candidateRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/candidate-management")
    public String candidateManagement(Model model) {

        List<Candidate> candidates = candidateRepository.findAll();

        model.addAttribute("candidates", candidates);

        return "candidate-management";
    }

    @GetMapping("/candidate-management/view/{id}")
    public String viewCandidate(
            @PathVariable Long id,
            Model model) {

        Candidate candidate = candidateRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidate not found: " + id));

        model.addAttribute("candidate", candidate);

        // Send all jobs to dropdown
        model.addAttribute(
                "jobs",
                jobRepository.findAll()
        );

        // Existing AI analyses for this candidate
        List<AIAnalysis> analyses =
                aiAnalysisRepository.findByCandidateId(id);

        model.addAttribute(
                "aiAnalyses",
                analyses
        );

        return "candidate-details";
    }

    @GetMapping("/candidate-management/edit/{id}")
    public String editCandidate(
            @PathVariable Long id,
            Model model) {

        Candidate candidate = candidateRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidate not found: " + id));

        model.addAttribute("candidate", candidate);

        return "candidate-edit";
    }

    @PostMapping("/candidate-management/update")
    public String updateCandidate(
            @ModelAttribute Candidate candidate) {

        Candidate existing = candidateRepository
                .findById(candidate.getId())
                .orElseThrow();

        existing.setName(candidate.getName());
        existing.setEmail(candidate.getEmail());
        existing.setPhone(candidate.getPhone());
        existing.setSkills(candidate.getSkills());
        existing.setStatus(candidate.getStatus());

        candidateRepository.save(existing);

        return "redirect:/candidate-management/view/"
                + candidate.getId();
    }

    @PostMapping("/candidate-management/delete/{id}")
    public String deleteCandidate(
            @PathVariable Long id) {

        candidateRepository.deleteById(id);

        return "redirect:/candidate-management";
    }

    @PostMapping("/candidate-management/shortlist/{id}")
    public String shortlistCandidate(
            @PathVariable Long id) {

        Candidate candidate = candidateRepository
                .findById(id)
                .orElseThrow();

        candidate.setStatus("SHORTLISTED");

        candidateRepository.save(candidate);

        return "redirect:/candidate-management/view/" + id;
    }

    @PostMapping("/candidate-management/reject/{id}")
    public String rejectCandidate(
            @PathVariable Long id) {

        Candidate candidate = candidateRepository
                .findById(id)
                .orElseThrow();

        candidate.setStatus("REJECTED");

        candidateRepository.save(candidate);

        return "redirect:/candidate-management/view/" + id;
    }

    @PostMapping("/candidate-management/reset/{id}")
    public String resetCandidate(
            @PathVariable Long id) {

        Candidate candidate = candidateRepository
                .findById(id)
                .orElseThrow();

        candidate.setStatus("PENDING");

        candidateRepository.save(candidate);

        return "redirect:/candidate-management/view/" + id;
    }
}
