package de.elydon.fragments.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ClassScanner {

	private ClassScanner() {
	}

	public static Set<Class<?>> scan(final ClassLoader classLoader, final ClassFilter filter, final Path root) {
		final Set<Class<?>> classes = new HashSet<>();
		
		System.out.println("scanning " + root);
		try {
			Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					if (file.toString().endsWith(".class")) {
						System.out.println("  scanning file " + file);
						final String classname = file.toString().replace(root.toString() + File.separator, "").replace(File.separator, ".").replace(".class", "");
						try {
							final Class<?> clazz = classLoader.loadClass(classname);
							if (filter.accepts(clazz)) {
								System.out.println("added " + classname);
								classes.add(clazz);
							}
						} catch (final ClassNotFoundException e) {
							System.err.println(classname + " could not be loaded from resource " + file);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		return classes;
	}
}
