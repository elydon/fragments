package de.elydon.fragments.webservice.tomcat;

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

		// TODO: add the other fragment attributes

		return result;
	}
	
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
