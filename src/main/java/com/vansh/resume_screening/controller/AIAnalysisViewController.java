package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vansh.resume_screening.AIAnalysis;
import com.vansh.resume_screening.AIAnalysisRepository;
import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;
import com.vansh.resume_screening.Job;
import com.vansh.resume_screening.JobRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/ai-analysis")
public class AIAnalysisViewController {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    public AIAnalysisViewController(
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            AIAnalysisRepository aiAnalysisRepository) {

        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
    }

    @GetMapping("/{candidateId}/{jobId}")
    public String viewAnalysis(
            @PathVariable Long candidateId,
            @PathVariable Long jobId,
            Model model) {

        Candidate candidate =
                candidateRepository
                        .findById(candidateId)
                        .orElse(null);

        Job job =
                jobRepository
                        .findById(jobId)
                        .orElse(null);

        AIAnalysis analysis =
                aiAnalysisRepository
                        .findTopByCandidateIdAndJobIdOrderByAnalyzedAtDesc(
                                candidateId,
                                jobId
                        );

        if (candidate == null ||
                job == null ||
                analysis == null) {

            return "redirect:/candidate-ranking?jobId=" + jobId;
        }

        model.addAttribute(
                "candidate",
                candidate
        );

        model.addAttribute(
                "job",
                job
        );

        model.addAttribute(
                "analysis",
                analysis
        );

        return "ai-analysis";
    }
}
