package de.elydon.fragments.core;

public class ImplementingClassFilter implements ClassFilter {
	
	private final Class<?> implementedClass;

	public ImplementingClassFilter(final Class<?> implementedClass) {
		this.implementedClass = implementedClass;
	}

	@Override
	public boolean accepts(final Class<?> clazz) {
		return !clazz.isAnonymousClass() && implementedClass.isAssignableFrom(clazz);
	}

}
