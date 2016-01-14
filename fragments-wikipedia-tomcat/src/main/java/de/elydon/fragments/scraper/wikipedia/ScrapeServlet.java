package de.elydon.fragments.scraper.wikipedia;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.elydon.fragments.core.Application;
import de.elydon.fragments.core.Fragment;
import de.elydon.fragments.core.FragmentManager;
import de.elydon.fragments.core.Main;

/**
 * <p>
 * A {@link Servlet} that uses {@link WikipediaPageScraper} to turn a wikipedia
 * page into {@link Fragment fragments}.
 * </p>
 * 
 * @author elydon
 *
 */
@WebServlet("/scrape.service")
public class ScrapeServlet extends HttpServlet {

	private static final long serialVersionUID = -1867595998385637489L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final String urlParam = req.getParameter("url");
		if (urlParam != null) {
			URL url = null;
			try {
				url = new URL(urlParam);
			} catch (final MalformedURLException e) {
				try {
					url = new URL("http://" + urlParam);
				} catch (final MalformedURLException e1) {
					return;
				}
			}
			// TODO: sanity checks
			
			// transform wikipedia page into fragments
			final WikipediaPageScraper scraper = new WikipediaPageScraper(url);
			final List<Fragment> fragments = scraper.parse();
			
			// store the fragments
			final Application application = Main.getApplication();
			final FragmentManager fragmentManager = application.get(FragmentManager.class);
			fragments.forEach(fragment -> {
				fragmentManager.store(fragment);
			});
		}
	}
}
