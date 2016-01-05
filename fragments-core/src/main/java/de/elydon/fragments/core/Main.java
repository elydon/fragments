package de.elydon.fragments.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * <p>
 * Main class of fragments. Starts the {@link Application}.
 * </p>
 * <p>
 * The class path (or the current directory, if no class path root is found)
 * will be scanned for a class that implements {@link Application}. The main
 * class instantiates an object (it needs a parameter-less constructor) of the
 * application and calls {@link Application#setup() setup}.
 * </p>
 * <p>
 * If not exactly one class implementing {@link Application} is found, the
 * program exits with an error.
 * </p>
 * <p>
 * The setup method sets up the whole environment. It is implementation
 * dependent how the various components get assembled to a whole fragments
 * system, but usually it is needed to configure the class path in a way the JVM
 * finds all necessary classes. Please refer to the implementation of the
 * application class for more detailed information.
 * </p>
 * <p>
 * The several components of the application can receive the running
 * {@link Application} instance using {@link Main#getApplication()}.
 * </p>
 * 
 * @author elydon
 *
 */
public class Main {

	private static Application application;

	@SuppressWarnings("unchecked")
	public static void main(final String[] args) {
		// scan for classes implementing Application, starting at current class
		// loader's root
		Path classPathRoot;
		try {
			classPathRoot = Paths.get(Main.class.getResource("/").toURI());
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
			// return needed to satisfy compiler ... otherwise the variable is
			// not initialized :/
			return;
		} catch (final NullPointerException e) {
			System.out.println("Class path root cannot be found, taking current directory");
			classPathRoot = Paths.get(".");
		}
		classPathRoot = classPathRoot.toAbsolutePath();
		System.out.println("root: " + classPathRoot);

		final Map<Class<?>, URL> applicationClasses = ClassScanner.scan(Main.class.getClassLoader(),
				new ImplementingClassFilter(Application.class), classPathRoot);
		if (applicationClasses.isEmpty()) {
			System.err.println("Found no class implementing " + Application.class.getCanonicalName());
			System.exit(1);
		} else if (applicationClasses.size() != 1) {
			System.err.println("Found more than one class implementing " + Application.class.getCanonicalName() + ":");
			for (final Class<?> clazz : applicationClasses.keySet()) {
				System.err.println("  " + clazz.getCanonicalName());
			}
			System.err.println("Cannot decide which one to use, exiting");
			System.exit(1);
		}

		// construct proper class loader to load the application class
		// this is necessary, as they probably want to load other stuff from the
		// class path
		ClassLoader classLoader = Main.class.getClassLoader();
		URL url = null;
		Class<Application> applicationClass = null;
		for (final Map.Entry<Class<?>, URL> entry : applicationClasses.entrySet()) {
			applicationClass = (Class<Application>) entry.getKey();
			url = entry.getValue();
			// no break necessary, we only have one value, see if statements
			// above
		}
		if (url != null) {
			classLoader = new URLClassLoader(new URL[] { url }, classLoader);

			// we have to reload the class with the "right" class loader,
			// otherwise the
			// class loader of the Main class will be used
			try {
				applicationClass = (Class<Application>) classLoader.loadClass(applicationClass.getCanonicalName());
			} catch (final ClassNotFoundException e) {
				// may not happen ... really
				e.printStackTrace();
			}
		}

		try {
			System.out.println("Instantiating application: " + applicationClass.getCanonicalName());
			application = applicationClass.newInstance();
			System.out.println("Setting up application");
			final Thread applicationThread = application.setup();
			if (applicationThread == null) {
				throw new IllegalStateException("Thread of the application is null");
			}

			// start thread if not already running
			if (!applicationThread.isAlive()) {
				applicationThread.start();
			}

			// wait until the application thread is dead
			applicationThread.join();
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (final InterruptedException e) {
		} catch (final Exception e) {
			System.err.println("Setting up the application failed");
			e.printStackTrace();
			System.exit(1);
		} finally {
			// remember to close the URLClassLoader, if used
			if (classLoader instanceof URLClassLoader) {
				try {
					((URLClassLoader) classLoader).close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Application getApplication() {
		return application;
	}
}
