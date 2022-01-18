package com.github.zhuaidadaya.modMdo.projects;

import com.github.zhuaidadaya.modMdo.usr.User;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ProjectUtil {
    private final LinkedHashMap<Object, Project> projects = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> projectsMap = new LinkedHashMap<>();

    public ProjectUtil() {

    }

    public ProjectUtil(Project... projects) {
        for(Project p : projects)
            this.projects.put(p.getID(), p);
    }

    public ProjectUtil(JSONObject projects) {
        for(Object o : projects.keySet()) {
            JSONObject projectJson = projects.getJSONObject(o.toString());
            String apply;
            try {
                apply = projectJson.getString("apply");
            } catch (Exception e) {
                apply = "";
            }

            Project project = new Project(projectJson.getString("name"), apply, new User(projectJson.getJSONObject("initiator")), new UserUtil(projectJson.getJSONObject("contributors")).getUsers()).setStartTime(projectJson.get("time_start").toString()).setStatus(ProjectStatus.getStatusFromString(projectJson.get("status").toString())).setID(o.toString());

            if(project.getID().equals("p-n"))
                project.setID("p-" + projects.length());

            this.projects.put(project.getID(), project);
            projectsMap.put(project.getName(), project.getID());
        }
        System.out.println("PJT done: " + this.projects);
    }

    public ProjectUtil addProject(Project... projects) {
        for(Project project : projects) {
            if(project.getID().equals("p-n")) {
                if(this.projects.get("p-" + (size() + 1)) == null)
                    project.setID("p-" + (size() + 1));
                else
                    project.setID("p-oer/" + (size() + 1));
            }
            this.projects.put(project.getID(), project);
            projectsMap.put(project.getName(), project.getID());
        }
        System.out.println(this.projects);
        return this;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(Object o : projects.keySet()) {
            Project project = projects.get(o.toString());
            json.put(project.getID(), project.toJSONObject());
        }
        return json;
    }

    public String[] getProjectsNames() {
        HashSet<String> returnValues = new HashSet<>();
        for(Project project : projects.values()) {
            returnValues.add(project.getName());
        }
        return returnValues.toArray(new String[0]);
    }

    public int size() {
        return projects.size();
    }

    public Project getFromIndex(int index) {
        return projects.get(projects.keySet().toArray()[index]);
    }

    public String getID(Project project) {
        return project.getID();
    }

    public String getID(String project) {
        if(projectsMap.get(project) == null)
            throw new NullPointerException();
        return projects.get(projectsMap.get(project)).getID();
    }

    public Collection<Project> getProjects() {
        return projects.values();
    }

    public ProjectUtil getForSpecially(Collection<Project> projects) {
        HashSet<String> candidates = new HashSet<>();
        for(Project project : projects) {
            candidates.add(project.getApply());
        }
        JSONObject json = toJSONObject();
        for(Project project : this.projects.values()) {
            if(! candidates.contains(project.getApply())) {
                json.remove(project.getID());
            }
        }
        return new ProjectUtil(json);
    }
}
