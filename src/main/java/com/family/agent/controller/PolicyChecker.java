package com.family.agent.controller;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.family.agent.model.PolicyEntry;

public class PolicyChecker {
	private final PolicyEntry pe;

    public PolicyChecker(PolicyEntry pe) 
    {
        this.pe = pe;
    }

    public boolean isDomainBlocked(String appName) 
    {
        if (appName == null) return false;
        String lower = appName.toLowerCase();

        for (String domainBlack : pe.getDomainBlacklist()) 
            if (lower.contains(domainBlack)) 
                return true;
        return false;
    }

    public boolean isInQuietTime(DayOfWeek today, LocalTime now) 
    {
        for (PolicyEntry.QuietRule rule : pe.getQuietRules()) 
            if (rule.match(today, now)) 
                return true;
            
        return false;
    }

    public boolean isAppWhitelisted(String appName) 
    {
        return pe.getAppWhitelist().contains(appName.toLowerCase());
    }

    public boolean isOverDailyQuota(int usedMinutes) 
    {
        int quota = pe.getDailyQuotaMinutes();
        if (quota <= 0) return false; 
        return usedMinutes > quota;
    }

    public int getDailyQuotaMinutes() 
    {
        return pe.getDailyQuotaMinutes();
    }
}
