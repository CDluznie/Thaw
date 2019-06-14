package fr.umlv.thaw.network.ressources;

import java.io.IOException;
import java.util.Objects;

import javax.naming.NameNotFoundException;

import fr.umlv.thaw.data.channel.ChannelManager;
import fr.umlv.thaw.data.chatter.bot.Bot;
import fr.umlv.thaw.data.chatter.bot.BotManager;
import fr.umlv.thaw.data.chatter.client.ClientManager;
import fr.umlv.thaw.data.message.MessageManager;
import fr.umlv.thaw.network.eventbus.ChatEventBus;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import fr.umlv.thaw.util.database.DataBaseConnection;
import fr.umlv.thaw.util.exception.InvalidValueException;
import io.vertx.core.Vertx;

public class Ressources {

	private final ClientManager clientManager;
	private final ChannelManager channelManager;
	private final MessageManager messageManager;
	private final BotManager botManager;
	private final ChatEventBus eventBus;
	private final BlockingExecutor blockingExecutor;
	
	/**
	 * Construct the required ressources for the server running.
	 * 
	 * @param	connection a connection to the database
	 * @param 	vertx a vertx
	 * @throws 	NullPointerException if connection is null
	 * @throws 	NullPointerException if vertx is null
	 */
	public Ressources(DataBaseConnection connection, Vertx vertx) {
		Objects.requireNonNull(connection);
		Objects.requireNonNull(vertx);
		this.clientManager = new ClientManager();
		this.channelManager = new ChannelManager(connection);
		this.messageManager = new MessageManager(connection);
		this.botManager = new BotManager(vertx);
		this.blockingExecutor = new BlockingExecutor(vertx.createSharedWorkerExecutor("thaw-router-workers"));
		this.eventBus = new ChatEventBus(vertx.eventBus(), messageManager, blockingExecutor);
		addPermanentChannel();
	}
	
	private void addPermanentChannel() {
		try {
			Bot thaw = botManager.create("thaw");
			Bot debug = botManager.create("ping-bot");
			Bot rssJava = botManager.create("rss-bot", "http://www.javaworld.com/category/core-java/index.rss");
			Bot rssActu = botManager.create("rss-bot", "http://www.lemonde.fr/m-actu/rss_full.xml");
			Bot gith = botManager.create("github-bot", "jcabi/jcabi-github");
			eventBus.connectChannel(channelManager.create("Java", thaw, debug, rssJava, gith));
			eventBus.connectChannel(channelManager.create("Actualites", thaw, thaw, rssActu));
			eventBus.connectChannel(channelManager.create("NouveauSurThaw", thaw, thaw));
		} catch (NameNotFoundException | InvalidValueException | IOException e) {
			System.err.println("an error occurs during channels creations");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Get the client manager from the ressources.
	 * 
	 * @return	The client manager.
	 */
	public ClientManager getClientManager() {
		return clientManager;
	}

	/**
	 * Get the channel manager from the ressources.
	 * 
	 * @return	The channel manager.
	 */
	public ChannelManager getChannelManager() {
		return channelManager;
	}
	
	/**
	 * Get the message manager from the ressources.
	 * 
	 * @return	The message manager.
	 */
	public MessageManager getMessageManager() {
		return messageManager;
	}
	
	/**
	 * Get the bot manager from the ressources.
	 * 
	 * @return	The bot manager.
	 */
	public BotManager getBotManager() {
		return botManager;
	}
	
	/**
	 * Get the chatting event bus from the ressources.
	 * 
	 * @return	The chatting event bus.
	 */
	public ChatEventBus getEventBus() {
		return eventBus;
	}
	
	/**
	 * Get the blocking executor from the ressources for router usage.
	 * 
	 * @return	The client manager.
	 */
	public BlockingExecutor getRouterBlockingExecutor() {
		return blockingExecutor;
	}
	
}
