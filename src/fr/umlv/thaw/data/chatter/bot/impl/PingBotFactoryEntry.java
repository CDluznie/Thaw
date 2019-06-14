package fr.umlv.thaw.data.chatter.bot.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.umlv.thaw.data.chatter.bot.BotBehavior;
import fr.umlv.thaw.data.chatter.bot.BotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.ChattingContext;

public class PingBotFactoryEntry implements BotFactoryEntry {

	@Override
	public String getName() {
		return "ping-bot";
	}

	@Override
	public BotBehavior getBehavior(String... args) {
		Objects.requireNonNull(args);
		return new BotBehavior() {
			
			@Override
			public List<String> react(ChattingContext chattingContext) {
				if (chattingContext.lastMessage().equals("ping")) {
					return List.of("pong");
				}
				return Collections.emptyList();
			}
			
		};
	}

}
