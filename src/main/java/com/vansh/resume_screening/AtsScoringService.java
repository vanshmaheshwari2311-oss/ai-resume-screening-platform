package com.vansh.resume_screening;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class AtsScoringService {

    /*
     * ============================================================
     * WEIGHTED ATS ANALYSIS
     *
     * Required Skills  = 80%
     * Preferred Skills = 20%
     * ============================================================
     */
    public AtsResult analyzeWeighted(
            String resumeText,
            String requiredSkills,
            String preferredSkills) {

        List<String> required =
                parseSkills(requiredSkills);

        List<String> preferred =
                parseSkills(preferredSkills);

        Set<String> matchedRequired =
                new LinkedHashSet<>();

        Set<String> missingRequired =
                new LinkedHashSet<>();

        Set<String> matchedPreferred =
                new LinkedHashSet<>();

        String resume =
                resumeText == null
                        ? ""
                        : resumeText.toLowerCase(Locale.ROOT);


        /*
         * ========================================================
         * REQUIRED SKILLS
         * ========================================================
         */

        for (String skill : required) {

            String normalizedSkill =
                    skill.toLowerCase(Locale.ROOT).trim();

            if (!normalizedSkill.isEmpty()
                    && resume.contains(normalizedSkill)) {

                matchedRequired.add(skill);

            } else {

                missingRequired.add(skill);
            }
        }


        /*
         * ========================================================
         * PREFERRED SKILLS
         * ========================================================
         */

        for (String skill : preferred) {

            String normalizedSkill =
                    skill.toLowerCase(Locale.ROOT).trim();

            if (!normalizedSkill.isEmpty()
                    && resume.contains(normalizedSkill)) {

                matchedPreferred.add(skill);
            }
        }


        /*
         * ========================================================
         * REQUIRED SCORE
         *
         * Maximum = 80 points
         * ========================================================
         */

        double requiredScore = 0;

        if (!required.isEmpty()) {

            requiredScore =
                    ((double) matchedRequired.size()
                            / required.size()) * 80;
        }


        /*
         * ========================================================
         * PREFERRED SCORE
         *
         * Maximum = 20 points
         * ========================================================
         */

        double preferredScore = 0;

        if (!preferred.isEmpty()) {

            preferredScore =
                    ((double) matchedPreferred.size()
                            / preferred.size()) * 20;
        }


        /*
         * ========================================================
         * FINAL SCORE
         * ========================================================
         */

        int finalScore =
                (int) Math.round(
                        requiredScore + preferredScore
                );


        /*
         * If there are no required or preferred skills,
         * score should be zero.
         */

        if (required.isEmpty() && preferred.isEmpty()) {

            finalScore = 0;
        }


        return new AtsResult(
                finalScore,
                new ArrayList<>(matchedRequired),
                new ArrayList<>(missingRequired),
                new ArrayList<>(matchedPreferred),
                (int) Math.round(requiredScore),
                (int) Math.round(preferredScore)
        );
    }


    /*
     * ============================================================
     * ORIGINAL ATS ANALYSIS
     *
     * Kept for compatibility with existing code.
     * ============================================================
     */
    public AtsResult analyze(
            String resumeText,
            String requiredSkills) {

        List<String> required =
                parseSkills(requiredSkills);

        Set<String> matched =
                new LinkedHashSet<>();

        Set<String> missing =
                new LinkedHashSet<>();

        String resume =
                resumeText == null
                        ? ""
                        : resumeText.toLowerCase(Locale.ROOT);


        for (String skill : required) {

            String normalizedSkill =
                    skill.toLowerCase(Locale.ROOT).trim();

            if (!normalizedSkill.isEmpty()
                    && resume.contains(normalizedSkill)) {

                matched.add(skill);

            } else {

                missing.add(skill);
            }
        }


        int totalSkills =
                required.size();

        int matchedSkills =
                matched.size();

        int score = 0;

        if (totalSkills > 0) {

            score =
                    (int) Math.round(
                            ((double) matchedSkills
                                    / totalSkills) * 100
                    );
        }


        return new AtsResult(
                score,
                new ArrayList<>(matched),
                new ArrayList<>(missing),
                List.of(),
                score,
                0
        );
    }


    /*
     * ============================================================
     * CALCULATE ATS SCORE
     *
     * Existing compatibility method.
     * ============================================================
     */
    public int calculateScore(
            String resumeText,
            String requiredSkills) {

        AtsResult result =
                analyze(
                        resumeText,
                        requiredSkills
                );

        return result.getScore();
    }


    /*
     * ============================================================
     * CALCULATE WEIGHTED SCORE
     * ============================================================
     */
    public int calculateWeightedScore(
            String resumeText,
            String requiredSkills,
            String preferredSkills) {

        AtsResult result =
                analyzeWeighted(
                        resumeText,
                        requiredSkills,
                        preferredSkills
                );

        return result.getScore();
    }


    /*
     * ============================================================
     * FIND MATCHED REQUIRED SKILLS
     * ============================================================
     */
    public List<String> findMatchedSkills(
            String resumeText,
            String requiredSkills) {

        AtsResult result =
                analyze(
                        resumeText,
                        requiredSkills
                );

        return result.getMatchedSkills();
    }


    /*
     * ============================================================
     * FIND MISSING REQUIRED SKILLS
     * ============================================================
     */
    public List<String> findMissingSkills(
            String resumeText,
            String requiredSkills) {

        AtsResult result =
                analyze(
                        resumeText,
                        requiredSkills
                );

        return result.getMissingSkills();
    }


    /*
     * ============================================================
     * FIND MATCHED PREFERRED SKILLS
     * ============================================================
     */
    public List<String> findMatchedPreferredSkills(
            String resumeText,
            String preferredSkills) {

        if (preferredSkills == null
                || preferredSkills.isBlank()) {

            return List.of();
        }

        List<String> preferred =
                parseSkills(preferredSkills);

        Set<String> matched =
                new LinkedHashSet<>();

        String resume =
                resumeText == null
                        ? ""
                        : resumeText.toLowerCase(Locale.ROOT);


        for (String skill : preferred) {

            String normalizedSkill =
                    skill.toLowerCase(Locale.ROOT).trim();

            if (!normalizedSkill.isEmpty()
                    && resume.contains(normalizedSkill)) {

                matched.add(skill);
            }
        }

        return new ArrayList<>(matched);
    }


    /*
     * ============================================================
     * FIND MISSING PREFERRED SKILLS
     *
     * Preferred skills are not treated as required,
     * but this method is useful for the future dashboard.
     * ============================================================
     */
    public List<String> findMissingPreferredSkills(
            String resumeText,
            String preferredSkills) {

        if (preferredSkills == null
                || preferredSkills.isBlank()) {

            return List.of();
        }

        List<String> preferred =
                parseSkills(preferredSkills);

        Set<String> missing =
                new LinkedHashSet<>();

        String resume =
                resumeText == null
                        ? ""
                        : resumeText.toLowerCase(Locale.ROOT);


        for (String skill : preferred) {

            String normalizedSkill =
                    skill.toLowerCase(Locale.ROOT).trim();

            if (!normalizedSkill.isEmpty()
                    && !resume.contains(normalizedSkill)) {

                missing.add(skill);
            }
        }

        return new ArrayList<>(missing);
    }


    /*
     * ============================================================
     * DETECT SKILLS FROM RESUME
     * ============================================================
     */
    public List<String> findDetectedSkills(
            String resumeText) {

        if (resumeText == null
                || resumeText.isBlank()) {

            return List.of();
        }

        String resume =
                resumeText.toLowerCase(Locale.ROOT);


        List<String> skillDictionary =
                Arrays.asList(

                        // Programming Languages
                        "java",
                        "python",
                        "c++",
                        "c#",
                        "javascript",
                        "typescript",

                        // Web Development
                        "html",
                        "css",
                        "bootstrap",
                        "tailwind",

                        // Frontend Frameworks
                        "react",
                        "angular",
                        "vue",

                        // Spring
                        "spring",
                        "spring boot",
                        "spring security",

                        // Databases
                        "mysql",
                        "postgresql",
                        "mongodb",
                        "sql",

                        // Version Control
                        "git",
                        "github",
                        "gitlab",

                        // DevOps / Cloud
                        "docker",
                        "kubernetes",
                        "aws",
                        "azure",
                        "google cloud",

                        // AI / ML
                        "machine learning",
                        "deep learning",
                        "artificial intelligence",
                        "ai",
                        "nlp",

                        // Backend / APIs
                        "rest api",
                        "api",

                        // Java Technologies
                        "hibernate",
                        "jpa",
                        "maven",
                        "gradle",

                        // Operating Systems
                        "linux",
                        "windows"
                );


        Set<String> detected =
                new LinkedHashSet<>();


        for (String skill : skillDictionary) {

            if (resume.contains(
                    skill.toLowerCase(Locale.ROOT))) {

                detected.add(skill);
            }
        }


        return new ArrayList<>(detected);
    }


    /*
     * ============================================================
     * COMPATIBILITY METHOD
     *
     * Existing AtsController uses detectSkills()
     * ============================================================
     */
    public List<String> detectSkills(
            String resumeText) {

        return findDetectedSkills(resumeText);
    }


    /*
     * ============================================================
     * PARSE SKILLS
     * ============================================================
     */
    private List<String> parseSkills(
            String skills) {

        if (skills == null
                || skills.isBlank()) {

            return List.of();
        }


        String[] skillArray =
                skills.split(",");


        List<String> result =
                new ArrayList<>();


        for (String skill : skillArray) {

            if (skill != null) {

                String cleaned =
                        skill.trim();

                if (!cleaned.isEmpty()) {

                    result.add(cleaned);
                }
            }
        }


        return result.stream()
                .distinct()
                .toList();
    }


    /*
     * ============================================================
     * ATS RESULT CLASS
     * ============================================================
     */
    public static class AtsResult {

        private final int score;

        private final List<String> matchedSkills;

        private final List<String> missingSkills;

        private final List<String> matchedPreferredSkills;

        private final int requiredScore;

        private final int preferredScore;


        public AtsResult(
                int score,
                List<String> matchedSkills,
                List<String> missingSkills,
                List<String> matchedPreferredSkills,
                int requiredScore,
                int preferredScore) {

            this.score =
                    score;

            this.matchedSkills =
                    matchedSkills;

            this.missingSkills =
                    missingSkills;

            this.matchedPreferredSkills =
                    matchedPreferredSkills;

            this.requiredScore =
                    requiredScore;

            this.preferredScore =
                    preferredScore;
        }


        public int getScore() {

            return score;
        }


        public List<String> getMatchedSkills() {

            return matchedSkills;
        }


        public List<String> getMissingSkills() {

            return missingSkills;
        }


        public List<String> getMatchedPreferredSkills() {

            return matchedPreferredSkills;
        }


        public int getRequiredScore() {

            return requiredScore;
        }


        public int getPreferredScore() {

            return preferredScore;
        }
    }
}