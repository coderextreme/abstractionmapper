set CLASSPATH=.
start rmiregistry
sleep 5
start java icbm.MUDServer ICBM
sleep 5
start java motion.Motion motion/prop.data motion/gedda/
