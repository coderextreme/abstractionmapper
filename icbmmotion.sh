export CLASSPATH=.
rmiregistry &
sleep 5s
java icbm.MUDServer ICBM &
sleep 5s
java motion.Motion motion/prop.data motion/gedda/
