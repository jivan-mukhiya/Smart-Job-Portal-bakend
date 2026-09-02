package com.texas.smart.job.portal.modules.recommendation.processor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TextPreprocessor {

    public String preprocess(String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "";
        }

        return text
                .toLowerCase()
                .replaceAll("[^a-z0-9+#.\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public List<String> tokenize(String text) {

        String processed =
                preprocess(text);

        if (processed.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(
                        processed.split("\\s+")
                )
                .filter(token -> !token.isBlank())
                .toList();
    }
}