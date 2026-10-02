package com.vansh.resume_screening;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class PublicJobController {

    private final JobRepository jobRepository;

    public PublicJobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/jobs/browse")
    public String browseJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findByStatusIgnoreCaseOrderByIdDesc("ACTIVE"));
        return "public-jobs";
    }

    @GetMapping("/jobs/public/{id}")
    public String publicJobDetails(@PathVariable Long id, Model model) {
        Job job = jobRepository.findById(id)
                .filter(Job::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        model.addAttribute("job", job);
        return "public-job-details";
    }
}
