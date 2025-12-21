' ================================
' FamilyAgent - run_agent.vbs
' Chạy agent trong chế độ ẩn + ép thư mục tạm + 1 file log có giới hạn
' ================================

Option Explicit

Dim shell, fso, base, data, logDir, logFile, cmd
Set shell = CreateObject("WScript.Shell")
Set fso   = CreateObject("Scripting.FileSystemObject")

' --- Thư mục cài đặt (jre, agent.jar) ---
base = "C:\Program Files\FamilyAgent"

' --- Thư mục ghi tạm (DLL/native/tmp) ---
data = "C:\ProgramData\FamilyAgent\tmp"
If Not fso.FolderExists(data) Then fso.CreateFolder(data)

' --- Thư mục & file log (duy nhất) ---
logDir  = "C:\ProgramData\FamilyAgent\logs"
If Not fso.FolderExists(logDir) Then fso.CreateFolder(logDir)
logFile = logDir & "\agent.log"

' --- Rotate đơn giản: nếu log > 5 MB thì đổi tên sang agent.YYYYMMDD-HHMMSS.log ---
If fso.FileExists(logFile) Then
  If fso.GetFile(logFile).Size > (5 * 1024 * 1024) Then
    On Error Resume Next
    Dim t, rotated
    t = Now
    rotated = logDir & "\agent." & _
              Year(t) & Right("0" & Month(t),2) & Right("0" & Day(t),2) & "-" & _
              Right("0" & Hour(t),2) & Right("0" & Minute(t),2) & Right("0" & Second(t),2) & ".log"
    fso.MoveFile logFile, rotated
    On Error GoTo 0
  End If
End If

' --- Chống chạy trùng: nếu đã có java.exe agent.jar thì thoát ---
If IsAgentRunning() Then WScript.Quit 0

' --- Ép working dir về thư mục data (giống cách bạn đang làm) ---
'     và ghi log duy nhất vào ProgramData\FamilyAgent\logs\agent.log
cmd = "cmd /c cd /d """ & data & """ && " & _
      """" & base & "\jre\bin\java.exe"" " & _
      "-Djava.io.tmpdir=""" & data & """ " & _
      "-Djnativehook.lib.location=""" & data & """ " & _
      "-Djnativehook.lib.path=""" & data & """ " & _
      "-Dorg.jnativehook.lib.location=""" & data & """ " & _
      "-Djna.tmpdir=""" & data & """ " & _
      "-Duser.dir=""" & data & """ " & _
      "-Dconfig.path=""C:\ProgramData\FamilyAgent\config.json"" " & _
      "-jar """ & base & "\agent.jar"" " & _
      ">> """ & logFile & """ 2>>&1"

' 0 = ẩn cửa sổ hoàn toàn
shell.Run cmd, 0, False
WScript.Quit 0

' -------- Helpers --------
Function IsAgentRunning()
  On Error Resume Next
  Dim svc, procs, p
  Set svc = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
  Set procs = svc.ExecQuery("SELECT CommandLine FROM Win32_Process WHERE Name='java.exe'")
  For Each p In procs
    If Not IsNull(p.CommandLine) Then
      If InStr(LCase(p.CommandLine), "agent.jar") > 0 Then
        IsAgentRunning = True
        Exit Function
      End If
    End If
  Next
  IsAgentRunning = False
End Function
