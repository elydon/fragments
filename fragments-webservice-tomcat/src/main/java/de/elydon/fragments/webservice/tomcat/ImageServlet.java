package de.elydon.fragments.webservice.tomcat;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

import de.elydon.fragments.core.Fragment;

/**
 * <p>
 * Stores images in memory, until fetched. Used to upload an image and then
 * fetch it again when creating a {@link Fragment}.
 * </p>
 * 
 * @author elydon
 *
 */
@WebServlet("/images.service")
public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = -3479189069342497186L;

	private static final Map<String, BufferedImage> images = new HashMap<>();

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final String key = req.getParameter("key");
		if (key != null) {
			final BufferedImage image = getImage(key, false);
			if (image != null) {
				resp.addHeader("Content-Type", "image/png");
				ImageIO.write(image, "png", resp.getOutputStream());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final PrintWriter writer = resp.getWriter();

		if (ServletFileUpload.isMultipartContent(req)) {
			final DiskFileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				final List<FileItem> formItems = upload.parseRequest(req);
				for (final FileItem item : formItems) {
					if (!item.isFormField()) {
						// save image
						final BufferedImage image = ImageIO.read(item.getInputStream());
						final String sha = DigestUtils.sha256Hex(item.getInputStream());
						synchronized (images) {
							images.put(sha, image);
						}

						// return SHA "key"
						final JSONObject result = new JSONObject();
						result.put("key", sha);
						writer.write(JsonUtils.generateResult(result).toJSONString());

						// only process one image
						break;
					}
				}
			} catch (final FileUploadException e) {
				writer.write(JsonUtils.generateError("Error uploading file: " + e.getMessage()).toJSONString());
			}
		}
	}
	
	public static BufferedImage getImage(final String key) {
		return getImage(key, true);
	}

	public static BufferedImage getImage(final String key, final boolean remove) {
		synchronized (images) {
			final BufferedImage image = images.get(key);
			if (remove) {
				images.remove(key);
			}
			return image;
		}
	}

}
