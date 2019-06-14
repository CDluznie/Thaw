package fr.umlv.thaw.util.rss;

import java.util.Objects;

public class FeedMessage {

	private String title;
	private String description;
	private String link;
	private String author;
	private String guid;

	/**
	 * Get the title of the feed.
	 * 
	 * @return	The title of the feed.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title to the feed.
	 * 
	 * @param 	title the title	
	 * @throws 	NullPointerException if title is null
	 */
	public void setTitle(String title) {
		this.title = Objects.requireNonNull(title);
	}

	/**
	 * Get the description of the feed.
	 * 
	 * @return	The description of the feed.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description to the feed.
	 * 
	 * @param	description the description
	 * @throws 	NullPointerException if description is null
	 */
	public void setDescription(String description) {
		this.description = Objects.requireNonNull(description);
	}

	/**
	 * Get the link of the feed.
	 * 
	 * @return	The link of the feed.
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * Set the link to the feed.
	 * 
	 * @param	link the link
	 * @throws 	NullPointerException if link is null
	 */
	public void setLink(String link) {
		this.link = Objects.requireNonNull(link);
	}

	/**
	 * Get the author of the feed.
	 * 
	 * @return	The author of the feed.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Set the author to the feed.
	 * 
	 * @param	author the author
	 * @throws 	NullPointerException if author is null
	 */
	public void setAuthor(String author) {
		this.author = Objects.requireNonNull(author);
	}

	/**
	 * Get the identifier of the feed.
	 * 
	 * @return	The identifier of the feed.
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the identifier to the feed.
	 * 
	 * @param	guid the identifier
	 * @throws 	NullPointerException if guid is null
	 */
	public void setGuid(String guid) {
		this.guid = Objects.requireNonNull(guid);
	}

}