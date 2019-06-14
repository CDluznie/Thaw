package fr.umlv.thaw.data.channel;

import java.rmi.ServerException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.data.chatter.bot.Bot;
import fr.umlv.thaw.util.database.DataBaseConnection;
import fr.umlv.thaw.util.exception.InvalidValueException;

public class ChannelManager {

	private final ChannelStorer channels;
	private final AtomicInteger currentId;
	private final ConcurrentSkipListSet<Integer> identifiers;
	/**
	 * Construct a thread-safe safe manager for the channels gestion.
	 *
	 * @param	connection a connection to the database
	 * @throws 	NullPointerException if connection is null
	 */
	public ChannelManager(DataBaseConnection connection) {
		this.channels = new ChannelStorer(Objects.requireNonNull(connection));
		this.currentId = new AtomicInteger(0);
		this.identifiers = new ConcurrentSkipListSet<>();
	}
	
	private static String getIdRegex() {
		return "[0-9]+";
	}
	
	/**
	 * Get the regular expression of channel for event bus input
	 * 
	 * @return	The regular expression of channels for event bus input.
	 */
	public static String getInputRegexChatAdress() {
		return "chat_" + getIdRegex() + "\\.to\\.server";
	}
	
	/**
	 * Get the regular expression of channel for event bus output
	 * 
	 * @return	The regular expression of channels for event bus output.
	 */
	public static String getOutputRegexChatAdress() {
		return "chat_" + getIdRegex() + "(\\.join|\\.leave)?\\.to\\.client";
	}

	/**
	 * Get the adress of a specified channel for event bus input
	 * 
	 * @param	channel the specified channel
	 * @return	The adress of the channel for event bus input.
	 * @throws 	NullPointerException if channel is null
	 */
	public static String inputChatAdress(Channel channel) {
		return "chat_" + Objects.requireNonNull(channel).getId() + ".to.server";
	}
	
	/**
	 * Get the adress of a specified channel for event bus output
	 * 
	 * @param	channel the specified channel
	 * @return	The adress of the channel for event bus output.
	 * @throws 	NullPointerException if channel is null
	 */
	public static String outputChatAdress(Channel channel) {
		return "chat_" + Objects.requireNonNull(channel).getId() + ".to.client";
	}
	
	/**
	 * Get the adress of a specified channel for connection notification
	 * to the event bus
	 * 
	 * @param	channel the specified channel
	 * @return	The adress of the channel for event bus join event output.
	 * @throws 	NullPointerException if channel is null
	 */
	public static String outputJoinChatAdress(Channel channel) {
		return "chat_" + Objects.requireNonNull(channel).getId() + ".join.to.client";
	}
	
	/**
	 * Get the adress of a specified channel for disconnection notification
	 * to the event bus
	 * 
	 * @param	channel the specified channel
	 * @return	The adress of the channel for event bus leave event output.
	 * @throws 	NullPointerException if channel is null
	 */
	public static String outputLeaveChatAdress(Channel channel) {
		return "chat_" + Objects.requireNonNull(channel).getId() + ".leave.to.client";
	}
	
	/**
	 * Get the channel with the sepcified identifier from the manager.
	 * This method return null if the manager do not contains channel
	 * with specified identifier.
	 * 
	 * @param 	identifier the specified identifier for the channel
	 * @return 	The channel or null if it not exist.
	 * @throws 	NullPointerException if identifier is null
	 */
	public Channel get(String identifier) {
		Objects.requireNonNull(identifier);
		try {
			return channels.get(getId(identifier));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * Create a new channel with the specified name.
	 * The manager will choose the id of the channel.
	 * An exception is thrown if the name do not respect the naming convention.
	 * 
	 * @param	name the specified name for the channel
	 * @param 	owner the owner of the channel
	 * @param 	bots the bots for the channel
	 * @return 	The created channel.
	 * @throws 	NullPointerException if name is null
	 * @throws 	NullPointerException if owner is null
	 * @throws 	NullPointerException if bots is null
	 * @throws 	InvalidValueException if name do not respect the naming convetion
	 * @throws 	ServerException if an unexpected error occurs during the process
	 */
	public Channel create(String name, Chatter owner, Bot ... bots) throws ServerException, InvalidValueException {
		Objects.requireNonNull(name);
		Objects.requireNonNull(owner);
		Objects.requireNonNull(bots);
		int id = currentId.updateAndGet(this::nextId);
		if (id < 0) {
			throw new ServerException("number of channels is full");
		}
		Channel channel = new Channel(id, name, owner);
		channels.put(channel);
		identifiers.add(id);
		List.of(bots).forEach(channel::addBot);
		return channel;
	}
	
	/**
	 * Remove the specified channel from the manager.
	 * An exception is thrown if the channel is not in the manager.
	 * 
	 * @param	channel the specified channel
	 * @throws 	NullPointerException if channel is null
	 * @throws 	IllegalArgumentException if channel is not in manager
	 * @throws 	ServerException if an unexpected error occurs during the process
	 */
	public void remove(Channel channel) throws ServerException {
		Objects.requireNonNull(channel);
		channels.remove(channel);
		identifiers.remove(channel.getId());
	}

	/**
	 * Get all the channels from the manager.
	 * Copies of values are made when you call this method.
	 * The list is descending-sorted by number of chatters in channel.
	 * The returned list is immutable.
	 *
	 * @return 	The list of all the channels.
	 */
	public List<Channel> getAllChannels() {
		return channels.getAll().stream().sorted(Comparator.<Channel>comparingInt(channel -> channel.getChatters().size()).reversed()).collect(Collectors.toList());
	}

	private int getId(String identifier) {
		return Integer.parseInt(Objects.requireNonNull(identifier));
	}
	
	private int nextId(int id) {
		for (int current = id+1; current != id; current = (current + 1) % Integer.MAX_VALUE) {
			if (!identifiers.contains(current)) {
				return current;
			}
		}
		return -1;
	}
	
}