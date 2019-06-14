package fr.umlv.thaw.data.chatter.bot.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.umlv.thaw.data.chatter.bot.BotBehavior;
import fr.umlv.thaw.data.chatter.bot.BotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.ChattingContext;

public class DefaultThawBotFactoryEntry implements BotFactoryEntry {

	@Override
	public String getName() {
		return "thaw";
	}

	@Override
	public BotBehavior getBehavior(String... args) throws IOException {
		Objects.requireNonNull(args);
		return new BotBehavior() {
			
			@Override
			public List<String> react(ChattingContext chattingContext) {
				return Collections.emptyList();
			}
			
		};
	}

	
	
}
