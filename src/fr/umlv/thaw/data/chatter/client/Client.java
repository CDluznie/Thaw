package fr.umlv.thaw.data.chatter.client;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.util.exception.InvalidValueException;
import fr.umlv.thaw.util.security.SecurityUtils;

public class Client implements Chatter {
	
	private final String login;
	private final String password;
	
	private Client(@JsonProperty("login")String login, @JsonProperty("cryptedPassword")String password) {
		this.login = Objects.requireNonNull(login);
		this.password = Objects.requireNonNull(password);
	}
	
	static Client create(String login, String password) throws InvalidValueException {
		return new Client(requireValidLogin(login), SecurityUtils.crypt(requireValidPassword(password)));
	}
	
	@Override
	public String toString() {
		return login;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Client)) {
			return false;
		}
		Client other = (Client) obj;
		return login.equals(other.login) && password.equals(other.password);
	}
	
	@Override
	public int hashCode() {
		return login.hashCode() ^ 153;
	}
	
	@Override
	public String getChattingName() {
		return login;
	}
	
	/**
	 * Get the login of the client.
	 *
	 * @return 	The login of the channel.
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Get the crypted password of the client.
	 *
	 * @return 	The crypted password of the client.
	 */
	@JsonIgnore
	public String getCryptedPassword() {
		return password;
	}
	
	private static String requireValidLogin(String login) throws InvalidValueException {
		if (login == null) {
			throw new InvalidValueException("login can not be null");
		}
		if (login.length() < 5) {
			throw new InvalidValueException("login must be greater than 5 characters");
		}
		if (login.length() > 18) {
			throw new InvalidValueException("login must be smaller than 18 characters");
		}
		if (!login.matches("[a-zA-Z0-9_]*")) {
			throw new InvalidValueException("login can only contains letters, digits and character '_'");
		}
		return login;
	}
	
	private static String requireValidPassword(String password) throws InvalidValueException {
		if (password == null) {
			throw new InvalidValueException("password can not be null");
		}
		if (password.length() < 5) {
			throw new InvalidValueException("password must be greater than 5 characters");
		}
		if (password.length() > 20) {
			throw new InvalidValueException("password must be smaller than 20 characters");
		}
		return password;
	}
	
}
