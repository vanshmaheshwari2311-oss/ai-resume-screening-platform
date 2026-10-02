package com.vansh.resume_screening;

import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CandidateController {

    private final CandidateRepository candidateRepository;
    private final ResumeTextExtractor resumeTextExtractor;

    public CandidateController(
            CandidateRepository candidateRepository,
            ResumeTextExtractor resumeTextExtractor) {

        this.candidateRepository = candidateRepository;
        this.resumeTextExtractor = resumeTextExtractor;
    }


    /*
     * ============================================================
     * RESUME MANAGEMENT PAGE
     *
     * URL:
     * http://localhost:8080/resumes
     * ============================================================
     */
    @GetMapping("/resumes")
    public String resumes(Model model) {

        model.addAttribute(
                "candidates",
                candidateRepository.findAll()
        );

        return "resumes";
    }


    /*
     * ============================================================
     * SAVE CANDIDATE + RESUME
     *
     * URL:
     * POST /resumes/save
     * ============================================================
     */
    @PostMapping("/resumes/save")
    public String saveCandidate(

            @RequestParam("name")
            String name,

            @RequestParam("email")
            String email,

            @RequestParam("phone")
            String phone,

            @RequestParam("skills")
            String skills,

            @RequestParam("resume")
            MultipartFile resume

    ) throws IOException {


        String fileName = "";

        String extractedText = "";


        /*
         * ========================================================
         * CHECK RESUME
         * ========================================================
         */

        if (resume != null && !resume.isEmpty()) {


            /*
             * Get original filename
             */
            fileName = resume.getOriginalFilename();


            /*
             * Extract resume text
             */
            extractedText =
                    resumeTextExtractor.extractText(resume);


            /*
             * Show extraction information
             * in VS Code terminal
             */

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "Resume File: "
                    + fileName
            );

            System.out.println(
                    "Content Type: "
                    + resume.getContentType()
            );

            System.out.println(
                    "File Size: "
                    + resume.getSize()
                    + " bytes"
            );

            System.out.println(
                    "Extracted Text Length: "
                    + extractedText.length()
            );

            System.out.println(
                    "================================="
            );
        }


        /*
         * ========================================================
         * CREATE CANDIDATE
         * ========================================================
         */

        Candidate candidate =
                new Candidate(
                        name,
                        email,
                        phone,
                        skills,
                        fileName,
                        extractedText
                );


        /*
         * ========================================================
         * STORE ACTUAL RESUME FILE
         * ========================================================
         */

        if (resume != null && !resume.isEmpty()) {

            candidate.setResumeFile(
                    resume.getBytes()
            );

            candidate.setResumeContentType(
                    resume.getContentType()
            );
        }


        /*
         * New candidate is PENDING
         */

        candidate.setStatus("PENDING");


        /*
         * ========================================================
         * SAVE TO DATABASE
         * ========================================================
         */

        candidateRepository.save(candidate);


        /*
         * Return to Resume Management
         */

        return "redirect:/resumes";
    }


    /*
     * ============================================================
     * VIEW RESUME
     *
     * Opens the uploaded PDF/DOC/DOCX in browser when supported.
     *
     * URL:
     *
     * /resumes/view/{id}
     *
     * Example:
     *
     * /resumes/view/1
     * ============================================================
     */

    @GetMapping("/resumes/view/{id}")
    public ResponseEntity<ByteArrayResource> viewResume(

            @PathVariable("id")
            Long id

    ) {


        Candidate candidate =
                candidateRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found with ID: "
                                        + id
                                )
                        );


        if (!candidate.hasResume()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        String contentType =
                candidate.getResumeContentType();


        if (contentType == null
                || contentType.isBlank()) {

            contentType =
                    MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }


        ByteArrayResource resource =
                new ByteArrayResource(
                        candidate.getResumeFile()
                );


        return ResponseEntity.ok()

                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + candidate.getResumeFileName()
                                + "\""
                )

                .contentLength(
                        candidate.getResumeFile().length
                )

                .body(resource);
    }


    /*
     * ============================================================
     * DOWNLOAD RESUME
     *
     * URL:
     *
     * /resumes/download/{id}
     *
     * ============================================================
     */

    @GetMapping("/resumes/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadResume(

            @PathVariable("id")
            Long id

    ) {


        Candidate candidate =
                candidateRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found with ID: "
                                        + id
                                )
                        );


        if (!candidate.hasResume()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        String contentType =
                candidate.getResumeContentType();


        if (contentType == null
                || contentType.isBlank()) {

            contentType =
                    MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }


        ByteArrayResource resource =
                new ByteArrayResource(
                        candidate.getResumeFile()
                );


        return ResponseEntity.ok()

                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + candidate.getResumeFileName()
                                + "\""
                )

                .contentLength(
                        candidate.getResumeFile().length
                )

                .body(resource);
    }


    /*
     * ============================================================
     * DELETE CANDIDATE
     *
     * This also deletes the stored resume.
     *
     * URL:
     *
     * POST /resumes/delete/{id}
     * ============================================================
     */

    @PostMapping("/resumes/delete/{id}")
    public String deleteCandidate(

            @PathVariable("id")
            Long id

    ) {


        Candidate candidate =
                candidateRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Candidate not found with ID: "
                                        + id
                                )
                        );


        candidateRepository.delete(candidate);


        return "redirect:/resumes";
    }
}
