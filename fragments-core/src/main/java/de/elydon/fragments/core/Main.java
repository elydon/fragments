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
 * The class path will be scanned for a class that implements
 * {@link Application}. The main class instantiates an object (it needs a
 * parameter-less constructor) of the application and calls
 * {@link Application#setup() setup}.
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
		}
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

		try {
			application = (Application) applicationClasses.keySet().iterator().next().newInstance();
			final Thread applicationThread = application.setup();
			if (applicationThread == null) {
				throw new IllegalStateException("Thread of the application is null");
			}

			if (!applicationThread.isAlive()) {
				applicationThread.start();
			}
			applicationThread.join();
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (final InterruptedException e) {
		} catch (Exception e) {
			System.err.println("Setting up the application failed");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Application getApplication() {
		return application;
	}
}
