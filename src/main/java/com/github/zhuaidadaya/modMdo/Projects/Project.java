package com.github.zhuaidadaya.modMdo.projects;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import com.github.zhuaidadaya.modMdo.usr.User;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class Project {
    private final String name;
    private final User initiator;
    private final LinkedHashSet<User> contributors = new LinkedHashSet<>();
    private final String applyIn;
    private String startTime;
    private String completedTime;
    private String note;
    private ProjectStatus status;
    private String id = "p-n";

    public Project(String name, String applyIn, User initiator) {
        if(name == null || initiator == null)
            throw new IllegalArgumentException("argument cannot be null");
        this.name = name;
        this.initiator = initiator;
        contributors.add(initiator);
        startTime = Times.getTime(TimeType.AS_SECOND);
        status = ProjectStatus.COMPLETING;
        this.applyIn = applyIn;
    }

    public Project(String name, String applyIn, User initiator, User... contributors) {
        if(name == null || initiator == null || contributors == null)
            throw new IllegalArgumentException("argument cannot be null");
        this.name = name;
        this.initiator = initiator;
        this.contributors.addAll(Arrays.asList(contributors));
        startTime = Times.getTime(TimeType.AS_SECOND);
        status = ProjectStatus.COMPLETING;
        this.applyIn = applyIn;
    }

    public String getID() {
        return id;
    }

    public Project setID(String id) {
        this.id = id;
        return this;
    }

    public Project setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
        return this;
    }

    public Project setNote(String note) {
        this.note = note;
        return this;
    }

    public Project setStatus(ProjectStatus status) {
        this.status = status;
        return this;
    }

    public String getName() {
        return name;
    }

    public User getInitiator() {
        return initiator;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getApply() {
        return applyIn;
    }

    public Project setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public LinkedHashSet<User> getContributors() {
        return contributors;
    }

    public void addContributor(User contributor) {
        contributors.add(contributor);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("initiator", getInitiator().toJSONObject());
        json.put("time_start", startTime);
        json.put("note", note);
        json.put("status", status.status);
        if(completedTime != null)
            json.put("time_completed", completedTime);
        if(!id.equals("p-n"))
            json.put("id", id);
        LinkedHashSet<User> contributors = getContributors();
        JSONObject contributorsJson = new JSONObject();
        for(User u : contributors) {
            contributorsJson.put(u.getName(), u.toJSONObject());
        }
        json.put("contributors", contributorsJson);
        json.put("apply",applyIn);
        return json;
    }
}
