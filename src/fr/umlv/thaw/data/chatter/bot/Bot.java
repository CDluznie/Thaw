package fr.umlv.thaw.data.chatter.bot;

import java.util.Objects;
import java.util.function.Consumer;

import fr.umlv.thaw.data.chatter.Chatter;

public class Bot implements Chatter, Comparable<Bot> {

	private final String name;
	private final BotBehavior behavior;
	private ChattingContext context;
	private Consumer<String> consumer;
	
	Bot(String name, BotBehavior behavior) {
		this.name = name;
		this.behavior = behavior;
	}

	@Override
	public String getChattingName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return (name.hashCode() << 3) ^ 150;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Bot)) {
			return false;
		}
		Bot other = (Bot) obj;
		return name.equals(other.name);
	}
	
	@Override
	public int compareTo(Bot other) {
		return name.compareTo(other.name);
	}
	
	/**
	 * Execute the current consumer on the current chatting context of the bot.
	 * This method make blocking waiting.
	 * 
	 * @throws	InterruptedException if the thread is interrupted
	 */
	public void run() throws InterruptedException {
		synchronized (name) {
			while (context == null) {
				name.wait();
			}
			name.notifyAll();
			behavior.react(context).forEach(consumer::accept);
			context = null;
		}
	}
	
	/**
	 * Give to the bot a new consumer of message and a new chatting context
	 * This method make blocking waiting.
	 * 
	 * @param	context the new chatting context
	 * @param	botCode the new consumer
	 * @throws	NullPointerException if context is null
	 * @throws	NullPointerException if botCode is null
	 * @throws 	InterruptedException if the thread is interrupted
	 */
	public void notifyBot(ChattingContext context, Consumer<String> botCode) throws InterruptedException {
		Objects.requireNonNull(context);
		Objects.requireNonNull(botCode);
		synchronized (name) {
			while (this.context != null) {
				name.wait();
			}
			name.notifyAll();
			this.context = context;
			consumer = botCode;
		}
	}

}
