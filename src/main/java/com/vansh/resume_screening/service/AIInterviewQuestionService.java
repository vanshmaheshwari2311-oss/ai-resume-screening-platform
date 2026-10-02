package com.vansh.resume_screening.service;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

@Service
public class AIInterviewQuestionService {

    private final Client geminiClient;

    public AIInterviewQuestionService() {

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

    public String generateQuestions(
            String resumeText,
            String questionType,
            String difficulty,
            int numberOfQuestions) {

        String prompt = """
                You are an AI-powered technical and HR interview
                question generator for a recruitment platform.

                Your task is to generate interview questions based ONLY
                on the candidate resume provided below.

                ============================================================
                IMPORTANT RULES
                ============================================================

                1. Use ONLY information explicitly present in the resume.

                2. Do NOT invent:
                   - projects
                   - technologies
                   - programming languages
                   - frameworks
                   - companies
                   - job responsibilities
                   - certifications
                   - education
                   - achievements
                   - work experience

                3. Do NOT assume that the candidate knows a technology
                   simply because it is common for their role.

                4. Every resume-based question must be traceable to
                   something actually mentioned in the resume.

                5. Questions should be useful for a real interviewer.

                6. Avoid duplicate questions.

                7. Do not ask questions about protected characteristics
                   such as:
                   - race
                   - religion
                   - gender
                   - age
                   - disability
                   - marital status
                   - nationality
                   - ethnicity

                8. Do not evaluate the candidate's personality,
                   intelligence, health or personal circumstances.

                9. Return ONLY valid JSON.

                10. Do NOT return Markdown.

                11. Do NOT use ``` characters.

                ============================================================
                QUESTION TYPE
                ============================================================

                Requested question type:
                %s

                Allowed values:

                RESUME_BASED
                TECHNICAL
                HR_BEHAVIORAL
                PROJECT_BASED
                MIXED

                ============================================================
                DIFFICULTY
                ============================================================

                Requested difficulty:
                %s

                Allowed values:

                EASY
                MEDIUM
                HARD

                ============================================================
                NUMBER OF QUESTIONS
                ============================================================

                Generate exactly:
                %d

                questions.

                ============================================================
                QUESTION GENERATION GUIDELINES
                ============================================================

                RESUME_BASED:
                Ask questions directly about information mentioned
                in the resume.

                TECHNICAL:
                Ask technical questions related to technologies,
                programming languages, frameworks, databases,
                tools or technical concepts explicitly mentioned
                in the resume.

                HR_BEHAVIORAL:
                Ask professional behavioral questions connected to
                the candidate's demonstrated projects, education,
                internships or work experience.

                PROJECT_BASED:
                Ask detailed questions about projects explicitly
                mentioned in the resume.

                MIXED:
                Create a balanced combination of resume-based,
                technical, project-based and professional behavioral
                questions based on the resume.

                ============================================================
                RESUME
                ============================================================

                %s

                ============================================================
                REQUIRED JSON FORMAT
                ============================================================

                {
                  "questions": [
                    {
                      "question": "",
                      "questionType": "",
                      "difficulty": "",
                      "reason": ""
                    }
                  ]
                }

                ============================================================
                FIELD RULES
                ============================================================

                question:
                - The actual interview question.
                - Must be relevant to the resume.

                questionType:
                - One of:
                  RESUME_BASED
                  TECHNICAL
                  HR_BEHAVIORAL
                  PROJECT_BASED

                difficulty:
                - One of:
                  EASY
                  MEDIUM
                  HARD

                reason:
                - Briefly explain what resume information caused
                  this question to be generated.
                - Do not invent information.

                ============================================================
                FINAL VALIDATION
                ============================================================

                Before returning the response verify:

                1. Exactly %d questions are returned.
                2. Every question is based on the supplied resume.
                3. No unsupported information was invented.
                4. No duplicate questions exist.
                5. questionType is valid.
                6. difficulty is valid.
                7. JSON is valid.
                8. No Markdown is returned.

                Return ONLY the JSON object.
                """.formatted(
                questionType,
                difficulty,
                numberOfQuestions,
                resumeText,
                numberOfQuestions
        );

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .temperature(0.2f)
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