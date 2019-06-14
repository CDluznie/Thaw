package fr.umlv.thaw.network.router;

import java.util.Objects;

import javax.naming.NameAlreadyBoundException;

import fr.umlv.thaw.data.chatter.client.Client;
import fr.umlv.thaw.data.chatter.client.ClientManager;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import fr.umlv.thaw.network.ressources.Ressources;
import fr.umlv.thaw.util.exception.InvalidValueException;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ClientsRouter extends AbstractThawRouter {

	private final ClientManager clientManager;
	private final BlockingExecutor executor;
	
	/**
	 * Construct a new router for clients management.
	 * 
	 * @param 	vertx a vertx
	 * @param 	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public ClientsRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Objects.requireNonNull(ressources);
		this.clientManager = ressources.getClientManager();
		this.executor = ressources.getRouterBlockingExecutor();
		Router router = vertxRouter();	
		router.get("/").handler(this::routerGetAllClients);
		router.post("/").handler(this::routerAddOneClient);
	}
	
	private void routerGetAllClients(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		executor.execute(() -> {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(clientManager.getAll()));
		});
	}
	
	private Client createClient(RoutingContext routingContext) {
		try {
			JsonObject json = routingContext.getBodyAsJson();
			return clientManager.create(json.getString("login"), json.getString("password"));
		} catch (DecodeException e) {
			routingContext.response().setStatusCode(400).end("undecodable request");
			return null;
		} catch (NameAlreadyBoundException | InvalidValueException e) {
			routingContext.response().setStatusCode(400).end(e.getMessage());
			return null;
		}
	}
	
	private void routerAddOneClient(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		executor.execute(() -> {
			Client client = createClient(routingContext);
			if (client != null) {
				routingContext.response().setStatusCode(201).end();
			}
 		});
	}
	
}
