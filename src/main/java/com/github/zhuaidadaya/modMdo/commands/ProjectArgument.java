package com.github.zhuaidadaya.modMdo.commands;

import static com.github.zhuaidadaya.modMdo.storage.Variables.projects;

public class ProjectArgument {
    public int getProjectId(String name) {
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

    public String[] getProjectsName() {
        if(projects != null) {
            System.out.println(projects.toJSONObject());

            return projects.toJSONObject().keySet().toArray(new String[projects.size()]);
        } else {
            return new String[]{"example"};
        }
    }
}
