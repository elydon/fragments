# fragments
Fragments is a combination of wikipedia and twitter, as it shall consist of articles that explain and define anything you want, but its size and contents are limited. Such an article is called a fragment.
A fragment consists of the following parts:
* a header (mandatory, max. 100 characters)
* a text (mandatory, max. 2000 characters)
* an image (optional)
* a source URL (optional)

## project layout

The whole system consists of several projects, which can be combined to get a running application. The most important project is `fragments-core`, which contains the main classes of a fragments system:
* The `Main` class (including the main method)
* The `Fragment` class
* The `FragmentManager` interface
* The `Application` interface

When starting the system via running the main method of the `Main` class, the class path (or the current path, if the class path root cannot be found) is scanned for a class that implements the `Application` interface, which gets instantiated and its `setup()` method is called. The `Application` is responsible for setting up the fragments system. It is implementation dependent how the various components get assembled to a whole fragments system, but usually it is needed to configure the class path in a way the JVM finds all necessary classes. The `SimpleApplication` class is a good starting point for your own implementation of an `Application`.

Now let's take a closer look on the other projects.

#### fragments-application-simple-tomcat

Offers an implementation of `Application` that starts an embedded Tomcat. All WAR archives on the current path get deployed as web applications. As stated above, the JAR file of this project has to be in the class path of the JVM when calling the main method.

#### fragments-store-memory

A very simple implementation of the `FragmentManager` that stores each and every fragment in memory. After shutting down the fragments system, all fragments are lost. Useful in development phase to always have a clean system after startup.

#### fragments-webservice-tomcat

A WAR archive that can be run by `fragments-application-simple-tomcat`. It offers a simple web interface to interact with the `FragmentManager` of the system. Images of the fragments are handled by `ImageServlet` and all fragment related stuff by `FragmentServlet`.

#### fragments-ui-simple-tomcat

A WAR archive that offers a very simple and minimalistic web GUI to manage fragments, which uses the web interface of `fragments-webservice-tomcat` under the hood.

## setting up a simple fragments system

To get a very simple fragments system up and running, you have to build several projects (I am assuming you know how to build with gradle):
* JAR files of 
  * `fragments-core`
  * `fragments-application-simple-tomcat`
  * `fragments-store-memory`
* WAR files of
  * `fragments-webservice-tomcat`
  * `fragments-ui-simple-tomcat`

Throw all files in a single directory and type (watch the version):
```shell
java -jar fragments-core-1.0.0-SNAPSHOT.jar
```

This will start the embedded Tomcat as application and take the `MemoryFragmentManager` as the `FragmentManager`. After successfully launching the application, you can open a browser and head over to `http://localhost:8080/ui-simple` ... and enjoy!

## okay, fine ... but why?

I have several reasons for this project, but let's consider the most important ones.

### 1. i want to learn

The project layout allows to try different technologies to solve the same problem. The task the project tries to solve is simple, yet challenging enough to be more than the common "Hello, world!" example. So I am forced to struggle more with the technologies I want to try than to copy and paste some code or configuration snippets from a tutorial page (at least I hope so).

### 2. it should be useful

Some projects that are set up to learn starve because they are ... well, just set up to learn. Once done or even before reaching the end of the learn process, they are aborted because of lack of interest. At least mine.
I need a project that is useful for myself. And I figured out, I sometimes need a tool for saving short explanations of topics or solutions to problems. That is a little different from just taking notes, because fragments can also save images and can be related to each other.
