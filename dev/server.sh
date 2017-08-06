export CLASSPATH=".;.."
rmiregistry &
# java -cp %CLASSPATH% -jar icbms.jar icbm.ICBM
java -cp %CLASSPATH% -Djava.rmi.server.codebase=file:///C:/Users/coderextreme/abstractionmapper icbm.MUDServer ICBM
