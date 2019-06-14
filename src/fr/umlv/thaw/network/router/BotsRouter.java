package fr.umlv.thaw.network.router;

import java.util.Objects;

import fr.umlv.thaw.data.chatter.bot.BotManager;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import fr.umlv.thaw.network.ressources.Ressources;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class BotsRouter extends AbstractThawRouter {

	private final BotManager clientManager;
	private final BlockingExecutor executor;

	/**
	 * Construct a new router for bots management.
	 * 
	 * @param 	vertx a vertx
	 * @param 	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public BotsRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Objects.requireNonNull(ressources);
		this.clientManager = ressources.getBotManager();
		this.executor = ressources.getRouterBlockingExecutor();
		Router router = vertxRouter();
		router.get("/").handler(this::routerGetAllBots);
	}
	
	private void routerGetAllBots(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		executor.execute(() -> {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(clientManager.getBots()));
		});
	}
	
}
