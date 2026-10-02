package com.vansh.resume_screening.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.vansh.resume_screening.AIInterviewQuestion;
import com.vansh.resume_screening.AIInterviewQuestionRepository;
import com.vansh.resume_screening.InterviewEvaluation;
import com.vansh.resume_screening.InterviewEvaluationRepository;

@Service
public class AIInterviewSummaryService {

    private final AIInterviewQuestionRepository questionRepository;
    private final InterviewEvaluationRepository evaluationRepository;

    private final Client geminiClient;

    public AIInterviewSummaryService(
            AIInterviewQuestionRepository questionRepository,
            InterviewEvaluationRepository evaluationRepository) {

        this.questionRepository = questionRepository;
        this.evaluationRepository = evaluationRepository;

        String apiKey = System.getenv("GOOGLE_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_API_KEY environment variable is not configured."
            );
        }

        this.geminiClient = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    // ============================================================
    // GENERATE AI INTERVIEW SUMMARY
    // ============================================================

    public String generateSummary(Long candidateId) {

        List<AIInterviewQuestion> questions =
                questionRepository
                        .findByCandidateIdOrderByGeneratedAtDesc(
                                candidateId
                        );

        if (questions == null || questions.isEmpty()) {

            throw new IllegalArgumentException(
                    "No interview questions found for this candidate."
            );
        }

        StringBuilder interviewData =
                new StringBuilder();

        int evaluatedCount = 0;

        for (AIInterviewQuestion question : questions) {

            InterviewEvaluation evaluation =
                    evaluationRepository
                            .findByQuestionId(question.getId());

            interviewData.append("\n\n");

            interviewData.append(
                    "QUESTION:\n"
            );

            interviewData.append(
                    safe(question.getQuestion())
            );

            interviewData.append("\n");

            interviewData.append(
                    "QUESTION TYPE: "
            );

            interviewData.append(
                    safe(question.getQuestionType())
            );

            interviewData.append("\n");

            interviewData.append(
                    "DIFFICULTY: "
            );

            interviewData.append(
                    safe(question.getDifficulty())
            );

            interviewData.append("\n");

            interviewData.append(
                    "AI REASON:\n"
            );

            interviewData.append(
                    safe(question.getReason())
            );

            interviewData.append("\n");

            interviewData.append(
                    "INTERVIEWER NOTES:\n"
            );

            interviewData.append(
                    safe(question.getInterviewerNotes())
            );

            interviewData.append("\n");

            if (evaluation != null) {

                evaluatedCount++;

                interviewData.append(
                        "CANDIDATE ANSWER:\n"
                );

                interviewData.append(
                        safe(evaluation.getCandidateAnswer())
                );

                interviewData.append("\n");

                interviewData.append(
                        "RATING: "
                );

                interviewData.append(
                        evaluation.getRating() == null
                                ? "Not provided"
                                : evaluation.getRating()
                );

                interviewData.append("\n");

                interviewData.append(
                        "INTERVIEWER FEEDBACK:\n"
                );

                interviewData.append(
                        safe(
                            evaluation
                                .getInterviewerFeedback()
                        )
                );

                interviewData.append("\n");

            } else {

                interviewData.append(
                        "EVALUATION: Not completed"
                );

                interviewData.append("\n");
            }
        }

        String prompt = buildPrompt(
                interviewData.toString(),
                questions.size(),
                evaluatedCount
        );

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .temperature(0.0f)
                        .build();

        return geminiClient.models.generateContent(
                "gemini-flash-latest",
                prompt,
                config
        ).text();
    }

    // ============================================================
    // AI PROMPT
    // ============================================================

    private String buildPrompt(
            String interviewData,
            int totalQuestions,
            int evaluatedQuestions) {

        return """
                You are an AI interview analysis assistant
                for an HR recruitment system.

                Analyze ONLY the interview information provided
                below.

                Do not invent candidate experience, skills,
                projects, technologies, achievements, or facts.

                Do not infer or evaluate protected characteristics.

                Do not make an automatic hiring decision.

                The final hiring decision must remain with the
                human interviewer or HR team.

                Your job is to produce an evidence-based summary
                of the recorded interview.

                INTERVIEW STATISTICS:

                Total Questions:
                %d

                Evaluated Questions:
                %d

                INTERVIEW DATA:
                %s

                Return ONLY valid JSON.

                Use exactly this structure:

                {
                  "averageRating": 0.0,
                  "strengths": [
                    ""
                  ],
                  "areasToClarify": [
                    ""
                  ],
                  "technicalSummary": "",
                  "responseSummary": "",
                  "followUpQuestions": [
                    ""
                  ],
                  "overallSummary": ""
                }

                RULES:

                1. averageRating must be calculated only from
                   the provided numeric ratings.

                2. Use a number between 0 and 5.

                3. If there are no ratings, use 0.0.

                4. Strengths must be supported by the candidate's
                   recorded answers, ratings, or interviewer feedback.

                5. AreasToClarify must identify specific topics
                   that would benefit from further questioning.
                   Do not invent weaknesses.

                6. technicalSummary must summarize technical
                   evidence actually present in the interview.

                7. responseSummary must summarize the candidate's
                   recorded responses without making unsupported
                   personality or psychological claims.

                8. followUpQuestions should be practical questions
                   based on areas that were unclear or require
                   deeper evidence.

                9. overallSummary should summarize the recorded
                   interview evidence neutrally.

                10. Do not output Markdown.

                11. Do not output anything outside the JSON object.

                """.formatted(
                    totalQuestions,
                    evaluatedQuestions,
                    interviewData
                );
    }

    // ============================================================
    // NULL-SAFE TEXT
    // ============================================================

    private String safe(String value) {

        if (value == null || value.isBlank()) {
            return "Not provided";
        }

        return value;
    }
}