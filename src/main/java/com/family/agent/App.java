package com.family.agent;

import com.family.agent.scheduler.Scheduler;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Scheduler scheduler = new Scheduler();
        scheduler.start();
    }
}
