# java -cp . -jar icbms.jar icbm.ICBM
CLASSPATH=.
java -cp . -Djava.rmi.server.codebase=file:. icbm.MUDServer ICBM
