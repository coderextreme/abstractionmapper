Gedda (motion/gedda), Internet Chat by MUD (dev), and Motion (motion).

Gedda may contain proprietary code written by John Carlson.  The rest are
owned by John Carlson

Gedda was the original, then ICBM was developed, then Motion (metadata search) was a new implementation of Gedda (incomplete).

First install maven, set JAVA_HOME, then run:
```
bash ./contacts.sh
```
or
```
bash ./icbmmotion.sh
```
or
```
mvn clean install
ant prop
```
or
```
mvn clean install
ant contacts
```
or
```
mvn clean javafx:run
```
This doesn't currently run JavaFX (Note that you won't be able to launch any web browser with selenium using this!

```
You may see errors, and this code is very pre-alpha quality
Checkout these special properties, per object
PROP_A		= "a";  // multiple selection
PROP_COLLABORATIONSERVER = "CollaborationServer";  // name of website for running JSONVerse
PROP_SESSIONNAME	= "s"; // Session/Group name for JSONverse.
PROP_SESSIONPASSWORD	= "o"; // password/token for JSONverse
PROP_WEBSOCKET	= "WebSocket"; // web socket fro collaboration server for JSONverse (often null)
PROP_PROPERTYFILE	= "property_file"; // file name to load session descriptions from (SDs), for a, o, and s above.
```
