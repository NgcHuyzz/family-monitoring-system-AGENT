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
"@ -ErrorAction SilentlyContinue

$hWnd = [WinAPI]::GetForegroundWindow()
if ($hWnd -eq [IntPtr]::Zero) { return }

# ❌ Không dùng $pid vì đụng $PID (read-only)
$winPid = [uint32]0

# Vì hàm đã khai báo "out uint processId" nên gọi trực tiếp kiểu out như này là đúng
[void][WinAPI]::GetWindowThreadProcessId($hWnd, [ref]$winPid)
if ($winPid -eq 0) { return }

try {
    $proc = Get-Process -Id $winPid -ErrorAction Stop
    $proc.ProcessName
} catch {
    ""
}
