package de.elydon.fragments.webservice.tomcat;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.elydon.fragments.core.Fragment;

/**
 * <p>
 * Offers utility methods regarding transformations to and from JSON format.
 * </p>
 * 
 * @author elydon
 *
 */
public final class JsonUtils {

	private JsonUtils() {
	}

	/**
	 * <p>
	 * Transforms the list of {@link Fragment fragments} into a {@link JSONArray
	 * JSON array}.
	 * </p>
	 * 
	 * @param fragments
	 * @return The {@link JSONArray} containing all fragments in the list, or
	 *         {@code null} if the list was {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray toJson(final List<Fragment> fragments) {
		if (fragments == null) {
			return null;
		}

		final JSONArray result = new JSONArray();
		for (final Fragment fragment : fragments) {
			result.add(toJson(fragment));
		}

		return result;
	}

	/**
	 * <p>
	 * Transforms the {@link Fragment} into a JSON representation.
	 * </p>
	 * 
	 * @param fragment
	 * @return The {@link JSONObject} representing the fragment, or {@code null}
	 *         , if the fragment was {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject toJson(final Fragment fragment) {
		if (fragment == null) {
			return null;
		}

		final JSONObject result = new JSONObject();
		result.put("id", fragment.getId());
		result.put("header", fragment.getHeader());
		result.put("text", fragment.getText());
		if (fragment.getSource() != null) {
			result.put("source", fragment.getSource().toString());
		}
		if (fragment.getImage() != null) {
			result.put("image", ImageServlet.computeKey(fragment.getImage()));
		}

		return result;
	}

	/**
	 * <p>
	 * Generates a {@link JSONObject} that indicates a successful process. If
	 * the specified object is not {@code null}, it is contained in the JSON
	 * object.
	 * </p>
	 * 
	 * @param r
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject generateResult(final Object r) {
		final JSONObject result = new JSONObject();

		result.put("status", "okay");
		if (r != null) {
			result.put("result", r);
		}

		return result;
	}

	/**
	 * <p>
	 * Generates a {@link JSONObject} that indicates an error. If the message is
	 * not {@code null}, the message is contained in the JSON object.
	 * </p>
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject generateError(final String message) {
		final JSONObject result = new JSONObject();

		result.put("status", "error");
		if (message != null) {
			result.put("message", message);
		}

		return result;
	}
}
