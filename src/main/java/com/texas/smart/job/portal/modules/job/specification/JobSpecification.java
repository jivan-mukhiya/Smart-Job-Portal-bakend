package com.texas.smart.job.portal.modules.job.specification;

import com.texas.smart.job.portal.modules.job.entity.Job;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> publishedActiveJobs() {

        return (root, query, criteriaBuilder) -> {

            return criteriaBuilder.and(

                    criteriaBuilder.equal(
                            root.get("active"),
                            true
                    ),

                    criteriaBuilder.equal(
                            root.get("status"),
                            "PUBLISHED"
                    )
            );
        };
    }

    public static Specification<Job> search(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            if (search == null ||
                    search.trim().isEmpty()) {

                return criteriaBuilder.conjunction();
            }

            String keyword =
                    "%" +
                            search.trim().toLowerCase() +
                            "%";

            Join<Object, Object> company =
                    root.join(
                            "company",
                            JoinType.LEFT
                    );

            Join<Object, Object> requiredSkills =
                    root.join(
                            "requiredSkills",
                            JoinType.LEFT
                    );

            query.distinct(true);

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("title")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("description")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("responsibilities")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("requirements")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("location")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("address")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    company.get("name")
                            ),
                            keyword
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    requiredSkills.get("skillName")
                            ),
                            keyword
                    )
            );
        };
    }
}