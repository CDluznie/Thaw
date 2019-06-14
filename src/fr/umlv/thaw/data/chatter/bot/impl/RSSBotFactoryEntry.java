package fr.umlv.thaw.data.chatter.bot.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import fr.umlv.thaw.data.chatter.bot.BotBehavior;
import fr.umlv.thaw.data.chatter.bot.BotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.ChattingContext;
import fr.umlv.thaw.util.rss.FeedMessage;
import fr.umlv.thaw.util.rss.RSSFeedParser;

public class RSSBotFactoryEntry implements BotFactoryEntry {

	@Override
	public String getName() {
		return "rss-bot";
	}

	@Override
	public BotBehavior getBehavior(String ... args) throws IOException {
		Objects.requireNonNull(args);
		if (args.length < 1) {
			throw new IOException("missing url argument for rss bot");
		}
		URL url = new URL(Objects.requireNonNull(args[0]));
		return new BotBehavior() {
			
			private final RSSFeedParser parser = new RSSFeedParser(url);
			
			@Override
			public List<String> react(ChattingContext chattingContext) {
				if (!chattingContext.lastMessage().equals("RSS")) {
					return Collections.emptyList();
				}
				try {
					return parser.readFeed().getMessages().stream().flatMap(RSSBotFactoryEntry::formatRSS).collect(Collectors.toList());
				} catch (XMLStreamException e) {
					return List.of("an error occurs during the RSS parsing");
				}			
			}
			
		};
	}
	
	private static Stream<String> formatRSS(FeedMessage message) {
		String title = "<a target=\"_blank\" href=\"" + message.getLink() + "\">" + message.getTitle() + "</a>";
		String body = message.getDescription();
		return Stream.of(title, body);
	}

}
