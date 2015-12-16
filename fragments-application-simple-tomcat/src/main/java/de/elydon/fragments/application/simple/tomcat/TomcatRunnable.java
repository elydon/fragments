package de.elydon.fragments.application.simple.tomcat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

/**
 * <p>
 * A {@link Runnable} that starts a {@link Tomcat} server and deploys all WAR
 * archives that are in the current path.
 * </p>
 * 
 * @author elydon
 *
 */
public class TomcatRunnable implements Runnable {

	private Tomcat tomcat;

	@Override
	public void run() {
		tomcat = new Tomcat();

		// do not unpack the WARs, just run them
		((StandardHost) tomcat.getHost()).setUnpackWARs(false);

		// find all WARs and "deploy" them
		try {
			deployWARs();
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to deploy WAR archives", e);
		}

		try {
			tomcat.start();
		} catch (final LifecycleException e) {
			throw new IllegalStateException("Unable to start tomcat", e);
		}
		tomcat.getServer().await();
	}

	private void deployWARs() throws IOException {
		Files.walkFileTree(Paths.get("."), new FileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
					throws IOException {
				// not scanning recursively
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				// is it a WAR archive?
				if (file.toString().endsWith(".war")) {
					// extract webapp name
					String webappName = file.getFileName().toString();
					webappName = webappName.substring(0, webappName.length() - ".war".length());

					try {
						tomcat.addWebapp("/" + webappName, file.toString());
					} catch (final ServletException e) {
						System.err.println("Unable to deploy [" + file + "], reason: " + e.getMessage());
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
				// ignore
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
