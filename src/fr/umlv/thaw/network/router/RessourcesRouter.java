package fr.umlv.thaw.network.router;

import java.util.Objects;

import fr.umlv.thaw.network.ressources.Ressources;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class RessourcesRouter extends AbstractThawRouter {
	
	/**
	 * Construct a new router for ressources management.
	 * 
	 * @param 	vertx a vertx
	 * @param 	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public RessourcesRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Objects.requireNonNull(ressources);
		Router router = vertxRouter();
		router.mountSubRouter("/channels", new ChannelsRouter(vertx, ressources).vertxRouter());
		router.mountSubRouter("/users", new ClientsRouter(vertx, ressources).vertxRouter());
		router.mountSubRouter("/bots", new BotsRouter(vertx, ressources).vertxRouter());
		router.get("/static/*").handler(StaticHandler.create("web/src/static/"));
	}
	
}
