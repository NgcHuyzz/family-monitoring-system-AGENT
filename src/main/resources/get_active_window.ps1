# Lấy tiêu đề cửa sổ đang active (Unicode an toàn)

Add-Type @"
using System;
using System.Runtime.InteropServices;
using System.Text;

public static class WinAPIW {
    [DllImport("user32.dll")]
    public static extern IntPtr GetForegroundWindow();

    [DllImport("user32.dll", CharSet = CharSet.Unicode, SetLastError=true)]
    public static extern int GetWindowTextW(IntPtr hWnd, StringBuilder lpString, int nMaxCount);
}
"@

$hWnd = [WinAPIW]::GetForegroundWindow()
if ($hWnd -eq [IntPtr]::Zero) { return }

$sb = New-Object System.Text.StringBuilder 1024
[void][WinAPIW]::GetWindowTextW($hWnd, $sb, $sb.Capacity)
$sb.ToString()
