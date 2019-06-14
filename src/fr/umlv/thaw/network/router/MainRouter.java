package fr.umlv.thaw.network.router;

import java.net.ProtocolException;
import java.util.Objects;

import fr.umlv.thaw.data.chatter.client.Client;
import fr.umlv.thaw.data.chatter.client.ClientManager;
import fr.umlv.thaw.network.handler.BasicAuthHandler;
import fr.umlv.thaw.network.ressources.Ressources;
import fr.umlv.thaw.util.security.BasicAuthDecoder;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainRouter extends AbstractThawRouter {

	private final ClientManager clientManager;
	
	/**
	 * Construct a new main router.
	 * 
	 * @param 	vertx a vertx
	 * @param 	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public MainRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Objects.requireNonNull(ressources);
		this.clientManager = ressources.getClientManager();
		Router router = vertxRouter();
		router.route().handler(BodyHandler.create());
		router.get("/").handler(this::routerMainPage);
		router.mountSubRouter("/public", new PublicRouter(vertx, ressources).vertxRouter());
		router.mountSubRouter("/logged", new LoggedRouter(vertx, ressources).vertxRouter());
		router.mountSubRouter("/ressources", new RessourcesRouter(vertx, ressources).vertxRouter());
	}
	
	private void routerMainPage(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		if (routingContext.get(BasicAuthHandler.USER_FIELD) != null) {
			routingContext.reroute("/logged");
			return;
		}
		Client client = log(routingContext);
		if (client == null) {
			routingContext.reroute("/public");
			return;
		}
		routingContext.put(BasicAuthHandler.USER_FIELD, client);
		routingContext.reroute("/logged");
	}
	
	private Client log(RoutingContext routingContext) {
		try {
			BasicAuthDecoder decoder = new BasicAuthDecoder(routingContext.request());
			return clientManager.log(decoder.getUsername(), decoder.getPassword());
		} catch (ProtocolException | IllegalAccessException e) {
			return null;
		}
	}
	
}
