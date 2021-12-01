package com.github.zhuaidadaya.modMdo.Commands;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.projects;

public class ProjectArgument {
    public static int getProjectId(String name) {
        return projects.getID(name);
    }

    public static String getProjectName(int operationID) {
        return switch(operationID) {
            case 0 -> "list";
            case 1 -> "start";
            case 2 -> "finish";
            case 3 -> "abandon";
            case 4 -> "remove";
            case 5 -> "member";
            default -> null;
        };
    }

    public static String[] getProjectsName() {
        String[] displayOperationNames = new String[projects.size()];

        for(int i = 0; i < projects.size(); ++ i) {
            displayOperationNames[i] = projects.getFromIndex(i).getName();
        }

        return displayOperationNames;
    }
}
