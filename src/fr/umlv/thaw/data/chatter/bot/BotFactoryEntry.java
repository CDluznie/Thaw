package fr.umlv.thaw.data.chatter.bot;

import java.io.IOException;

public interface BotFactoryEntry {

	/**
	 * Get the name of the producted bot.
	 * 
	 * @return 	The name of the producted bot.
	 */
	public String getName();
	
	/**
	 * Get the parameterized behavior of the bot.
	 * An exception is thrown if an error occurs during the arguments parsing.
	 * 
	 * @param	args the arguments of behavior production
	 * @return	The producted behavior
	 * @throws 	IOException if an error occurs during the parsing of the arguments.
	 */
	public BotBehavior getBehavior(String ... args) throws IOException;
	
}
