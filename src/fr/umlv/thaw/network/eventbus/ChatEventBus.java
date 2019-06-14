package fr.umlv.thaw.network.eventbus;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Objects;

import javax.naming.CommunicationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.data.channel.ChannelManager;
import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.data.chatter.bot.ChattingContext;
import fr.umlv.thaw.data.chatter.client.Client;
import fr.umlv.thaw.data.message.Message;
import fr.umlv.thaw.data.message.MessageManager;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ChatEventBus {
	
	private final EventBus eventbus;
	private final MessageManager messageManager;
	private final BlockingExecutor blockingExecutor;
	
	/**
	 * Construct an event bus for live update chatting.
	 * 
	 * @param 	eventbus a vertx eventbus
	 * @param 	messageManager the message manager
	 * @param 	blockingExecutor a blocking executor
	 * @throws 	NullPointerException if eventbus is null 
	 * @throws 	NullPointerException if messageManager is null
	 * @throws 	NullPointerException if blockingExecutor is null
	 */
	public ChatEventBus(EventBus eventbus, MessageManager messageManager, BlockingExecutor blockingExecutor) {
		this.eventbus = Objects.requireNonNull(eventbus);
		this.messageManager = Objects.requireNonNull(messageManager);
		this.blockingExecutor = Objects.requireNonNull(blockingExecutor);
	}

	/**
	 * Connect the specified channel to the chatting event bus.
	 * 
	 * @param 	channel the specified channel
	 * @throws 	NullPointerException if channel is null
	 */
	public void connectChannel(Channel channel) {
		Objects.requireNonNull(channel);
		eventbus.consumer(ChannelManager.inputChatAdress(channel)).handler(message -> {
			blockingExecutor.execute(() -> {
				String msg = manageClient(channel, message);
				manageBot(channel, message, new ChattingContext() {
					@Override
					public String lastMessage() {
						return msg;
					}
				});
			});
		});
	}
	
	/**
	 * Notify to the event bus the specified chatter has join the channel
	 * 
	 * @param	channel the channel to join
	 * @param 	chatter the specified chatter
	 */
	public void notifyJoin(Channel channel, Chatter chatter) {
		eventbus.publish(ChannelManager.outputJoinChatAdress(channel), Json.encodePrettily(Chatter.asSimpleChatter(chatter)));
	}
	
	/**
	 * Notify to the event bus the specified chatter has left the channel
	 * 
	 * @param	channel the channel to join
	 * @param 	chatter the specified chatter
	 */
	public void notifyLeave(Channel channel, Chatter chatter) {
		eventbus.publish(ChannelManager.outputLeaveChatAdress(channel), Json.encodePrettily(Chatter.asSimpleChatter(chatter)));
	}

	/**
	 * Disconnect the specified channel from the chatting event bus.
	 * 
	 * @param 	channel the specified channel
	 * @throws 	NullPointerException if channel is null
	 */
	public void disconnectChannel(Channel channel) {
		Objects.requireNonNull(channel);
		eventbus.consumer(ChannelManager.inputChatAdress(channel)).unregister();
	}
	
	private void publish(Message message) {
		eventbus.publish(ChannelManager.outputChatAdress(message.getChannel()), Json.encodePrettily(message));
	}
	
	private <T> String manageClient(Channel channel, io.vertx.core.eventbus.Message<T> message) {
		String body = null;
		try {
			JsonObject json = new JsonObject(message.body().toString());
			Client client = decodeSender(json);
			body = decodeBody(json);
			publish(messageManager.create(channel, client, body));
		} catch (CommunicationException | IOException e) {
			message.reply(e.getMessage());
		}
		return body;
	}
	
	private <T> void manageBot(Channel channel, io.vertx.core.eventbus.Message<T> message, ChattingContext context) {
		channel.getBots().forEach(bot -> {
			try {
				bot.notifyBot(context, msg -> {
					try {
						publish(messageManager.create(channel, bot, msg));
					} catch (ServerException | CommunicationException e) {
						message.reply(e.getMessage());
					}
				});
			} catch (InterruptedException e) {
				message.reply("I have been interrupted");
			}
		});
	}
	
	private Client decodeSender(JsonObject json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(json.getJsonObject("sender").toString(), Client.class);
	}
	
	private String decodeBody(JsonObject json) {
		return json.getString("body");
	}

}