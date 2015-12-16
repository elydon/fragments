package de.elydon.fragments.application.simple.tomcat;

import javax.sql.DataSource;

import org.apache.catalina.Realm;
import org.apache.catalina.startup.Tomcat;

import de.elydon.fragments.core.SimpleApplication;

/**
 * <p>
 * A {@link SimpleApplication} that starts a {@link Tomcat} instance. All WAR
 * archives in the current directory are deployed to the tomcat, using the WAR
 * archive's file name as the webapp's context name.
 * </p>
 * <p>
 * Only the default configuration of the tomcat is used, no {@link DataSource
 * data sources}, {@link Realm realms} or anything get defined.
 * </p>
 * 
 * @author elydon
 *
 */
public class SimpleTomcatApplication extends SimpleApplication {

	@Override
	public Thread setup() throws Exception {
		setupFragmentManager(getClass().getClassLoader());
		
		final TomcatRunnable runnable = new TomcatRunnable();
		final Thread tomcatThread = new Thread(runnable);

		return tomcatThread;
	}

}
