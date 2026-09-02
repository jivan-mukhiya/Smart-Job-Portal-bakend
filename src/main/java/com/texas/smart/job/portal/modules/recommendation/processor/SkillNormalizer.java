package com.texas.smart.job.portal.modules.recommendation.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SkillNormalizer {

    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        ALIASES.put("springboot", "spring boot");
        ALIASES.put("spring-boot", "spring boot");

        ALIASES.put("springsecurity", "spring security");
        ALIASES.put("spring-security", "spring security");

        ALIASES.put("springmvc", "spring mvc");
        ALIASES.put("spring-mvc", "spring mvc");

        ALIASES.put("js", "javascript");
        ALIASES.put("javascript", "javascript");

        ALIASES.put("reactjs", "react");
        ALIASES.put("react.js", "react");

        ALIASES.put("postgres", "postgresql");
        ALIASES.put("postgresql", "postgresql");

        ALIASES.put("mongo", "mongodb");
        ALIASES.put("mongodb", "mongodb");

        ALIASES.put("k8s", "kubernetes");

        ALIASES.put("nodejs", "node.js");
        ALIASES.put("node.js", "node.js");

        ALIASES.put("restapi", "rest api");
        ALIASES.put("rest-api", "rest api");
        ALIASES.put("rest", "rest api");
    }

    private SkillNormalizer() {
    }

    public static String normalize(String skill) {

        if (skill == null || skill.isBlank()) {
            return "";
        }

        String normalized = skill
                .trim()
                .toLowerCase();

        return ALIASES.getOrDefault(normalized, normalized);
    }

    public static Set<String> normalizeSkills(Set<String> skills) {

        Set<String> normalizedSkills = new HashSet<>();

        if (skills == null || skills.isEmpty()) {
            return normalizedSkills;
        }

        for (String skill : skills) {

            String normalized = normalize(skill);

            if (!normalized.isBlank()) {
                normalizedSkills.add(normalized);
            }
        }

        return normalizedSkills;
    }
}