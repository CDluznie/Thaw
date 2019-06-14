package fr.umlv.thaw.data.channel;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.data.chatter.bot.Bot;
import fr.umlv.thaw.data.chatter.client.Client;
import fr.umlv.thaw.util.counter.SetCounter;
import fr.umlv.thaw.util.exception.InvalidValueException;
 
public class Channel {
	
	private final int id;
	private final String name;
	private final Chatter owner;
	private final SetCounter<Client> clients;
	private final ConcurrentSkipListSet<Bot> bots;

	Channel(int id, String name, Chatter owner) throws InvalidValueException {
		this.id = id;
		this.name = requireValidName(name);
		this.owner = Objects.requireNonNull(owner);
		this.clients = new SetCounter<>();
		this.bots = new ConcurrentSkipListSet<>();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Channel)) {
			return false;
		}
		Channel other = (Channel) obj;
		return (id == other.id) && name.equals(other.name);
	}
	
	@Override
	public int hashCode() {
		return ((name.hashCode() << 5) ^ id);
	}
	
	/**
	 * Get the identifier of the channel.
	 *
	 * @return 	The identifier of the channel.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the name of the channel.
	 *
	 * @return 	The name of the channel.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the table name in the database of the channel.
	 *
	 * @return	The table name of the specified channel.
	 */
	public String tableName() {
		return "CHANNEL_" + name + "_" + id;
	}
	
	/**
	 * Add the specified client to the channel.
	 * The client can join the channel many times,
	 * the channel will count the number of occurences of it.
	 *
	 * @param 	client the specified client
	 * @return	True if the client is the first occurence of the channel, False else.
	 * @throws 	NullPointerException if client is null
	 */
	public boolean join(Client client) {
		Objects.requireNonNull(client);
		return clients.add(client);
	}
	
	/**
	 * Get the set of the clients connected on the channel.
	 * Each client is count once in the set.
	 * The returned set is immutable.
	 * 
	 * @return 	The set of the clients connected on the channel.
	 */
	@JsonIgnore
	public Set<Client> getClients() {
		return clients.getAll();
	}
	
	/**
	 * Remove the specified client from the channel.
	 * The client can be remove fom the channel many times,
	 * the channel will count the number of occurences of it.
	 *
	 * @param  	client the specified client
	 * @return	True if the client is the last occurence of the channel, False else.
	 * @throws 	NullPointerException if client is null
	 */
	public boolean leave(Client client) {
		return clients.remove(client);
	}
	
	/**
	 * Add the specified bot to the channel.
	 * A channel can only have one instance of each bot.
	 * 
	 * @param 	bot the specified bot
	 * @return	True if the channel did not contains the bot, False else.
	 * @throws 	NullPointerException if bot is null
	 */
	public boolean addBot(Bot bot) {
		Objects.requireNonNull(bot);
		return bots.add(bot);
	}
	
	/**
	 * Get the set of the bots connected in the channel.
	 * Each bot is count once in the set.
	 * The returned set is immutable.
	 *
	 * @return 	The set of bots in the channel.
	 */
	@JsonIgnore
	public Set<Bot> getBots() {
		return Collections.unmodifiableSet(bots);
	}
	
	/**
	 * Get the set of the chatters connected in the channel.
	 * Each chatter is count once in the set.
	 * The returned set is immutable.
	 *
	 * @return 	The set of chatters in the channel.
	 */
	public Set<Chatter> getChatters() {
		return new AbstractSet<Chatter>() {

			@Override
			public Iterator<Chatter> iterator() {
				return Stream.concat(bots.stream().map(Chatter::asSimpleChatter), clients.getAll().stream().map(Chatter::asSimpleChatter)).iterator();
			}

			@Override
			public int size() {
				return bots.size() + clients.size();
			}
			
			@Override
			public boolean contains(Object object) {
				return clients.getAll().contains(object) || bots.contains(object);
			}
			
			@Override
			public boolean isEmpty() {
				return clients.getAll().isEmpty() && bots.isEmpty();
			}
			
		};
	}
	
	/**
	 * Get the owner of the channel.
	 * 
	 * @return	The owner of the channel.
	 */
	public Chatter getOwner() {
		return owner;
	}
	
	private static String requireValidName(String name) throws InvalidValueException {
		if (name == null) {
			throw new InvalidValueException("channel name can not be null");
		}
		if (name.length() < 1) {
			throw new InvalidValueException("channel name must be greater than 1 characters");
		}
		if (name.length() > 30) {
			throw new InvalidValueException("channel name must be smaller than 15 characters");
		}
		if (!name.matches("[a-zA-Z]*")) {
			throw new InvalidValueException("channel name can only contains letters");
		}
		return name;
	}

	
}
