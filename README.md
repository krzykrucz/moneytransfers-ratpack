This is a sample microservice for money transfers powered by Ratpack server with Guice. It was developed according to DDD's good practices.

## Running

Check this project out, cd into the directory and run:

    ./gradlew run

This will start the ratpack app in a development mode. In your browser go to `http://localhost:5050`.

The Gradle Ratpack plugin builds on the Gradle Application plugin. This means it's easy to create a standalone
distribution for your app.

Run:

    ./gradlew installApp
    cd build/install/example-ratpack-gradle-java-app
    bin/example-ratpack-gradle-java-app

Your app should now be running (see http://gradle.org/docs/current/userguide/application_plugin.html) for more on what
the Gradle application plugin can do for you.

## Event storming

![alt text](https://raw.githubusercontent.com/krzykrucz/moneytransfers-ratpack/master/ES.jpg)