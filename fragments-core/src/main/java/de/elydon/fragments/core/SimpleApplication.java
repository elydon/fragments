package de.elydon.fragments.core;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>
 * A container for fragment projects that glues all items together to get a
 * running system.
 * </p>
 * <p>
 * This is an abstract class that offers the necessary methods to manage the
 * object sources and injections.
 * </p>
 * 
 * @author elydon
 *
 */
public abstract class SimpleApplication implements Application {

	private final Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

	/**
	 * <p>
	 * Searches for a implementation of {@link FragmentManager}, instantiates
	 * it, and {@link #addObjectSource(Class, Supplier) adds} it to this
	 * application.
	 * </p>
	 * 
	 * @param classLoader
	 *            The {@link ClassLoader} to use for scanning for the
	 *            implementation of {@link FragmentManager}
	 * @throws IllegalStateException
	 *             If not exactly one implementation is found, or it cannot be
	 *             instantiated
	 */
	protected void setupFragmentManager(final ClassLoader classLoader) {
		try {
			final Map<Class<?>, URL> fragmentManagerClasses = ClassScanner.scan(classLoader,
					new ImplementingClassFilter(FragmentManager.class),
					Paths.get(classLoader.getResource(".").toURI()));
			if (fragmentManagerClasses.isEmpty()) {
				throw new IllegalStateException(
						"Found no class implementing " + FragmentManager.class.getCanonicalName());
			}
			if (fragmentManagerClasses.size() != 1) {
				throw new IllegalStateException("Found " + fragmentManagerClasses.size() + " classes implementing "
						+ FragmentManager.class.getCanonicalName());
			}

			final FragmentManager fragmentManager = (FragmentManager) fragmentManagerClasses.keySet().iterator().next()
					.newInstance();
			addObjectSource(FragmentManager.class, () -> fragmentManager);
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Unable to scan for " + FragmentManager.class.getCanonicalName(), e);
		} catch (final IllegalAccessException | InstantiationException e) {
			throw new IllegalStateException(
					"Unable to instantiate an object for " + FragmentManager.class.getCanonicalName(), e);
		}
	}

	@Override
	public <T> void addObjectSource(final Class<T> clazz, final Supplier<T> supplier) {
		synchronized (suppliers) {
			suppliers.put(Objects.requireNonNull(clazz), Objects.requireNonNull(supplier));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final Class<T> clazz) {
		synchronized (suppliers) {
			T o = (T) suppliers.get(Objects.requireNonNull(clazz)).get();
			inject(o);
			return o;
		}
	}

	@Override
	public void inject(final Object patient) {
		if (patient == null) {
			throw new IllegalArgumentException("Patient must not be null");
		}

		synchronized (suppliers) {
			for (final Field field : patient.getClass().getDeclaredFields()) {

				for (final Map.Entry<Class<?>, Supplier<?>> entry : suppliers.entrySet()) {
					final Class<?> clazz = entry.getKey();
					if (field.getType().isAssignableFrom(clazz)) {
						field.setAccessible(true);
						try {
							field.set(patient, get(clazz));
						} catch (final IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
