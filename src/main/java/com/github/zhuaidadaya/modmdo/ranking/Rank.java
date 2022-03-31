package com.github.zhuaidadaya.modmdo.ranking;

public class Rank {
    private final boolean isStat;
    private final String name;
    private final String inConfigId;
    private final String rankId;

    public Rank(String name,String inConfigId, String rankId) {
        this.name = name;
        this.inConfigId = inConfigId;
        this.rankId = rankId;
        this.isStat = false;
    }

    public Rank(String name, String inConfigId, String rankId, boolean isStat) {
        this.name = name;
        this.inConfigId = inConfigId;
        this.rankId = rankId;
        this.isStat = isStat;
    }

    public String getName() {
        return name;
    }

    public String getRankId() {
        return rankId;
    }

    public String toString() {
        return name;
    }

    public String getInConfigId() {
        return inConfigId;
    }

    public boolean isStat() {
        return isStat;
    }
}
