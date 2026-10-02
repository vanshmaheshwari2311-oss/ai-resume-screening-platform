package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CandidateResumeController {

    private final CandidateRepository candidateRepository;


    public CandidateResumeController(
            CandidateRepository candidateRepository) {

        this.candidateRepository = candidateRepository;
    }


    /*
     * ============================================================
     * VIEW / OPEN RESUME
     *
     * URL:
     *
     * /candidates/resume/{id}
     *
     * Example:
     *
     * /candidates/resume/1
     * ============================================================
     */

    @GetMapping("/candidates/resume/{id}")
    public ResponseEntity<byte[]> viewResume(
            @PathVariable("id") Long id) {

        Candidate candidate =
                candidateRepository
                        .findById(id)
                        .orElse(null);


        /*
         * Candidate not found
         */

        if (candidate == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        /*
         * Resume not available
         */

        if (!candidate.hasResume()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        /*
         * Get content type
         */

        String contentType =
                candidate.getResumeContentType();


        if (contentType == null
                || contentType.isBlank()) {

            contentType =
                    MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }


        /*
         * Return the actual file
         *
         * inline = open in browser when supported
         */

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + candidate.getResumeFileName()
                                + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .body(
                        candidate.getResumeFile()
                );
    }
}
