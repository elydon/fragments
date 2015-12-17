package de.elydon.fragments.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassScanner {

	private ClassScanner() {
	}

	public static Map<Class<?>, URL> scan(final ClassLoader classLoader, final ClassFilter filter, final Path root) {
		final Map<Class<?>, URL> classes = new HashMap<>();

		System.out.println("scanning " + root);
		try {
			Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					if (file.toString().endsWith(".class")) {
						System.out.println("  scanning file " + file);
						String classname = file.toString().replace(root.toString() + File.separator, "")
								.replace(File.separator, ".");
						classname = classname.substring(0, classname.length() - ".class".length());
						try {
							final Class<?> clazz = classLoader.loadClass(classname);
							if (filter.accepts(clazz)) {
								System.out.println("    added " + classname);
								classes.put(clazz, null);
							}
						} catch (final NoClassDefFoundError | ClassNotFoundException e) {
							System.err.println(
									classname + " could not be loaded from [" + file + "], reason: " + e.getMessage());
						}
					} else if (file.toString().endsWith(".jar")) {
						System.out.println("  scanning JAR file " + file);
						// construct class loader for the JAR file
						final URL url = file.toUri().toURL();
						try (final URLClassLoader jarClassLoader = new URLClassLoader(
								new URL[] { url }, classLoader)) {

							// loop through the JAR file's entries
							try (final ZipInputStream zip = new ZipInputStream(new FileInputStream(file.toFile()))) {
								for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
									if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
										// extract class name
										String classname = entry.getName().replace('/', '.');
										classname = classname.substring(0, classname.length() - ".class".length());
										try {
											final Class<?> clazz = jarClassLoader.loadClass(classname);
											if (filter.accepts(clazz)) {
												System.out.println("    added " + classname);
												classes.put(clazz, url);
											}
										} catch (final NoClassDefFoundError | ClassNotFoundException e) {
											System.err.println(classname + " could not be loaded from [" + file
													+ "], reason: " + e.getMessage());
										}
									}
								}
							}
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
