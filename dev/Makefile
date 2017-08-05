DIST= Makefile client.bat client.sh client2.bat client2.sh client3.bat icbm icbm.bat icbms.jar objects.properties reg.bat reg.sh server.bat server.sh icbms.jar icbmc.jar icbm.start
THINDIST=Makefile objects.properties LocalMUDClient.java LocalMUDClient.class LocalMUDServer.java LocalMUDServer.class thin.bat
KEYSTORE=../mykeystore

.SUFFIXES: .class .java

.java.class:
	javac -g $<
test:
	(cd icbm; vi MUDObject.java; make)
	-rm *jar
	make all
	./server.sh


all: $(DIST) $(THINDIST)
	jar -cf icbm.0.7.6.zip $(DIST)
	jar -cf thin.0.1.zip $(THINDIST)

icbmc.jar:
	jar -cmf icbm07.manifest.client  icbmc.jar icbm objects.properties
	jarsigner -keystore $(KEYSTORE) icbmc.jar myself

icbms.jar:
	jar -cmf icbm07.manifest.server icbms.jar icbm objects.properties

redokey:
	-keytool -delete -keystore $(KEYSTORE) -alias myself
	keytool -genkey -keystore $(KEYSTORE) -alias myself
	keytool -selfcert -keystore $(KEYSTORE) -alias myself
