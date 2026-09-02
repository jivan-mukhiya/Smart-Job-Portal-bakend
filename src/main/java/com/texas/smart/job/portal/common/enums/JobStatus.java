package com.texas.smart.job.portal.common.enums;

public enum JobStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    PUBLISHED("Published"),
    EXPIRED("Expired"),
    CLOSED("Closed"),
    DRAFT("Draft"),
    REJECTED("Rejected");

    private final String displayName;

    JobStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}