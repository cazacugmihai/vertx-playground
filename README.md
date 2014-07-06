# Vert.x playground project

Simple project to play around with Vert.x, included modules and customizations:

* SLF4J and logback for logging
* Guice for verticle injection
* SockJS event bus bridge, including a test do demonstrate raw access over websocket

## Create and run a fat JAR

```bash
./gradlew fatJar
```

```bash
java -Dorg.vertx.logger-delegate-factory-class-name=org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory -jar ./build/libs/vertx-playground-0.0.1-fat.jar runmod de.frostcode~vertx-playground~0.0.1
```

## IDEA run configuration

**Main class:** `org.vertx.java.platform.impl.cli.Starter`  
**VM options:** `-Dvertx.langs.java=com.englishtown~vertx-mod-guice~1.3.0-final:com.englishtown.vertx.guice.GuiceVerticleFactory -Dorg.vertx.logger-delegate-factory-class-name=org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory -Dlogback.configurationFile=./classes/production/vertx-playground/platform_lib/logback.xml`  
**Program arguments:** `runmod de.frostcode~vertx-playground~0.0.1 -cp ./classes/production/vertx-playground`  
**Working directory:** `./build`  
**Use classpath of module:** Create an IDEA module with the runtime JARs of Vert.x (basically the dependencies scoped as `provided` of the main module) as `compile` scoped dependencies.
