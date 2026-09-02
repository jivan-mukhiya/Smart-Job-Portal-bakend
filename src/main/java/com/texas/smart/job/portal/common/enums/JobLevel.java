package com.texas.smart.job.portal.common.enums;

public enum JobLevel {
    ENTRY("Entry Level"),
    MID("Mid Level"),
    SENIOR("Senior Level"),
    LEAD("Lead"),
    MANAGER("Manager"),
    DIRECTOR("Director"),
    EXECUTIVE("Executive");

    private final String displayName;

    JobLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}