## No lwjgl in java.library.path ##
#### at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1682) ####

The Bui tests will give this error if you have correctly specified lwjgl and the other jME requirements, but not specified lwjgl native library path.

This can be Done in Eclipse through:

run/debug dialog > Arguments(tab) > VM-Arguments :

-Djava.library.path="E:\randomPath\workspace\jme\_directory\lib"

**Or** ( if your not using Maven )

In Project Properties > Java Build Path > Libraries (tab) > lwjgl > Native Library Location

### Exception ###
```
java.lang.UnsatisfiedLinkError: no lwjgl in java.library.path
	at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1682)
	at java.lang.Runtime.loadLibrary0(Runtime.java:823)
	at java.lang.System.loadLibrary(System.java:1030)
	at org.lwjgl.Sys$1.run(Sys.java:72)
	at java.security.AccessController.doPrivileged(Native Method)
	at org.lwjgl.Sys.doLoadLibrary(Sys.java:65)
	at org.lwjgl.Sys.loadLibrary(Sys.java:81)
	at org.lwjgl.Sys.<clinit>(Sys.java:98)
	at org.lwjgl.opengl.Display.<clinit>(Display.java:128)
	at com.jme.system.lwjgl.LWJGLDisplaySystem.initDisplay(LWJGLDisplaySystem.java:429)
	at com.jme.system.lwjgl.LWJGLDisplaySystem.createWindow(LWJGLDisplaySystem.java:143)
	at com.jme.app.BaseSimpleGame.initSystem(BaseSimpleGame.java:344)
	at com.jme.app.BaseGame.start(BaseGame.java:65)
	at com.jmex.bui.tests.LineBreakTest.main(LineBreakTest.java:80)
```

## java.lang.NullPointerException ##
#### at java.io.Reader.

&lt;init&gt;

 ####

This ocurs because the system cannot find the style sheet or a other resource.

### Exception ###
```
java.lang.NullPointerException
	at java.io.Reader.<init>(Unknown Source)
	at java.io.InputStreamReader.<init>(Unknown Source)
	at com.jmex.bui.tests.BaseTest.simpleInitGame(BaseTest.java:53)
	at com.jme.app.BaseSimpleGame.initGame(BaseSimpleGame.java:506)
	at com.jme.app.BaseGame.start(BaseGame.java:69)
	at com.jmex.bui.tests.LineBreakTest.main(LineBreakTest.java:81)
```