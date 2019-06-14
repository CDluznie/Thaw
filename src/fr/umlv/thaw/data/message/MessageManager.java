package fr.umlv.thaw.data.message;

import java.rmi.ServerException;
import java.util.List;
import java.util.Objects;

import javax.naming.CommunicationException;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.util.database.DataBaseConnection;
import fr.umlv.thaw.util.date.Date;

public class MessageManager {

	private final MessageStorer messages;
	
	/**
	 * Construct a thread-safe safe manager for the messages gestion.
	 * 
	 * @param connection a connection to the database
	 */
	public MessageManager(DataBaseConnection connection) {
		this.messages = new MessageStorer(Objects.requireNonNull(connection));
	}
	
	/**
	 * Create a new message destinated to a channel, 
	 * send by a chatter with the specified body.
	 * Exception are thrown if the sender is not connected on the specified channel.
	 * 
	 * @param	channel the destinated channel
	 * @param	sender the sender of the message
	 * @param	body the body of the message
	 * @return 	The created Message.
	 * @throws 	NullPointerException if channel is null
	 * @throws 	NullPointerException if sender is null
	 * @throws 	NullPointerException if body is null
	 * @throws 	CommunicationException if the chatter is not logged in the channel
	 * @throws 	ServerException if an error occurs during the process
	 */
	public Message create(Channel channel, Chatter sender, String body) throws CommunicationException, ServerException {
		Objects.requireNonNull(channel);
		Objects.requireNonNull(sender);
		Objects.requireNonNull(body);
		if (!channel.getChatters().contains(sender)) {
			throw new CommunicationException("'" + sender + " is not in channel '" + channel + "'");
		}
		Message message = new Message(channel, sender, body, Date.now());
		messages.add(message);
		return message;
	}
	
	/**
	 * Get all the message of the specified channel from the manager.
	 * Be careful, this method is not in O(n) complexity.
	 * The returned list is ascending ordered by date.
	 * The returned list is immutable.
	 *
	 * @param	channel the specified channel
	 * @return 	The list of all the chatters.
	 * @throws 	NullPointerException if channel is null
	 * @throws 	ServerException if an error occurs during the process
	 */
	public List<Message> getMessages(Channel channel) throws ServerException {
		return messages.getAll(Objects.requireNonNull(channel));
	}
	
}
