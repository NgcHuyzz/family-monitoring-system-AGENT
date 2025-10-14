package com.family.agent.scheduler;

import com.family.agent.collector.AppCollector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private AppCollector appCollector;

    public void start()
    {
        appCollector = new AppCollector();
        scheduler.scheduleAtFixedRate(appCollector, 0, 5, TimeUnit.SECONDS);
        System.out.println("Scheduler started; thu thap du lieu ve ung dung 5 giay 1 lan");
    }

    public AppCollector getAppCollector()
    {
        return appCollector;
    }
}
