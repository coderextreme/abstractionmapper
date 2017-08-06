PARENT=dev/objects.properties
DIST= Makefile client.bat client.sh client2.bat client2.sh client3.bat icbm.bat icbms.jar reg.bat reg.sh server.bat server.sh icbms.jar icbmc.jar icbm.start
THINDIST=Makefile LocalMUDClient.java LocalMUDClient.class LocalMUDServer.java LocalMUDServer.class thin.bat
KEYSTORE=../mykeystore

.SUFFIXES: .class .java

.java.class:
	javac -g $<
test:
	(cd ..; rmiregistry &)
	(cd ../icbm; vi MUDObject.java; make)
	-rm *jar
	make all
	java -cp .. -Djava.rmi.server.codebase=file:.. icbm.MUDServer ICBM
	# java -cp .. -Djava.rmi.server.codebase=file:.. -jar icbms.jar icbm.ICBM


all: $(DIST) $(THINDIST) ../$(PARENT)
	jar -cf icbm.0.7.6.zip $(DIST) -C .. $(PARENT)
	jar -cf thin.0.1.zip $(THINDIST) -C .. $(PARENT)

icbmc.jar:
	jar -cmf icbm07.manifest.client  icbmc.jar -C .. icbm -C .. $(PARENT)
	jarsigner -keystore $(KEYSTORE) icbmc.jar myself

icbms.jar:
	jar -cmf icbm07.manifest.server icbms.jar -C .. icbm -C .. $(PARENT)

redokey:
	-keytool -delete -keystore $(KEYSTORE) -alias myself
	keytool -genkey -keystore $(KEYSTORE) -alias myself
	keytool -selfcert -keystore $(KEYSTORE) -alias myself
