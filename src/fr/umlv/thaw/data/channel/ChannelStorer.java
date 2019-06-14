package fr.umlv.thaw.data.channel;

import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import fr.umlv.thaw.util.database.DataBaseConnection;

public class ChannelStorer {

	private final ConcurrentHashMap<Integer, Channel> channels;
	private final ChannelDataBaseLink channelDBLink;

	/**
	 * Construct an empty thread-safe structure that can contain some channel.
	 *
	 * @param	connection a connection to the database
	 * @throws	NullPointerException if connection is null
	 */
	public ChannelStorer(DataBaseConnection connection) {
		this.channels = new ConcurrentHashMap<>();
		this.channelDBLink = new ChannelDataBaseLink(Objects.requireNonNull(connection));
	}

	/**
	 * Get the channel with the specified identifier from the storer.
	 * This method return null if the storer do not contains channel
	 * with specified identifier.
	 *
	 * @param	id the specified id
	 * @return	The channel, or null if not exist.
	 */
	public Channel get(int id) {
		return channels.get(id);
	}

	/**
	 * Add the specified channel to the storer.
	 * The channel must have an identifier which is not already contains in storer.
	 *
	 * @param	channel the specified channel
	 * @throws 	NullPointerException if channel is null
	 * @throws	IllegalStateException if storer contains a channel with the same id
	 * @throws	ServerException if an unexpected error occurs during the process
	 */
	public void put(Channel channel) throws ServerException {
		Objects.requireNonNull(channel);
		int id = channel.getId();
		synchronized (channels) {
			if (channels.containsKey(id)) {
				throw new IllegalStateException("channel '" + channel + "' can not be store, already contained channel with id '" + id + "'");
			}
			try {
				channelDBLink.insert(channel);
			} catch (SQLException e) {
				throw new ServerException("an error occurs during channel insertion", e);
			}
			channels.put(id,channel);
		}
	}

	/**
	 * Remove the speicied channel from the storer.
	 * The channel must be present in storer.
	 *
	 * @param	channel the specified channel
	 * @throws 	NullPointerException if channel is null
	 * @throws	IllegalStateException if storer do not contain the specified channe
	 * @throws	ServerException if an unexpected error occurs during the process
	 */
	public void remove(Channel channel) throws ServerException {
		Objects.requireNonNull(channel);
		if (channels.remove(channel.getId()) == null){
			throw new IllegalArgumentException(channel + " is not contains in storer");
		}
		try {
			channelDBLink.perpetuate(channel);
		} catch (SQLException e) {
			throw new ServerException("an error occurs during channel deleting", e);
		}
	}

	/**
	 * Get all the channels from the storer.
	 * Copies of values are made when you call this method.
	 * The returned list is immutable.
	 *
	 * @return 	The list of the channels contained in the storer
	 */
	public List<Channel> getAll() {
		return Collections.unmodifiableList(new ArrayList<>(channels.values()));
	}

}
