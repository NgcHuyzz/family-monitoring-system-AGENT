package com.family.agent.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import com.family.agent.util.FilePath;

public class PolicyEntry {

    public static class QuietRule {
        public DayOfWeek day;
        public LocalTime start;
        public LocalTime end;

        public QuietRule(DayOfWeek day, LocalTime start, LocalTime end) {
            this.day = day;
            this.start = start;
            this.end = end;
        }

        public boolean match(DayOfWeek today, LocalTime now) {
        	if (today != this.day) return false;

            if (!end.isBefore(start)) {
                return !now.isBefore(start) && !now.isAfter(end);
            }
            
            return !now.isBefore(start) || !now.isAfter(end);
        }
    }

    private Set<String> appWhitelist = new HashSet<>();
    private Set<String> domainBlacklist = new HashSet<>();
    private List<QuietRule> quietRules = new ArrayList<>();
    private int dailyQuotaMinutes = 0; 

    public Set<String> getAppWhitelist()     { return appWhitelist; }
    public Set<String> getDomainBlacklist()  { return domainBlacklist; }
    public List<QuietRule> getQuietRules()   { return quietRules; }
    public int getDailyQuotaMinutes()        { return dailyQuotaMinutes; }

    public static PolicyEntry loadFromFolder() throws IOException {
        PolicyEntry ce = new PolicyEntry();

        File fileAppWhite = FilePath.base().resolve("appWhite.txt").toFile();

        if (fileAppWhite.exists()) {
        	BufferedReader br = new BufferedReader(new FileReader(fileAppWhite));
        	String line;
        	while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    ce.appWhitelist.add(line.toLowerCase());
                }
            }
        	br.close();
        }
        File fileDomain   = FilePath.base().resolve("domain.txt").toFile();

        if (fileDomain.exists()) {
        	BufferedReader br = new BufferedReader(new FileReader(fileDomain));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    ce.domainBlacklist.add(line.toLowerCase());
                }
            }
            br.close();
        }

        File fileQuiet    = FilePath.base().resolve("quietHour.txt").toFile();
        if (fileQuiet.exists()) {
        	BufferedReader br = new BufferedReader(new FileReader(fileQuiet));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("-");
                if (parts.length != 2) continue;

                String dayStr = parts[0].trim();           
                DayOfWeek day = parseDayOfWeek(dayStr);

                String timePart = parts[1].trim();        
                timePart = timePart.replace("from", "")
                                   .replace("From", "")
                                   .trim();
                String[] times = timePart.split("to");
                if (times.length != 2) continue;

                LocalTime start = LocalTime.parse(times[0].trim());
                LocalTime end   = LocalTime.parse(times[1].trim());

                ce.quietRules.add(new QuietRule(day, start, end));
            }
            br.close();
        }


        File fileQuote    = FilePath.base().resolve("timeQuote.txt").toFile();
        if (fileQuote.exists()) {
        	BufferedReader br = new BufferedReader(new FileReader(fileQuote));
        	 String line;
             if ((line = br.readLine()) != null) 
             {
                line = line.trim();
                try 
                {
                    ce.dailyQuotaMinutes = Integer.parseInt(line);
                } 
                catch (Exception e) 
                {
                	
                }
            }
             br.close();
        }

        return ce;
    }

    private static DayOfWeek parseDayOfWeek(String s) {
        s = s.trim().toLowerCase();
        if (s.equals("monday")) return DayOfWeek.MONDAY;
        if (s.equals("tuesday")) return DayOfWeek.TUESDAY;
        if (s.equals("wednesday")) return DayOfWeek.WEDNESDAY;
        if (s.equals("thursday")) return DayOfWeek.THURSDAY;
        if (s.equals("friday")) return DayOfWeek.FRIDAY;
        if (s.equals("saturday")) return DayOfWeek.SATURDAY;
        if (s.equals("sunday")) return DayOfWeek.SUNDAY;
        return DayOfWeek.MONDAY;
    }
}
