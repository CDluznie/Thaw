package fr.umlv.thaw.data.chatter.bot;

import java.util.List;

@FunctionalInterface
public interface BotBehavior {

	/**
	 * Get the list of message create from the bot with the specified chatting context.
	 * 
	 * @param	chattingContext the specified chatting context
	 * @return	The list of the producted message of the bot.
	 */
	public List<String> react(ChattingContext chattingContext);
	
}
