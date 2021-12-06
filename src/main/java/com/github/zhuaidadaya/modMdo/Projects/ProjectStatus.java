package com.github.zhuaidadaya.modMdo.Projects;

public enum ProjectStatus {
    COMPLETING(0,"Completing"),
    ABANDONED(1,"Abandoned"),
    FINISHED(2,"Finished");

    final int value;
    final String status;

    ProjectStatus(int value, String status) {
        this.value = value;
        this.status = status;
    }

    public static ProjectStatus getStatusFromString(String status) {
        switch(status) {
            case "Completing" -> {
                return COMPLETING;
            }
            case "Abandoned" -> {
                return ABANDONED;
            }
            case "Finished" -> {
                return FINISHED;
            }
        }
        return null;
    }

    public static String getStatus(ProjectStatus status) {
        switch(status) {
            case COMPLETING -> {
                return "Completing";
            }
            case ABANDONED -> {
                return "Abandoned";
            }
            case FINISHED-> {
                return "Finished";
            }
        }

        return null;
    }
}
