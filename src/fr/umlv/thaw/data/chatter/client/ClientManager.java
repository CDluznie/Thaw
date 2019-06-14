package fr.umlv.thaw.data.chatter.client;

import java.util.List;
import java.util.Objects;

import javax.naming.NameAlreadyBoundException;

import fr.umlv.thaw.util.exception.InvalidValueException;
import fr.umlv.thaw.util.security.SecurityUtils;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ClientManager {

	private final ClientStorer clients;
	
	/**
	 * Construct a thread-safe safe manager for the clients gestion.
	 */
	public ClientManager() {
		this.clients = new ClientStorer();
	}
	
	/**
	 * Get the json object of the specified client
	 * with the confidential informations.
	 * 
	 * @param 	client the specified client
	 * @return	The json of client.
	 * @throws	NullPointerException if client is null
	 */
	public static JsonObject showConfidential(Client client) {
		Objects.requireNonNull(client);
		JsonObject json = new JsonObject(Json.encodePrettily(client));
		json.put("cryptedPassword", client.getCryptedPassword());
		return json;
	}

	/**
	 * Create a new client with the specified name.
	 * Exception are thrown if the login is already bound is or 
	 * login/password do not respect the naming convetion.
	 * 
	 * @param	login the login of the client
	 * @param	password the password of the client
	 * @return 	The created client.
	 * @throws	NullPointerException if login is null
	 * @throws	NullPointerException if password is null
	 * @throws 	InvalidValueException if the login do not respect the naming convention
	 * @throws 	InvalidValueException if the password do not respect the naming convention
	 * @throws 	NameAlreadyBoundException if the login is already bound
	 */
	public Client create(String login, String password) throws InvalidValueException, NameAlreadyBoundException {
		Objects.requireNonNull(login);
		Objects.requireNonNull(password);
		if (clients.getLogins().contains(login)) {
			throw new NameAlreadyBoundException("login '" + login + "' is already use");
		}
		Client client = Client.create(login, password);
		clients.put(client);
		return client;
	}

	/**
	 * Get a client from the manager with the specified login
	 * This method return null if the manager do not contains client with specified login.
	 * 
	 * @param 	login the specified login
	 * @return	The client, or null if not exist 
	 * @throws	NullPointerException if login is null
	 */
	public Client get(String login) {
		Objects.requireNonNull(login);
		return clients.get(login);
	}

	/**
	 * Log the client with the specified login/password.
	 * An exception is thrown if the login is unknow from the manager or
	 * the password do not match with the registered login.
	 * 
	 * @param 	login the specified login
	 * @param 	password the specified password
	 * @return	The logged client.
	 * @throws	NullPointerException if login is null
	 * @throws	NullPointerException if password is null
	 * @throws 	IllegalAccessException if the login is not registered in the manage
	 * @throws 	IllegalAccessException if the password do not match with the registered login
	 */
	public Client log(String login, String password) throws IllegalAccessException {
		Objects.requireNonNull(login);
		Objects.requireNonNull(password);
		Client client = clients.get(login);
		if (client == null) {
			throw new IllegalAccessException("identifiant inconnu");
		} else if (!SecurityUtils.crypt(password).equals(client.getCryptedPassword())) {
			throw new IllegalAccessException("mot de passe �rron�");
		}
		return client;
	}

	/**
	 * Get all the clients from the manager.
	 * Copies of values are made when you call this method.
	 * The returned list is immutable.
	 *
	 * @return 	The list of all the chatters.
	 */
	public List<Client> getAll() {
		return clients.getAll();
	}

}
