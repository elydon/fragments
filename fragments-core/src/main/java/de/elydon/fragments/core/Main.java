package de.elydon.fragments.core;

/**
 * <p>
 * Main class of fragments. Starts the {@link Application}.
 * </p>
 * <p>
 * You have to specify the fully qualified class name of the class that
 * implements {@link Application} as the first and single argument. The main
 * class instantiates an object (it needs a parameter-less constructor) of the
 * application and calls {@link Application#setup() setup}. The application
 * class must be part of the class path.
 * </p>
 * <p>
 * The setup method sets up the whole environment. It is implementation
 * dependent how the various components get assembled to a whole fragments
 * system, but usually it is needed to configure the class path in a way the JVM
 * finds all necessary classes. Please refer to the implementation of the
 * application class for more detailed information.
 * </p>
 * 
 * @author elydon
 *
 */
public class Main {

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.out.println("Please specify the application class name as first argument.");
			System.exit(0);
		}
		
		final String applicationClassName = args[0];
		try {
			final Class<?> applicationClass = Class.forName(applicationClassName);
			if (!applicationClass.isAssignableFrom(Application.class)) {
				System.out.println(applicationClassName + " does not implement " + Application.class.getCanonicalName());
				System.exit(0);
			}
			
			final Application application = (Application) applicationClass.newInstance();
			final Thread applicationThread = application.setup();
			
			if (!applicationThread.isAlive()) {
				applicationThread.start();
			}
			applicationThread.join();
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
		} catch (Exception e) {
			System.err.println("Setting up the application failed");
			e.printStackTrace();
		}
	}
}
