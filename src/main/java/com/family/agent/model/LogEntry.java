package com.family.agent.model;

import java.sql.Time;
import java.sql.Timestamp;

public class LogEntry {
    private String type;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;

    public LogEntry(){}
    public  LogEntry(String type, String name, Timestamp startTime, Timestamp endTime)
    {
        this.type = type;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getType() {return type;}
    public String getName() {return name;}
    public Timestamp getStartTime() {return startTime;}
    public Timestamp getEndTime() {return endTime;}

    public void setType(String type) {this.type = type;}
    public void setName(String name) {this.name = name;}
    public void setStartTime(Timestamp startTime) {this.startTime = startTime;}
    public void setEndTime(Timestamp endTime) {this.endTime = endTime;}

    public void print() {
        System.out.printf("[%s] %s | %s → %s%n",
                type, name, startTime, endTime);
    }
    public String toString()
    {
        return type + "|" + name + "|" + startTime + "|" + endTime;
    }
}
