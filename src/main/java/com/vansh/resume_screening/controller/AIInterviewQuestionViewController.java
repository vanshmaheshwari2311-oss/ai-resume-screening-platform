package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vansh.resume_screening.Candidate;
import com.vansh.resume_screening.CandidateRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/ai-interview-questions")
public class AIInterviewQuestionViewController {

    private final CandidateRepository candidateRepository;

    public AIInterviewQuestionViewController(
            CandidateRepository candidateRepository) {

        this.candidateRepository = candidateRepository;
    }

    @GetMapping
    public String interviewQuestions(Model model) {

        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );

        return "ai-interview-questions";
    }

    @GetMapping("/{candidateId}")
    public String interviewQuestionsForCandidate(
            @PathVariable Long candidateId,
            Model model) {

        Candidate candidate =
                candidateRepository.findById(candidateId)
                        .orElse(null);

        if (candidate == null) {
            return "redirect:/ai-interview-questions";
        }

        model.addAttribute("candidate", candidate);

        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );

        return "ai-interview-questions";
    }
}
