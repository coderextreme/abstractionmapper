CLASSFILES = MUDServer.class \
	MUDClient.class \
	Config.class \
	InputLine.class \
	InventoryItem.class \
	MUDObject.class \
	MUDObject_Stub.class \
	MUDObject_Skel.class \
	MUDRemote.class \
	MUDRoom.class \
	MountainTop.class \
	ParentItem.class \
	Valley.class \
	Hyperactive.class \
	FromMUD.class \
	SendText.class \
	CommandThread.class

.SUFFIXES: .class .java

.java.class:
	javac -classpath .. -g $<

all: $(CLASSFILES)

MUDObject_Skel.class: MUDObject.class
	# rmic -classpath .. -d .. icbm.MUDObject

MUDObject_Stub.class: MUDObject.class
	# rmic -classpath .. -d .. icbm.MUDObject

clean:
	rm *.class
