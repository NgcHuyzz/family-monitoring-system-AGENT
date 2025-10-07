package com.family.agent.collector;

import com.family.agent.model.LogEntry;
import java.sql.Timestamp;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AppCollector implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(AppCollector.class);
    private String lastApp = null;
    private Timestamp startTime = null;

    @Override
    public void run() {
        String currentApp = getActiveWindowTitle();

        if (currentApp == null || currentApp.isEmpty())
        {
            System.out.println("Khong tim thay ung dung dang su dung");
            return;
        }

        // lan dau khi chay
        if (lastApp == null)
        {
            lastApp = currentApp;
            startTime = new Timestamp(System.currentTimeMillis());
            System.out.println("dang su dung " + lastApp);
            return;
        }

        // neu nguoi dung chuyen app khac
        if (!currentApp.equals(lastApp))
        {
            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            LogEntry log = new LogEntry("app", lastApp, startTime, endTime);
            log.print();

            lastApp = currentApp;
            startTime = new Timestamp(System.currentTimeMillis());
            System.out.println("Chuyen sang ung dung moi " + currentApp);
        }
    }

    private String getActiveWindowTitle()
    {
        char[] buffer = new char[1024];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) return null;

        User32.INSTANCE.GetWindowText(hwnd, buffer, 1024);
        return Native.toString(buffer);
    }
}
