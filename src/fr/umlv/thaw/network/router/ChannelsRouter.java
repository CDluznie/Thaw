package fr.umlv.thaw.network.router;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;
import java.util.Objects;

import javax.naming.NameNotFoundException;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.data.channel.ChannelManager;
import fr.umlv.thaw.data.chatter.bot.Bot;
import fr.umlv.thaw.data.chatter.bot.BotManager;
import fr.umlv.thaw.data.chatter.client.Client;
import fr.umlv.thaw.data.message.Message;
import fr.umlv.thaw.data.message.MessageManager;
import fr.umlv.thaw.network.eventbus.ChatEventBus;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import fr.umlv.thaw.network.handler.BasicAuthHandler;
import fr.umlv.thaw.network.handler.ChatEventBusHandler;
import fr.umlv.thaw.network.ressources.Ressources;
import fr.umlv.thaw.util.exception.InvalidValueException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ChannelsRouter extends AbstractThawRouter {

	private final ChannelManager channelManager;
	private final MessageManager messageManager;
	private final BotManager botManager;
	private final ChatEventBus eventBus;
	private final BlockingExecutor blockingExecutor;

	/**
	 * Construct a new router for channels management.
	 * 
	 * @param 	vertx a vertx
	 * @param 	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public ChannelsRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Objects.requireNonNull(ressources);
		this.channelManager = ressources.getChannelManager();
		this.messageManager = ressources.getMessageManager();
		this.botManager = ressources.getBotManager();
		this.eventBus = ressources.getEventBus();
		this.blockingExecutor = ressources.getRouterBlockingExecutor();
		Router router = vertxRouter();
		router.post("/*").handler(new BasicAuthHandler(ressources.getClientManager()));
		router.delete("/*").handler(new BasicAuthHandler(ressources.getClientManager()));
		router.get("/").handler(this::routerGetAllChannels);
		router.post("/").handler(this::routerAddOneChannel);
		router.route("/eventbus/*").handler(new ChatEventBusHandler(vertx));
		router.get("/:channel").handler(this::routerGetOneChannel);
		router.delete("/:channel").handler(this::routerdeleteOneChannel);
		router.get("/:channel/messages").handler(this::routerGetMessagesChannel);
		router.post("/:channel/bot").handler(this::addOneBotChannel);
	}
	
	private List<Channel> getAllChannels(RoutingContext routingContext) {
		List<Channel> x = channelManager.getAllChannels();
		return x;
	}
	
	private void routerGetAllChannels(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8").end(Json.encodePrettily(getAllChannels(routingContext)));
		});
	}
	
	private Channel createChannel(RoutingContext routingContext) {
		String name;
		try {
			JsonObject json = routingContext.getBodyAsJson();
			name = json.getString("name");
		} catch (DecodeException e) {
			routingContext.response().setStatusCode(400).end("undecodable request");
			return null;
		}
		if (name == null) {
			routingContext.response().setStatusCode(400).end("missing parametter name");
			return null;
		}
		try {
			return channelManager.create(name, getClient(routingContext));
		} catch (ServerException e) {
			routingContext.response().setStatusCode(500).end(e.getMessage());
			return null;
		} catch (InvalidValueException e) {
			routingContext.response().setStatusCode(400).end(e.getMessage());
			return null;
		}
	}

	private void routerAddOneChannel(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			Channel channel = createChannel(routingContext);
			if (channel != null) {
				eventBus.connectChannel(channel);
				routingContext.response().setStatusCode(201).end(Integer.toString(channel.getId()));
			}
		});
	}

	private Channel getChannel(RoutingContext routingContext) {
		String id = routingContext.request().getParam("channel");
		if (id == null) {
			routingContext.response().setStatusCode(404).end("can not get the channel id");
			return null;
		}
		Channel channel = channelManager.get(id);
		if (channel == null) {
			routingContext.response().setStatusCode(404).end("unknow channel");
			return null;
		}
		return channel;
	}
	
	private void routerGetOneChannel(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			Channel channel = getChannel(routingContext);
			if (channel != null) {
				routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8").end(Json.encodePrettily(channel));
			}
		});
	}
	
	private void routerdeleteOneChannel(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			Channel channel = getChannel(routingContext);
			if (channel == null) {
				return;
			}
			if (!channel.getOwner().equals(getClient(routingContext))) {
				routingContext.response().setStatusCode(403).end("you are not the owner of the channel");
			}
			try {
				channelManager.remove(channel);
			} catch (ServerException e) {
				routingContext.response().setStatusCode(500).end(e.getMessage());
				return;
			}
			eventBus.disconnectChannel(channel);
			routingContext.response().setStatusCode(204).end();
		});
	}
	
	private List<Message> getMessagesChannel(RoutingContext routingContext) {
		Channel channel = getChannel(routingContext);
		if (channel == null) {
			return null;
		}
		try {
			return messageManager.getMessages(channel);
		} catch (ServerException e) {
			routingContext.response().setStatusCode(500).end(e.getMessage());
			return null;
		}
	}
	
	private void routerGetMessagesChannel(RoutingContext routingContext) {
		blockingExecutor.execute(() -> {
			List<Message> messages = getMessagesChannel(routingContext);
			if (messages != null) {
				routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8").end(Json.encodePrettily(messages));
			}
		});
	}
	
	private Bot createBot(RoutingContext routingContext) {
		String name;
		JsonArray jsonArgs;
		try {
			JsonObject json = routingContext.getBodyAsJson();
			name = json.getString("name");
			jsonArgs = json.getJsonArray("args");
		} catch (DecodeException e) {
			routingContext.response().setStatusCode(400).end("undecodable request");
			return null;
		}
		if (name == null) {
			routingContext.response().setStatusCode(400).end("missing parametter name");
			return null;
		}
		if (jsonArgs == null) {
			routingContext.response().setStatusCode(400).end("missing parametter args");
			return null;
		}
		String[] args = new String[jsonArgs.size()];
		for (int i = 0; i < jsonArgs.size(); i++) {
			args[i] = jsonArgs.getString(i);
		}
		try {
			return botManager.create(name, args);
		} catch (NameNotFoundException e) {
			routingContext.response().setStatusCode(404).end(e.getMessage());
			return null;
		} catch (IOException e) {
			routingContext.response().setStatusCode(400).end(e.getMessage());
			return null;
		}
	}
	
	private void addOneBotChannel(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		blockingExecutor.execute(() -> {
			Bot bot = createBot(routingContext);
			if (bot == null) {
				return;
			}
			Channel channel = getChannel(routingContext);
			if (channel == null) {
				return;
			}
			if (!channel.getOwner().equals(getClient(routingContext))) {
				routingContext.request().response().setStatusCode(403).end("you are not the owner of the channel");
				return;
			}
			if (channel.addBot(bot)) {
				routingContext.request().response().setStatusCode(201).end();
			} else {
				routingContext.request().response().setStatusCode(202).end("bot already in channel");
			}
		});
	}
	
	private Client getClient(RoutingContext routingContext) {
		return routingContext.get(BasicAuthHandler.USER_FIELD);
	}

}