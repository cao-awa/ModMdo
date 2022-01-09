package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.projects.Project;

import java.util.Collection;

import static com.github.zhuaidadaya.modMdo.storage.Variables.getApply;
import static com.github.zhuaidadaya.modMdo.storage.Variables.projects;

public class ProjectArgument {
    public String getProjectId(String name) {
        try {
            return projects.getID(name);
        } catch (Exception e) {
            return "p-n";
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

    public String[] getProjectsName() {
        String apply = getApply();
        if(projects != null) {
            Collection<Project> projectCollection = projects.getProjects();
            projectCollection.removeIf(project -> ! project.getApply().equals(apply));
            return projects.getForSpecially(projectCollection).getProjectsNames();
        } else {
            return new String[]{"example"};
        }
    }
}
