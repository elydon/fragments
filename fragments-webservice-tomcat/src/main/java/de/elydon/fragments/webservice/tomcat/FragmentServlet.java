package de.elydon.fragments.webservice.tomcat;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

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
 * A {@link Servlet} that offers methods of the {@link FragmentManager}.
 * </p>
 * 
 * @author elydon
 *
 */
@WebServlet("/fragments.service")
public class FragmentServlet extends HttpServlet {

	private static final long serialVersionUID = -8750629139338396088L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		// fetch the FragmentManager of the system
		final Application application = Main.getApplication();
		final FragmentManager fragmentManager = application.get(FragmentManager.class);
		// IE may not be able to handle this .. screw you, IE, learn to be a
		// browser (calling before getting the writer to ensure the encoding gets passed)
		resp.addHeader("Content-Type", "application/json;charset=UTF8");
		final PrintWriter writer = resp.getWriter();

		// fetching ID
		final String idParam = req.getParameter("id");
		if (idParam != null) {
			handleFragment(fragmentManager, writer, idParam, (fragment) -> {
				writer.write(JsonUtils.generateResult(JsonUtils.toJson(fragment)).toJSONString());
			});

			return;
		}

		// search request
		final String search = req.getParameter("search");
		if (search != null) {
			final List<Fragment> foundFragments = fragmentManager.search(search);
			writer.write(JsonUtils.generateResult(JsonUtils.toJson(foundFragments)).toJSONString());
			return;
		}

		// related items
		final String relatedToParam = req.getParameter("relatedTo");
		if (relatedToParam != null) {
			handleFragment(fragmentManager, writer, relatedToParam, (fragment) -> {
				final List<Fragment> related = fragmentManager.fetchRelated(fragment);
				writer.write(JsonUtils.generateResult(JsonUtils.toJson(related)).toJSONString());
			});

			return;
		}

		// all fragments
		if (req.getParameter("all") != null) {
			writer.write(JsonUtils.generateResult(JsonUtils.toJson(fragmentManager.getAll())).toJSONString());
			return;
		}

		// delete fragment
		final String deleteIdParam = req.getParameter("delete");
		if (deleteIdParam != null) {
			try {
				final long id = Long.valueOf(deleteIdParam);
				final Fragment deletedFragment = fragmentManager.delete(id);

				writer.write(JsonUtils.generateResult(JsonUtils.toJson(deletedFragment)).toJSONString());
			} catch (final NumberFormatException e) {
				writer.write(JsonUtils.generateError("ID is not valid: " + deleteIdParam).toJSONString());
			} catch (final IllegalArgumentException e) {
				writer.write(JsonUtils.generateError("There is no fragment #" + deleteIdParam).toJSONString());
			}

			return;
		}

		// assuming we want to store a fragment, so check the mandatory parts
		final String storeId = req.getParameter("storeId");
		Long id = null;
		if (storeId != null) {
			try {
				id = Long.valueOf(storeId);
			} catch (final NumberFormatException e) {
				writer.write(JsonUtils.generateError("ID is not valid: " + storeId).toJSONString());
				return;
			}
		}

		final String text = req.getParameter("text");
		if (text == null || text.trim().isEmpty()) {
			writer.write(JsonUtils.generateError("No text given, but is mandatory").toJSONString());
			return;
		}
		final String header = req.getParameter("header");
		if (header == null || header.trim().isEmpty()) {
			writer.write(JsonUtils.generateError("No header given, but is mandatory").toJSONString());
			return;
		}
		
		// optional attribute: source URL
		URL sourceURL = null;
		final String source = req.getParameter("source");
		if (source != null && !source.isEmpty()) {
			try {
				sourceURL = new URL(source);
			} catch (final MalformedURLException e) {
				// try with http://
				try {
					sourceURL = new URL("http://" + source);
				} catch (final MalformedURLException e1) {
					writer.write(JsonUtils.generateError("Source URL is malformed").toJSONString());
					return;
				}
			}
		}
		if (sourceURL != null) {
			try {
				sourceURL.openConnection().getInputStream();
			} catch (final IOException e) {
				writer.write(JsonUtils.generateError("Cannot reach source URL: " + sourceURL).toJSONString());
				return;
			}
		}
		
		// optional attribute: image
		BufferedImage image = null;
		final String imageKey = req.getParameter("filekey");
		if (imageKey != null && !imageKey.isEmpty()) {
			image = ImageServlet.getImage(imageKey);
		}

		// we've come so far, finally just store it
		final Fragment fragment = new Fragment(header, text);
		if (id != null) {
			fragment.setId(id);
		}
		fragment.setSource(sourceURL);
		fragment.setImage(image);

		final Fragment storedFragment = fragmentManager.store(fragment);
		writer.write(JsonUtils.generateResult(JsonUtils.toJson(storedFragment)).toJSONString());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private void handleFragment(final FragmentManager fragmentManager, final PrintWriter writer, final String idParam,
			final Consumer<Fragment> consumer) {
		try {
			final long id = Long.valueOf(idParam);
			final Fragment fragment = fragmentManager.get(id);

			if (fragment == null) {
				writer.write(JsonUtils.generateError("No fragment found: #" + idParam).toJSONString());
			} else {
				consumer.accept(fragment);
			}
		} catch (final NumberFormatException e) {
			writer.write(JsonUtils.generateError("ID is not valid: " + idParam).toJSONString());
		}
	}

}
