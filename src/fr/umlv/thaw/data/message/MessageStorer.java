package fr.umlv.thaw.data.message;

import java.rmi.ServerException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.util.database.DataBaseConnection;

public class MessageStorer {

	private final MessageDataBaseLink messageDBLink;
	
	/**
	 * Construct an empty thread-safe structure that can contain some messages.
	 * 
	 * @param connection a connection to the database
	 * @throws 	NullPointerException if connection is null
	 */
	public MessageStorer(DataBaseConnection connection) {
		this.messageDBLink = new MessageDataBaseLink(Objects.requireNonNull(connection));
	}

	/**
	 * Add the specified message to the storer.
	 * 
	 * @param 	message the specified message
	 * @throws 	NullPointerException if message is null
	 * @throws 	ServerException if an error occurs during the process
	 */
	public void add(Message message) throws ServerException {
		try {
			messageDBLink.insert(Objects.requireNonNull(message));
		} catch (SQLException e) {
			throw new ServerException("an error occurs during messages insertion", e);
		}
	}
	
	/**
	 * Get all the messages of the specified channel from the storer.
	 * Be careful, this method is not in O(n) complexity.
	 * The returned list is ascending ordered by date.
	 * The returned list is immutable.
	 * 
	 * @param 	channel the specified channel
	 * @return	The list of messages.
	 * @throws 	NullPointerException if channel is null
	 * @throws 	ServerException if an error occurs during the process
	 */
	public List<Message> getAll(Channel channel) throws ServerException {
		try {
			return messageDBLink.select(Objects.requireNonNull(channel));
		} catch (SQLException | ParseException e) {
			throw new ServerException("an error occurs during messages selection", e);
		}
	}
	
}
