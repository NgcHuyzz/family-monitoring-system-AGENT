Add-Type -Name WinAPI -Namespace Native -MemberDefinition '
[DllImport("user32.dll")] public static extern IntPtr GetForegroundWindow();
[DllImport("user32.dll")] public static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count);';

$h = [Native.WinAPI]::GetForegroundWindow()
$b = New-Object System.Text.StringBuilder 256
[Native.WinAPI]::GetWindowText($h, $b, $b.Capacity) | Out-Null
Write-Output $b.ToString()
