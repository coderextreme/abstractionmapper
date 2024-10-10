#!/bin/bash
export CLASSPATH=".;selenium-java/selenium-devtools-v129-4.25.0.jar;selenium-java/selenium-os-4.25.0.jar;selenium-java/selenium-json-4.25.0.jar;selenium-java/selenium-manager-4.25.0.jar;selenium-java/failsafe-3.3.2.jar;selenium-java/selenium-http-4.25.0.jar;selenium-java/selenium-api-4.25.0.jar;selenium-java/selenium-chrome-driver-4.25.0.jar;selenium-java/selenium-chromium-driver-4.25.0.jar;selenium-java/selenium-edge-driver-4.25.0.jar;selenium-java/selenium-firefox-driver-4.25.0.jar;selenium-java/selenium-ie-driver-4.25.0.jar;selenium-java/selenium-remote-driver-4.25.0.jar;selenium-java/selenium-safari-driver-4.25.0.jar;flatlaf-3.5.1.jar"
javac */*java
java main.App
