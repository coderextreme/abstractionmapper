export CLASSPATH
CLASSPATH=..:../dev:.
rmiregistry &
java -cp $CLASSPATH icbm.MUDServer ICBM &
sleep 5
java -cp $CLASSPATH motion.Motion prop.data gedda/
