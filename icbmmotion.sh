export CLASSPATH=dev:.
rmiregistry &
sleep 5
java icbm.MUDServer ICBM &
sleep 5
java motion.Motion motion/prop.data motion/gedda/
