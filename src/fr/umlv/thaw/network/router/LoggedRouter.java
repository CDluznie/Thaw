package fr.umlv.thaw.network.router;

import java.util.Objects;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.data.channel.ChannelManager;
import fr.umlv.thaw.data.chatter.client.Client;
import fr.umlv.thaw.data.chatter.client.ClientManager;
import fr.umlv.thaw.network.eventbus.ChatEventBus;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import fr.umlv.thaw.network.handler.BasicAuthHandler;
import fr.umlv.thaw.network.ressources.Ressources;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LoggedRouter extends AbstractThawRouter {

	private final ChannelManager channelManager;
	private final BlockingExecutor blockingExecutor;
	private final ChatEventBus eventbus;
	
	/**
	 * Construct a new router for logged clients management.
	 * 
	 * @param 	vertx a vertx
	 * @param 	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public LoggedRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Objects.requireNonNull(ressources);
		this.channelManager = ressources.getChannelManager();
		this.eventbus = ressources.getEventBus();
		this.blockingExecutor = ressources.getRouterBlockingExecutor();
		Router router = vertxRouter();
		router.route("/*").handler(new BasicAuthHandler(ressources.getClientManager()));
		router.get("/").handler(this::routerGetHomePage);
		router.get("/me/username").handler(this::routerGetUser);
		router.get("/chat").handler(this::routerConnectChannelPage);
		router.delete("/chat").handler(this::routerDisconnectChannelPage);
	}
	
	private void routerGetHomePage(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		routingContext.response().sendFile("web/src/home.html");
	}
	
	private void routerGetUser(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			JsonObject client = ClientManager.showConfidential(getClient(routingContext));
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(client));
		});
	}
	
	private Channel getChannel(RoutingContext routingContext) {
		String idChannel = routingContext.request().params().get("channel");
		if (idChannel == null) {
			routingContext.response().setStatusCode(404).end("unknow channel");
			return null;
		}
		Channel channel = channelManager.get(idChannel);
		if (channel == null) {
			routingContext.response().setStatusCode(404).end("unknow channel");
			return null;
		}
		return channel;
	}
	
	private Client getClient(RoutingContext routingContext) {
		return routingContext.get(BasicAuthHandler.USER_FIELD);
	}
	
	private void routerConnectChannelPage(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			Client client = getClient(routingContext);
			Channel channel = getChannel(routingContext);
			if (client == null || channel == null) {
				return;
			}
			if (channel.join(client)) {
				eventbus.notifyJoin(channel, client);
			}
			routingContext.response().sendFile("web/src/chat.html");
		});
	}
	
	private void routerDisconnectChannelPage(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			Client client = getClient(routingContext);
			Channel channel = getChannel(routingContext);
			if (client == null || channel == null) {
				return;
			}
			if (channel.leave(client)) {
				eventbus.notifyLeave(channel, client);
			}
			routingContext.response().setStatusCode(204).end();
		});
	}

}
