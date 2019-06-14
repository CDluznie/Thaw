package fr.umlv.thaw.util.rss;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Feed {

	private final String title;
	private final String link;
	private final String description;
	private final String language;
	private final String copyright;
	private final String pubDate;

	final List<FeedMessage> entries = new ArrayList<FeedMessage>();

	/**
	 * Construct the main feed of a RSS stream.
	 * 
	 * @param 	title the title of the feed
	 * @param 	link the link to the feed
	 * @param 	description the description of the feed
	 * @param 	language the language of the feed
	 * @param 	copyright the copyright of the feed
	 * @param 	pubDate the publication date of the feed
	 * @throws 	NullPointerException if title is null
	 * @throws 	NullPointerException if link is null
	 * @throws 	NullPointerException if description is null
	 * @throws 	NullPointerException if language is null
	 * @throws 	NullPointerException if copyright is null
	 * @throws 	NullPointerException if pubDate is null
	 */
	public Feed(String title, String link, String description, String language, String copyright, String pubDate) {
		this.title = Objects.requireNonNull(title);
		this.link = Objects.requireNonNull(link);
		this.description = Objects.requireNonNull(description);
		this.language = Objects.requireNonNull(language);
		this.copyright = Objects.requireNonNull(copyright);
		this.pubDate = Objects.requireNonNull(pubDate);
	}

	/**
	 * Get all the feed messages of the main feed.
	 * 
	 * @return The list of the feed messages.
	 */
	public List<FeedMessage> getMessages() {
		return entries;
	}

	/**
	 * Get the title of the main feed.
	 * 
	 * @return	The title of the main feed.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get the link of the main feed.
	 * 
	 * @return	The link of the main feed.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Get the description of the main feed.
	 * 
	 * @return	The description of the main feed.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the title of the main feed.
	 * 
	 * @return	The title of the main feed.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Get the copyright of the main feed.
	 * 
	 * @return	The copyright of the main feed.
	 */
	public String getCopyright() {
		return copyright;
	}

	/**
	 * Get the title of the main feed.
	 * 
	 * @return	The title of the main feed.
	 */
	public String getPubDate() {
		return pubDate;
	}

}