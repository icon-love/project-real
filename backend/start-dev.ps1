# 一键启动后端（自动设置数据库密码）
# 用法：右键 -> 使用 PowerShell 运行；或在终端执行 .\start-dev.ps1
$ErrorActionPreference = "Stop"
$env:DB_PASSWORD = "123456"
$root = $PSScriptRoot

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    & mvn -f "$root\pom.xml" org.springframework.boot:spring-boot-maven-plugin:3.3.5:run
} elseif (Test-Path "E:\tools\maven\apache-maven-3.9.16\bin\mvn.cmd") {
    & "E:\tools\maven\apache-maven-3.9.16\bin\mvn.cmd" -f "$root\pom.xml" org.springframework.boot:spring-boot-maven-plugin:3.3.5:run
} else {
    Write-Host "未找到 Maven，请安装 mvn 或修改脚本中的路径。" -ForegroundColor Red
    Read-Host "按回车退出"
}
