set CLASSPATH="dev;."
start rmiregistry
start java -cp %CLASSPATH% -Djava.rmi.server.codebase="file:///C:/Users/coderextreme/abstractionmapper" icbm.MUDServer ICBM
start java -cp %CLASSPATH% motion.Motion -Djava.rmi.server.codebase="file:///C:/Users/coderextreme/abstractionmapper" motion/prop.data motion\gedda\
