set CLASSPATH="target\classes;selenium-java\selenium-devtools-v130-4.26.0.jar;selenium-java\selenium-os-4.26.0.jar;selenium-java\selenium-json-4.26.0.jar;selenium-java\selenium-manager-4.26.0.jar;selenium-java\failsafe-3.3.2.jar;selenium-java\selenium-http-4.26.0.jar;selenium-java\selenium-api-4.26.0.jar;selenium-java\selenium-chrome-driver-4.26.0.jar;selenium-java\selenium-chromium-driver-4.26.0.jar;selenium-java\selenium-edge-driver-4.26.0.jar;selenium-java\selenium-firefox-driver-4.26.0.jar;selenium-java\selenium-ie-driver-4.26.0.jar;selenium-java\selenium-remote-driver-4.26.0.jar;selenium-java\selenium-safari-driver-4.26.0.jar;flatlaf-3.5.1.jar"
mvn clean install && java -cp %CLASSPATH% net.coderextreme.main.App src/main/java/net/coderextreme/motion/prop.data target/classes/net/coderextreme/motion/gedda/
