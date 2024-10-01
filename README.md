Gedda (motion/gedda), Internet Chat by MUD (dev), and Motion (motion).

Gedda may contain proprietary code written by John Carlson.  The rest are
owned by John Carlson

Gedda was the original, then ICBM was developed, then Motion (metadata search) was a new implementation of Gedda (incomplete).

to run:

```
cd dev
make redokey
make
```

```
cd motion
make
```

```
cd motion/gedda
make
make run
```

You may see errors, and this code is very pre-alpha quality
Checkout these special properties, per object
PROP_A		= "a";  // multiple selection
PROP_COLLABORATIONSERVER = "CollaborationServer";  // name of website for running JSONVerse
PROP_SESSIONNAME	= "s"; // Session/Group name for JSONverse.
PROP_SESSIONPASSWORD	= "o"; // password/token for JSONverse
PROP_WEBSOCKET	= "WebSocket"; // web socket fro collaboration server for JSONverse (often null)
PROP_PROPERTYFILE	= "property_file"; // file name to load session descriptions from (SDs), for a, o, and s above.
