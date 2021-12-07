package com.github.zhuaidadaya.modMdo.projects;

import com.github.zhuaidadaya.modMdo.usr.User;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class ProjectUtil {
    private final LinkedHashMap<Object, Project> projects = new LinkedHashMap<>();

    public ProjectUtil() {

    }

    public ProjectUtil(Project... projects) {
        for(Project p : projects)
            this.projects.put(p.getName(), p);
    }

    public ProjectUtil(JSONObject projects) {
        for(Object o : projects.keySet()) {
            JSONObject projectJson = projects.getJSONObject(o.toString());

            Project project = new Project(projectJson.get("name").toString(),
                    new User(projectJson.getJSONObject("initiator")),
                    new UserUtil(projectJson.getJSONObject("contributors")).getUsers()
            ).setStartTime(projectJson.get("time_start").toString())
                    .setStatus(ProjectStatus.getStatusFromString(projectJson.get("status").toString()))
                    .setID(projectJson.getInt("id"));

            this.projects.put(o.toString(), project.setID(projects.length()));
        }
    }

    public ProjectUtil addProject(Project... projects) {
        for(Project p : projects) {
            if(p.getID() == -1)
                p.setID(size());
            this.projects.put(p.getName(), p);
        }
        return this;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(Object o : projects.keySet())
            json.put(o.toString(), projects.get(o.toString()).toJSONObject());
        return json;
    }

    public int size() {
        return projects.size();
    }

    public Project getFromIndex(int index) {
        return projects.get(projects.keySet().toArray()[index]);
    }

    public int getID(Project project) {
        return project.getID();
    }

    public int getID(String project) {
        if(projects.get(project) == null)
            throw new NullPointerException();
        return projects.get(project).getID();
    }
}
