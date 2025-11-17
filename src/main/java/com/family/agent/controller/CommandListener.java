package com.family.agent.controller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandListener extends Thread {
    private final Socket soc;

    public CommandListener(Socket soc) {
        this.soc = soc;
    }

    @Override
    public void run() {
        System.out.println("Starting CommandListener...");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

            // thông báo Agent đã online
            out.println("{\"status\":\"ONLINE\"}");

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("[Agent] received command: " + msg);

                JSONObject json = new JSONObject(msg);
                String action = json.getString("action");   // chỉ cần action

                switch (action) {
                    case "KILL_APP":
                        System.out.println("[Agent] Executing KILL_APP");
                        SystemControl.KillActiveProcess();  // không cần target
                        break;

                    case "SHUTDOWN":
                        System.out.println("[Agent] Executing SHUTDOWN");
                        SystemControl.shutdownComputer();
                        break;

                    case "RESTART":
                        System.out.println("[Agent] Executing RESTART");
                        SystemControl.restartComputer();
                        break;

                    case "LOCKSCREEN":
                        System.out.println("[Agent] Executing LOCKSCREEN");
                        SystemControl.lockScreen();
                        break;

                    default:
                        System.out.println("[Agent] Unknown action " + action);
                }
            }

            System.out.println("[Agent] Command socket closed by server");
        } catch (Exception e) {
            System.out.println("[Agent] CommandListener error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
