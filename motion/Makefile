.SUFFIXES: .class .java
OBJS= HashtableRemote.class BrowseObject.class BrowseRelationship.class MetaMotion.class Motion.class MoveOp.class Operation.class object_list.class MContainer.class ObjectAtURL.class dnd/TransferActionListener.class

.java.class:
	javac -g -classpath "../dev:..:." $?

run: $(OBJS) .
	# (cd ..; rmic motion.HashtableRemote; cd motion)
	./icbmmotion.sh
# (java -classpath "E:\apps;E:\apps\dev;..;." motion.Motion prop.data gedda\\)

jar:
	jar -cf motion.jar *java prop.data

clean:
	rm *.class

jdb:
	jdb motion.Motion prop.data gedda\\
