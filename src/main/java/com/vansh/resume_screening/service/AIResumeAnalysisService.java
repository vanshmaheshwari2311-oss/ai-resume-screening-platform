package com.vansh.resume_screening.service;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

@Service
public class AIResumeAnalysisService {

    private final Client geminiClient;

    public AIResumeAnalysisService() {

        String apiKey = System.getenv("GOOGLE_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_API_KEY is not available to the Java application."
            );
        }

        this.geminiClient = Client.builder()
                .apiKey(apiKey.trim())
                .build();
    }

    public String analyzeResume(String resumeText, String jobDescription) {

        String prompt = """
                You are an AI-powered Applicant Tracking System (ATS)
                and recruitment analysis assistant.

                Your task is to analyze the candidate resume against
                the supplied job description.

                ============================================================
                IMPORTANT RULES
                ============================================================

                1. Use ONLY information contained in the supplied
                   resume and job description.

                2. Do NOT invent skills, experience, education,
                   projects, certifications, or qualifications.

                3. Do NOT assume experience that is not explicitly
                   demonstrated in the resume.

                4. The ATS score MUST be an integer from 0 to 100.

                5. Use the exact scoring framework provided below.

                6. Apply the same scoring framework every time.

                7. Do not randomly increase or decrease the score.

                8. Do not use protected characteristics such as:
                   - race
                   - religion
                   - gender
                   - age
                   - disability
                   - marital status
                   - nationality
                   - ethnicity
                   - or similar personal characteristics

                9. Evaluate ONLY job-related qualifications.

                10. Return ONLY valid JSON.

                11. Do NOT return Markdown.

                12. Do NOT put the JSON inside ``` blocks.

                ============================================================
                ATS SCORING FRAMEWORK
                ============================================================

                Calculate the ATS score using exactly these four
                categories.

                ------------------------------------------------------------
                1. REQUIRED SKILLS MATCH = 50 POINTS
                ------------------------------------------------------------

                Compare the candidate's demonstrated skills with the
                required skills listed in the job description.

                If all important required skills are clearly demonstrated:
                award up to 50 points.

                If some required skills are demonstrated:
                award proportional points.

                If no relevant required skills are demonstrated:
                award 0 points.

                ------------------------------------------------------------
                2. PREFERRED SKILLS MATCH = 15 POINTS
                ------------------------------------------------------------

                Compare the candidate's demonstrated skills with the
                preferred skills.

                Award points ONLY for skills explicitly demonstrated
                in the resume.

                Full preferred-skill match:
                up to 15 points.

                Partial match:
                award proportional points.

                No demonstrated preferred skills:
                0 points.

                ------------------------------------------------------------
                3. EXPERIENCE MATCH = 20 POINTS
                ------------------------------------------------------------

                Compare the required experience with experience
                explicitly demonstrated in the resume.

                If the candidate clearly satisfies the experience
                requirement:
                award up to 20 points.

                If the candidate partially satisfies the requirement:
                award approximately 10 points.

                If the candidate does not satisfy or does not
                demonstrate the requirement:
                award 0 points.

                Do NOT assume missing experience.

                ------------------------------------------------------------
                4. TECHNICAL / ROLE RELEVANCE = 15 POINTS
                ------------------------------------------------------------

                Evaluate how directly the candidate's demonstrated
                technical work, projects, responsibilities and
                experience relate to the job.

                Highly relevant:
                up to 15 points.

                Moderately relevant:
                approximately 8 points.

                Little or no relevant evidence:
                0 points.

                ============================================================
                FINAL SCORE
                ============================================================

                ATS SCORE =
                Required Skills Match
                +
                Preferred Skills Match
                +
                Experience Match
                +
                Technical / Role Relevance

                The final score MUST be between 0 and 100.

                ============================================================
                MATCH LEVEL
                ============================================================

                Use these exact ranges:

                0 - 39:
                LOW

                40 - 59:
                MODERATE

                60 - 79:
                HIGH

                80 - 100:
                VERY_HIGH

                The matchLevel MUST correspond to the atsScore.

                ============================================================
                JOB DESCRIPTION
                ============================================================

                %s

                ============================================================
                CANDIDATE RESUME
                ============================================================

                %s

                ============================================================
                REQUIRED JSON FORMAT
                ============================================================

                Return exactly this structure:

                {
                  "atsScore": 0,
                  "matchLevel": "LOW",
                  "matchedSkills": [],
                  "missingSkills": [],
                  "experienceAnalysis": "",
                  "strengths": [],
                  "gaps": [],
                  "recommendation": ""
                }

                ============================================================
                FIELD RULES
                ============================================================

                atsScore:
                - Integer only.
                - Must be between 0 and 100.
                - Must follow the scoring framework above.

                matchLevel:
                - Must be exactly one of:
                  "LOW"
                  "MODERATE"
                  "HIGH"
                  "VERY_HIGH"

                matchedSkills:
                - JSON array of strings.
                - Include only skills explicitly demonstrated
                  in the resume.
                - Include only skills relevant to the job.

                missingSkills:
                - JSON array of strings.
                - Include important job requirements that are
                  not demonstrated in the resume.

                experienceAnalysis:
                - String.
                - Concisely compare the candidate's demonstrated
                  experience with the job's experience requirement.

                strengths:
                - JSON array of strings.
                - Include relevant job-related candidate strengths.

                gaps:
                - JSON array of strings.
                - Include relevant job-related gaps.

                recommendation:
                - String.
                - Provide a concise recruiter-oriented next step
                  based ONLY on job-related qualifications.

                ============================================================
                FINAL VALIDATION
                ============================================================

                Before returning the answer, verify:

                1. atsScore is an integer from 0 to 100.
                2. matchLevel matches the score range.
                3. matchedSkills contains only resume-supported skills.
                4. missingSkills contains genuine missing requirements.
                5. No unsupported candidate information was invented.
                6. The JSON is valid.
                7. There is no Markdown.
                8. There are no ``` characters.

                Return ONLY the JSON object.
                """.formatted(
                jobDescription,
                resumeText
        );

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .temperature(0.0f)
                        .build();

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-flash-latest",
                        prompt,
                        config
                );

        return response.text();
    }
}