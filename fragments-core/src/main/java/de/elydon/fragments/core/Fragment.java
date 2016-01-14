package de.elydon.fragments.core;

import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * <p>
 * A fragment is a single piece of knowledge.
 * </p>
 * <p>
 * It holds a header with up to 100 characters and a text with at most
 * {@link #MAX_CHARS_IN_TEXT} characters, and optionally a source URL, and an
 * image.
 * </p>
 * 
 * @author elydon
 *
 */
public class Fragment {

	/**
	 * Maximum number of characters in the header
	 */
	public static final int MAX_CHARS_IN_HEADER = 100;

	/**
	 * Maximum number of characters in the text
	 */
	public static final int MAX_CHARS_IN_TEXT = 3000;

	private long id;

	private String header;

	private String text;

	private BufferedImage image;

	private URL source;

	/**
	 * <p>
	 * Constructs a {@link Fragment} with the specified header and text.
	 * </p>
	 * 
	 * @param header
	 * @param text
	 * @throws IllegalArgumentException
	 *             If any of the arguments is {@code null} or the header exceeds
	 *             100 characters or the text exceeds {@link #MAX_CHARS_IN_TEXT}
	 *             characters
	 */
	public Fragment(final String header, final String text) {
		setHeader(header);
		setText(text);
	}

	/**
	 * <p>
	 * Sets the fragment's unique identifier.
	 * </p>
	 * <p>
	 * The caller is responsible to guarantee that the ID is indeed unique.
	 * </p>
	 * <p>
	 * The ID has to be greater than zero, as negative IDs are not allowed and a
	 * value of zero indicates, that the fragment has not been
	 * {@link FragmentManager#store(Fragment) stored} yet.
	 * </p>
	 * 
	 * @param id
	 */
	public void setId(long id) {
		if (id < 1) {
			throw new IllegalArgumentException("ID must be at least 1");
		}
		this.id = id;
	}

	/**
	 * <p>
	 * Gets the unique identifier of the fragment.
	 * </p>
	 * <p>
	 * If zero, the fragment has not been {@link FragmentManager#store(Fragment)
	 * stored} yet.
	 * </p>
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * <p>
	 * Set the text of the fragment.
	 * </p>
	 * 
	 * @param text
	 * @throws IllegalArgumentException
	 *             If the text is {@code null} or exceeds
	 *             {@link #MAX_CHARS_IN_TEXT} characters
	 */
	public void setText(String text) {
		if (text == null || text.length() > MAX_CHARS_IN_TEXT) {
			throw new IllegalArgumentException(
					"Text must not be null and contain max. " + MAX_CHARS_IN_TEXT + " characters");
		}
		this.text = text;
	}

	/**
	 * <p>
	 * Returns the fragment's text.
	 * </p>
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * <p>
	 * Returns the fragment's header.
	 * </p>
	 * 
	 * @return
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * <p>
	 * Set the header of the fragment.
	 * </p>
	 * 
	 * @param header
	 * @throws IllegalArgumentException
	 *             If the header is {@code null} or exceeds
	 *             {@link #MAX_CHARS_IN_HEADER} characters
	 */
	public void setHeader(String header) {
		if (header == null || header.length() > MAX_CHARS_IN_HEADER) {
			throw new IllegalArgumentException(
					"Header must not be null and contain max. " + MAX_CHARS_IN_HEADER + " characters");
		}
		this.header = header;
	}

	/**
	 * <p>
	 * Gets the fragment's source URL.
	 * </p>
	 * 
	 * @return
	 */
	public URL getSource() {
		return source;
	}

	/**
	 * <p>
	 * Set the fragment's source URL.
	 * </p>
	 * 
	 * @param source
	 */
	public void setSource(URL source) {
		this.source = source;
	}

	/**
	 * <p>
	 * Returns the fragment's image.
	 * </p>
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * <p>
	 * Sets the fragment's image.
	 * </p>
	 * 
	 * @param image
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
