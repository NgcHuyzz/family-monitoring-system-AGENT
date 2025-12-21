FamilyAgent - README
---------------------------------------

Agent này dùng cho đồ án Family Monitoring System.

Chức năng:
 - Gửi dữ liệu về server
 - Tự động chạy nền khi người dùng đăng nhập Windows
 - Gửi log/chụp màn hình/ghi phím (phụ thuộc code bạn đã cài)

Vị trí lưu log:
  C:\ProgramData\FamilyAgent\agent.log

Cách gỡ:
 - Mở Settings → Apps → Installed apps
 - Tìm "FamilyAgent" → Uninstall
 - Installer sẽ tự xóa tác vụ auto-start trong Task Scheduler

Cách chạy bằng tay:
  wscript.exe "C:\Program Files\FamilyAgent\run_agent.vbs"
  
------------------------------------------------------------------------
Đồ án PBL4 - Family Monitoring System
