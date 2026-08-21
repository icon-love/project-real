@echo off
chcp 65001 >nul
set DB_PASSWORD=123456
cd /d "%~dp0"

where mvn >nul 2>nul
if %errorlevel%==0 (
    mvn -f pom.xml org.springframework.boot:spring-boot-maven-plugin:3.3.5:run
) else if exist "E:\tools\maven\apache-maven-3.9.16\bin\mvn.cmd" (
    call "E:\tools\maven\apache-maven-3.9.16\bin\mvn.cmd" -f pom.xml org.springframework.boot:spring-boot-maven-plugin:3.3.5:run
) else (
    echo 未找到 Maven，请安装 mvn 或修改脚本中的路径。
    pause
)
