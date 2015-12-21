package de.elydon.fragments.webservice.tomcat;

import java.io.IOException;

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

		final String idParam = req.getParameter("id");
		if (idParam != null) {
			// IE may not be able to handle this .. screw you, IE, learn to be a
			// browser
			resp.addHeader("Content-Type", "application/json");

			try {
				final long id = Long.valueOf(idParam);
				final Fragment fragment = fragmentManager.get(id);

				if (fragment == null) {
					resp.getWriter().write(JsonUtils.generateError("No fragment found: #" + idParam).toJSONString());
				} else {
					resp.getWriter().write(JsonUtils.generateResult(JsonUtils.toJson(fragment)).toJSONString());
				}
			} catch (final NumberFormatException e) {
				resp.getWriter().write(JsonUtils.generateError("ID is not valid: " + idParam).toJSONString());
			}

			return;
		}
	}

}
