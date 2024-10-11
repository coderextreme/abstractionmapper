set CLASSPATH="..;../dev;."
start rmiregistry
start java -cp %CLASSPATH% icbm.MUDServer ICBM
sleep 5
start java -cp %CLASSPATH% motion.Motion prop.data gedda/
