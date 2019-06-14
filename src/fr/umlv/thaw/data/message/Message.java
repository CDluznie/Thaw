package fr.umlv.thaw.data.message;

import java.util.Objects;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.util.date.Date;

public class Message {

	private final Channel channel;
	private final Chatter sender;
	private final String body;
	private final Date date;
	
	Message(Channel channel, Chatter sender, String body, Date date) {
		this.channel = Objects.requireNonNull(channel);
		this.sender = Objects.requireNonNull(sender);
		this.body = Objects.requireNonNull(body);
		this.date = Objects.requireNonNull(date);
	}
	
	@Override
	public String toString() {
		return sender.getChattingName() + "-" + channel + "-" + body;
	}

	/**
	 * Get the destinated channel of the message.
	 * 
	 * @return The  destinated channel of the message.
	 */
	public Channel getChannel() {
		return channel;
	}
	
	/**
	 * Get the body of the message.
	 * 
	 * @return The body of the message.
	 */
	public String getBody() {
		return body;
	}
	
	/**
	 * Get the writer of the message.
	 * 
	 * @return The writer of the message.
	 */
	public Chatter getSender() {
		return sender;
	}
	
	/**
	 * Get the issue date of the message.
	 * 
	 * @return The issue date of the message.
	 */
	public Date getDate() {
		return date;
	}
	
}
