# Lấy tên tiến trình của cửa sổ đang active (Chrome, eclipse, ...)

Add-Type @"
using System;
using System.Runtime.InteropServices;

public static class WinAPI {
    [DllImport("user32.dll")]
    public static extern IntPtr GetForegroundWindow();

    [DllImport("user32.dll")]
    public static extern int GetWindowThreadProcessId(IntPtr hWnd, out uint processId);
}
"@

$hWnd = [WinAPI]::GetForegroundWindow()
if ($hWnd -eq [IntPtr]::Zero) { return }

$pid = 0
[void][WinAPI]::GetWindowThreadProcessId($hWnd, [ref]$pid)
if ($pid -eq 0) { return }

try {
    $proc = Get-Process -Id $pid -ErrorAction Stop
    $proc.ProcessName
} catch {
    ""
}
