package com.github.zhuaidadaya.modMdo.Commands;

import java.util.Collection;
import java.util.List;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.projects;

public class ProjectArgument {
    public int getProjectId(String name) {
        System.out.println(projects.toJSONObject());
        try {
            return projects.getID(name);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getProjectName(int operationID) {
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

    public Collection<String> getProjectsName() {
        if(projects.size() == 0) {
            return List.of("example");
        }
        Collection<String> displayOperationNames = new java.util.ArrayList<>(List.of());

        for(int i = 0; i < projects.size(); ++ i) {
            displayOperationNames.add(projects.getFromIndex(i).getName());
        }

        return displayOperationNames;
    }
}
