package com.family.agent.model;

import java.sql.Timestamp;

public class LogEntry {
    private String type;
    private String appName;
    private Timestamp startTime;
    private Timestamp endTime;

    public LogEntry(){}
    public  LogEntry(String type, String name, Timestamp startTime, Timestamp endTime)
    {
        this.type = type;
        this.appName = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getType() {return type;}
    public String getAppName() {return appName;}
    public Timestamp getStartTime() {return startTime;}
    public Timestamp getEndTime() {return endTime;}
    public void setType(String type) {this.type = type;}
    public void setAppName(String appName) {this.appName = appName;}
    public void setStartTime(Timestamp startTime) {this.startTime = startTime;}
    public void setEndTime(Timestamp endTime) {this.endTime = endTime;}

    public void print() {
        System.out.printf("[%s] %s | %s → %s%n",
                type, appName, startTime, endTime);
    }
    public String changeToString()
    {
        return type + "|" + appName + "|" + startTime + "|" + endTime;
    }
}
