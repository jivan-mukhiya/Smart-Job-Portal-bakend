package com.texas.smart.job.portal.modules.recommendation.engine;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TfIdfEngine {

    public Map<String, Double> calculateTfIdf(
            String document,
            List<String> corpus
    ) {

        Map<String, Double> tfIdf =
                new HashMap<>();

        if (document == null ||
                document.trim().isEmpty()) {

            return tfIdf;
        }

        if (corpus == null ||
                corpus.isEmpty()) {

            return tfIdf;
        }

        Map<String, Integer> termFrequency =
                calculateTermFrequency(document);

        int totalTerms =
                document.split("\\s+").length;

        for (Map.Entry<String, Integer> entry :
                termFrequency.entrySet()) {

            String term =
                    entry.getKey();

            int frequency =
                    entry.getValue();

            double tf =
                    (double) frequency /
                            totalTerms;

            int documentFrequency =
                    calculateDocumentFrequency(
                            term,
                            corpus
                    );

            double idf =
                    Math.log(
                            (double) corpus.size()
                                    / (1 + documentFrequency)
                    ) + 1.0;

            tfIdf.put(
                    term,
                    tf * idf
            );
        }

        return tfIdf;
    }

    private Map<String, Integer> calculateTermFrequency(
            String document
    ) {

        Map<String, Integer> frequency =
                new HashMap<>();

        String[] terms =
                document.toLowerCase()
                        .split("\\s+");

        for (String term : terms) {

            if (term.isBlank()) {
                continue;
            }

            frequency.merge(
                    term,
                    1,
                    Integer::sum
            );
        }

        return frequency;
    }

    private int calculateDocumentFrequency(
            String term,
            List<String> corpus
    ) {

        int count = 0;

        for (String document : corpus) {

            if (document == null) {
                continue;
            }

            Set<String> terms =
                    new HashSet<>(
                            List.of(
                                    document
                                            .toLowerCase()
                                            .split("\\s+")
                            )
                    );

            if (terms.contains(term)) {
                count++;
            }
        }

        return count;
    }
}