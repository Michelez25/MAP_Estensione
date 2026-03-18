@echo off
title QT Server - MultiServer Console
echo Avvio del Server...
java -cp "bin;mysql-connector-java-8.0.17.jar" server.MultiServer 8080

pause
