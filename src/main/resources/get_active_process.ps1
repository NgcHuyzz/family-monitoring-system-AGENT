Add-Type -TypeDefinition @"
using System;
using System.Runtime.InteropServices;

public class WinAPI {
    [DllImport("user32.dll")]
    public static extern IntPtr GetForegroundWindow();

    [DllImport("user32.dll")]
    public static extern int GetWindowThreadProcessId(IntPtr hWnd, out uint processId);
}
"@

$hWnd = [WinAPI]::GetForegroundWindow()

$processId = 0
[WinAPI]::GetWindowThreadProcessId($hWnd, [ref]$processId) | Out-Null

$proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
if ($proc) {
    Write-Output $proc.ProcessName
} else {
    Write-Output "Unknown"
}