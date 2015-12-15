package de.elydon.fragments.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import de.elydon.fragments.core.Application;

/**
 * <p>
 * A container for fragment projects that glues all items together to get a
 * running system.
 * </p>
 * <p>
 * This is an abstract class that offers the necessary methods to
 * manage the object sources and injections.
 * </p>
 * 
 * @author elydon
 *
 */
public abstract class SimpleApplication implements Application {

	private final Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

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
