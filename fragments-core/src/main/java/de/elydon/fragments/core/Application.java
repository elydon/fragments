package de.elydon.fragments.core;

import java.util.function.Supplier;

/**
 * <p>
 * A container for fragment projects that glues all items together to get a
 * running system.
 * </p>
 * <p>
 * This is the main class of the container that offers all necessary methods to
 * manage the whole application.
 * </p>
 * 
 * @author elydon
 *
 */
public interface Application {

	/**
	 * <p>
	 * Sets up the whole environment and the container for fragments.
	 * </p>
	 * 
	 * @return Returns the {@link Thread} in which the application will run
	 */
	Thread setup();

	/**
	 * <p>
	 * Adds a source for objects.
	 * </p>
	 * 
	 * @param clazz
	 *            The class of the objects the source is for
	 * @param supplier
	 *            The {@link Supplier} of those objects
	 */
	<T> void addObjectSource(final Class<T> clazz, final Supplier<T> supplier);

	/**
	 * <p>
	 * Retrieves an object of the specified class.
	 * </p>
	 * 
	 * @param clazz
	 * @return
	 * @throws IllegalArgumentException
	 *             If no object source was
	 *             {@link #addObjectSource(Class, Supplier) added} for the
	 *             specified class
	 */
	<T> T get(final Class<T> clazz);

	/**
	 * <p>
	 * Examines the {@link Class#getDeclaredFields() fields} of the object and
	 * injects an object, if the field is annotated with Inject and a proper
	 * {@link #addObjectSource(Class, Supplier) object source} exists.
	 * </p>
	 * 
	 * @param patient
	 * @throws IllegalArgumentException
	 *             If the specfied object is {@code null}
	 */
	void inject(final Object patient);
}
