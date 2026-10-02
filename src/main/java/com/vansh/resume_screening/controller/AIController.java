package com.vansh.resume_screening.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vansh.resume_screening.service.AIResumeAnalysisService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AIController {

    private final AIResumeAnalysisService aiResumeAnalysisService;

    public AIController(AIResumeAnalysisService aiResumeAnalysisService) {
        this.aiResumeAnalysisService = aiResumeAnalysisService;
    }

    @GetMapping("/api/ai/test")
    public String testAI(
            @RequestParam String resume,
            @RequestParam String job) {

        return aiResumeAnalysisService.analyzeResume(resume, job);
    }
}
