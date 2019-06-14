package fr.umlv.thaw.util.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class RSSFeedParser {

	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String LANGUAGE = "language";
	private static final String COPYRIGHT = "copyright";
	private static final String LINK = "link";
	private static final String AUTHOR = "author";
	private static final String ITEM = "item";
	private static final String PUB_DATE = "pubDate";
	private static final String GUID = "guid";

	final URL url;

	/**
	 * Construct a RSS parser for the specified URL.
	 * 
	 * @param 	feedUrl the specified URL.
	 * @throws 	NullPointerException if feedUrl is null
	 */
	public RSSFeedParser(URL feedUrl) {
		this.url = Objects.requireNonNull(feedUrl);
	}

	/**
	 * Parse the RSS stream.
	 * 
	 * @return	The feed of the parsed RSS.
	 * @throws	XMLStreamException if an error occurs during the parsing
	 */
	public Feed readFeed() throws XMLStreamException {
		Feed feed = null;
		boolean isFeedHeader = true;
		String description = "";
		String title = "";
		String link = "";
		String language = "";
		String copyright = "";
		String author = "";
		String pubdate = "";
		String guid = "";
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = read();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				String localPart = event.asStartElement().getName()
						.getLocalPart();
				switch (localPart) {
				case ITEM:
					if (isFeedHeader) {
						isFeedHeader = false;
						feed = new Feed(title, link, description, language,copyright, pubdate);
					}
					event = eventReader.nextEvent();
					break;
				case TITLE:
					title = getCharacterData(event, eventReader);
					break;
				case DESCRIPTION:
					description = getCharacterData(event, eventReader);
					break;
				case LINK:
					link = getCharacterData(event, eventReader);
					break;
				case GUID:
					guid = getCharacterData(event, eventReader);
					break;
				case LANGUAGE:
					language = getCharacterData(event, eventReader);
					break;
				case AUTHOR:
					author = getCharacterData(event, eventReader);
					break;
				case PUB_DATE:
					pubdate = getCharacterData(event, eventReader);
					break;
				case COPYRIGHT:
					copyright = getCharacterData(event, eventReader);
					break;
				}
			} else if (event.isEndElement()) {
				if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
					FeedMessage message = new FeedMessage();
					message.setAuthor(author);
					message.setDescription(description);
					message.setGuid(guid);
					message.setLink(link);
					message.setTitle(title);
					feed.getMessages().add(message);
					event = eventReader.nextEvent();
					continue;
				}
			}
		}
		return feed;
	}

	private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		return result;
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}