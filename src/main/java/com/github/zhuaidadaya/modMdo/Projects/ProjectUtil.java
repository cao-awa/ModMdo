package com.github.zhuaidadaya.modMdo.Projects;

import com.github.zhuaidadaya.modMdo.Usr.User;
import com.github.zhuaidadaya.modMdo.Usr.UserUtil;
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

            Project project = new Project(projectJson.get("name").toString(), new User(projectJson.getJSONObject("initiator")), new UserUtil(projectJson.getJSONObject("contributors")).getUsers());

            this.projects.put(o.toString(), project.setID(projects.length()));
        }
    }

    public ProjectUtil addProject(Project... projects) {
        for(Project p : projects)
            this.projects.put(p.getName(), p);
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
        return projects.get(project).getID();
    }
}
