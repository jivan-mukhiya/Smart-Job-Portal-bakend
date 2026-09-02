package com.texas.smart.job.portal.modules.recommendation.processor;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SkillExtractor {

    private final Map<String, String> skills =
            Map.ofEntries(

                    Map.entry("java", "java"),
                    Map.entry("spring", "spring"),
                    Map.entry("spring boot", "spring boot"),
                    Map.entry("spring security", "spring security"),
                    Map.entry("spring mvc", "spring mvc"),
                    Map.entry("hibernate", "hibernate"),
                    Map.entry("jpa", "jpa"),

                    Map.entry("mysql", "mysql"),
                    Map.entry("postgresql", "postgresql"),
                    Map.entry("postgres", "postgresql"),
                    Map.entry("mongodb", "mongodb"),
                    Map.entry("mongo", "mongodb"),
                    Map.entry("redis", "redis"),

                    Map.entry("javascript", "javascript"),
                    Map.entry("js", "javascript"),
                    Map.entry("typescript", "typescript"),
                    Map.entry("react", "react"),
                    Map.entry("reactjs", "react"),
                    Map.entry("angular", "angular"),
                    Map.entry("vue", "vue"),
                    Map.entry("html", "html"),
                    Map.entry("css", "css"),

                    Map.entry("python", "python"),
                    Map.entry("django", "django"),
                    Map.entry("flask", "flask"),

                    Map.entry("docker", "docker"),
                    Map.entry("kubernetes", "kubernetes"),
                    Map.entry("git", "git"),
                    Map.entry("github", "github"),
                    Map.entry("gitlab", "gitlab"),

                    Map.entry("aws", "aws"),
                    Map.entry("azure", "azure"),
                    Map.entry("gcp", "gcp"),

                    Map.entry("rest api", "rest api"),
                    Map.entry("rest", "rest"),
                    Map.entry("graphql", "graphql"),
                    Map.entry("microservices", "microservices"),
                    Map.entry("jwt", "jwt"),
                    Map.entry("oauth", "oauth"),

                    Map.entry("c++", "c++"),
                    Map.entry("c#", "c#"),
                    Map.entry(".net", ".net"),
                    Map.entry("php", "php"),
                    Map.entry("laravel", "laravel"),

                    Map.entry("machine learning", "machine learning"),
                    Map.entry("artificial intelligence", "artificial intelligence"),
                    Map.entry("tensorflow", "tensorflow"),
                    Map.entry("pytorch", "pytorch")
            );

    public Set<String> extractSkills(String text) {

        Set<String> extractedSkills =
                new LinkedHashSet<>();

        if (text == null ||
                text.trim().isEmpty()) {

            return extractedSkills;
        }

        String normalized =
                text.toLowerCase();

        for (Map.Entry<String, String> entry :
                skills.entrySet()) {

            String skill =
                    entry.getKey();

            String canonical =
                    entry.getValue();

            Pattern pattern =
                    Pattern.compile(
                            "(?<![a-z0-9])"
                                    + Pattern.quote(skill)
                                    + "(?![a-z0-9])",
                            Pattern.CASE_INSENSITIVE
                    );

            if (pattern.matcher(normalized).find()) {

                extractedSkills.add(canonical);
            }
        }

        return extractedSkills;
    }
}