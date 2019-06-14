package fr.umlv.thaw.data.chatter.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientStorer {

	private final ConcurrentHashMap<String, Client> clients;
	
	/**
	 * Construct an empty thread-safe structure that can contain some clients.
	 */
	public ClientStorer() {
		this.clients = new ConcurrentHashMap<>();
	}
	
	/**
	 * Get the client with the specified login from the storer.
	 * This method return null if the storer do not contains client with specified login.
	 *
	 * @param	login the specified login
	 * @return	The client, or null if not exist
	 * @throws	NullPointerException if login is null
	 */
	public Client get(String login) {
		Objects.requireNonNull(login);
		return clients.get(login);
	}
	
	/**
	 * Add the specified client to the storer.
	 * The client must be absent from the storer.
	 *
	 * @param	client the specified client
	 * @throws 	NullPointerException if client is null
	 * @throws	IllegalStateException if storer contains the client
	 */
	public void put(Client client) {
		String login = Objects.requireNonNull(client).getLogin();
		if (clients.putIfAbsent(login, client) != null) {
			throw new IllegalStateException("login '" + login + "' already contained");
		}
	}
	
	/**
	 * Get all the logins from the storer.
	 * The returned set is immutable.
	 * 
	 *  @return The set of the login contained in the storer
	 */
	public Set<String> getLogins() {
		return clients.keySet();
	}
	
	/**
	 * Get all the clients from the storer.
	 * Copies of values are made when you call this method.
	 * The returned list is immutable.
	 *
	 * @return 	The list of the clients contained in the storer
	 */
	public List<Client> getAll() {
		return Collections.unmodifiableList(new ArrayList<>(clients.values()));
	}
	
}
