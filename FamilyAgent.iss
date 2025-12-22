#define AppName "FamilyAgent"
#define AppVersion "1.0.0"
#define AppPublisher "Your Team/School"
#define InstallDir "{pf}\FamilyAgent"
#define DataDir "{commonappdata}\FamilyAgent"

[Setup]
AppId={{F0A5A2B7-3C4E-4C7A-9E7E-FAFA12345678}
AppName={#AppName}
AppVersion={#AppVersion}
DefaultDirName={#InstallDir}
DefaultGroupName={#AppName}
OutputBaseFilename=FamilyAgent-Setup-{#AppVersion}
Compression=lzma
SolidCompression=yes
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
PrivilegesRequired=admin
LicenseFile=dist\README.txt

[Dirs]
Name: "{#DataDir}"; Permissions: users-modify

[Files]
Source: "dist\agent.jar"; DestDir: "{#InstallDir}"; Flags: ignoreversion
Source: "dist\run_agent.vbs"; DestDir: "{#InstallDir}"; Flags: ignoreversion
Source: "dist\README.txt"; DestDir: "{#InstallDir}"; Flags: ignoreversion
Source: "dist\jre\*"; DestDir: "{#InstallDir}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\FamilyAgent.task.xml"; DestDir: "{tmp}"; Flags: deleteafterinstall

[Run]
; --- Copy config.json nằm cạnh file setup.exe vào ProgramData ---
Filename: "{cmd}"; \
  Parameters: "/C if exist ""{src}\config.json"" (copy /Y ""{src}\config.json"" ""{#DataDir}\config.json"") else (echo No config.json next to setup: {src})"; \
  Flags: runhidden
  
Filename: "schtasks.exe"; \
  Parameters: "/Create /F /TN ""FamilyAgent"" /XML ""{tmp}\FamilyAgent.task.xml"""; \
  Flags: runhidden

; Chạy ngay sau cài (để test)
Filename: "wscript.exe"; Parameters: """{#InstallDir}\run_agent.vbs"""; Flags: nowait postinstall runhidden skipifsilent

[UninstallRun]
; Gỡ Scheduled Task khi uninstall
Filename: "schtasks.exe"; Parameters: "/Delete /TN ""FamilyAgent"" /F"; Flags: runhidden
