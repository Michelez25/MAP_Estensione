@echo off
title QT Client - JavaFX GUI
echo Avvio interfaccia grafica...
set LIB_DIR=lib
java -Dprism.order=sw ^
     -Djava.library.path=%LIB_DIR% ^
     --module-path %LIB_DIR% ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp "bin;mysql-connector-java-8.0.17.jar" ^
     gui.MainGui

pause