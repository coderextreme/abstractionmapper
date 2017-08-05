set CLASSPATH=.\dev;.
start rmiregistry
start java icbm.MUDServer ICBM
start java motion.Motion motion\prop.data motion\gedda\
