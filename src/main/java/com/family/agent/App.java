package com.family.agent;

import com.family.agent.network.Uploader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Uploader u = new Uploader();
        u.start();
    }
}


