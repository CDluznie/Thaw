package fr.umlv.thaw.network.handler;

import java.net.ProtocolException;
import java.util.Objects;

import fr.umlv.thaw.data.chatter.client.ClientManager;
import fr.umlv.thaw.util.security.BasicAuthDecoder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class BasicAuthHandler implements Handler<RoutingContext> {

	public static final String USER_FIELD = "usr";
	
	private final ClientManager clientManager;
	
	/**
	 * Create a HTTP Basic authentication handler.
	 * If the request do not had a HTTP authentication or if the authentication fail,
	 * a HTTP authentication request is send.
	 * Else, the request is normally execute.
	 * The authentication is based on the the specified client manager.
	 * 
	 * @param 	clients the specified client manager
	 * @throws 	NullPointerException if clients is null
	 */
	public BasicAuthHandler(ClientManager clients) {
		this.clientManager = Objects.requireNonNull(clients);
	}
	
	@Override
	public void handle(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		String user, password;
		if (routingContext.get(USER_FIELD) != null) {
			routingContext.next();
			return;
		}
		try {
			BasicAuthDecoder decoder = new BasicAuthDecoder(routingContext.request());
			user = decoder.getUsername();
			password = decoder.getPassword();
			routingContext.put(USER_FIELD, clientManager.log(user, password));
		} catch (ProtocolException e) {
			routingContext.response().setStatusCode(401).putHeader("WWW-Authenticate", "Basic realm=\"Entrez votre identifiant et votre mot de passe\"").end();
			return;
		} catch (IllegalAccessException e) {
			routingContext.response().setStatusCode(401).putHeader("WWW-Authenticate", "Basic realm=\"" + e.getMessage() + "\"").end();
			return;
		}
		routingContext.next();
	}

}
