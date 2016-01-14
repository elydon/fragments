package de.elydon.fragments.scraper.wikipedia;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import de.elydon.fragments.core.Fragment;

/**
 * <p>
 * Fetches the content of a single wikipedia page and divides it into several
 * {@link Fragment fragments}, one per header and text block.
 * </p>
 * <p>
 * Certain blocks are ignored, for example blocks that contain only a list
 * (which is assumed to be a list of links, like referenced literature or links
 * to similar topics).
 * </p>
 * 
 * @author elydon
 *
 */
public class WikipediaPageScraper {

	private final URL url;

	private Elements headers;

	public WikipediaPageScraper(final URL url) {
		this.url = url;
	}

	public List<Fragment> parse() throws IOException {
		final Document document = Jsoup.connect(url.toString()).followRedirects(true).ignoreContentType(false).get();

		// remove navigation, table of contents and other elements we are not
		// interested in
		document.getElementById("mw-navigation").remove();
		document.getElementById("toc").remove();
		document.getElementsByClass("mw-editsection").remove();
		document.getElementsByClass("hatnote").remove();
		document.getElementsByClass("ambox").remove();
		document.getElementsByClass("noprint").remove();
		document.getElementsByClass("plainlinks").remove();
		document.getElementsByClass("navbox").remove();
		document.getElementsByClass("infobox").remove();
		document.getElementsByClass("refbegin").remove();

		final List<Fragment> result = new ArrayList<>();
		// h4 is getting to small and has to be count as part of the header above
		headers = document.body().select("h1,h2,h3,h4,h5,h6");
		for (final Element header : headers) {
			System.out.println("found " + header.text());

			Element contentExtractionElement = header.nextElementSibling();
			if ("firstHeading".equals(header.id())) {
				contentExtractionElement = document.getElementById("mw-content-text").child(0);
			}

			final StringBuilder extractedContent = new StringBuilder();
			boolean foundNonListElement = false;
			BufferedImage image = null;
			while (isValidContentElement(contentExtractionElement)) {
				if (!foundNonListElement) {
					foundNonListElement = !isListElement(contentExtractionElement);
				}

				// contains image? search for thumb construct
				final Element thumbElement = contentExtractionElement.select("div.thumb").first();
				if (thumbElement != null) {
					image = fetchImage(thumbElement);
					System.out.println("found image (" + image.getWidth() + "x" + image.getHeight() + ")");
				}

				// extract text
				final String elementContent = cleanseElementHtml(contentExtractionElement) + "\n";
				if (extractedContent.length() + elementContent.length() < Fragment.MAX_CHARS_IN_TEXT) {
					extractedContent.append(elementContent);
				} else {
					System.out.println("ignoring (exceeding character limit): " + elementContent);
				}
				contentExtractionElement = contentExtractionElement.nextElementSibling();
			}

			if (foundNonListElement) {
				System.out.println(
						"extracted (" + extractedContent.length() + " chars): \n" + extractedContent.toString());
				final Fragment fragment = new Fragment(header.text(), extractedContent.toString());
				fragment.setSource(url);
				fragment.setImage(image);
				result.add(fragment);
			} else {
				System.out.println("ignoring, contains only list\n");
			}
		}

		return result;
	}

	private BufferedImage fetchImage(final Element thumbElement) throws IOException {
		String url = thumbElement.select("img").first().attr("abs:src");
		if (url.contains("thumb")) {
			url = url.replace("/thumb", "");
			url = url.substring(0, url.lastIndexOf('/'));

			// java cannot interpret SVG into BufferedImage, so try to get the
			// bigger image behind the thumb
			if (url.endsWith(".svg")) {
				// get link to image detail page
				final String detailPageUrl = thumbElement.select("a.image").attr("abs:href");
				final Document detailPage = Jsoup.connect(detailPageUrl).followRedirects(true).get();
				url = detailPage.body().select("div#file img").first().attr("abs:src");
			}
		}

		try (final ByteArrayInputStream bis = new ByteArrayInputStream(
				Jsoup.connect(url).ignoreContentType(true).execute().bodyAsBytes())) {
			return ImageIO.read(bis);
		}
	}

	private String cleanseElementHtml(final Element element) {
		return Jsoup.clean(element.outerHtml(), new Whitelist().addTags("p", "i", "b", "em", "strong", "ul", "ol", "dl", "li", "dd", "table", "thead", "tbody", "tr", "td", "th"));
	}

	private boolean isValidContentElement(final Element element) {
		return element != null && !headers.contains(element) && element.hasText();
	}

	private boolean isListElement(final Element element) {
		return "ul".equals(element.tagName()) || "ol".equals(element.tagName()) || "dl".equals(element.tagName());
	}
}
