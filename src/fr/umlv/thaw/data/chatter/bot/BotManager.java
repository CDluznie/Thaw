package fr.umlv.thaw.data.chatter.bot;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import fr.umlv.thaw.data.chatter.bot.impl.PingBotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.impl.DefaultThawBotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.impl.GitBotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.impl.GithubBotFactoyEntry;
import fr.umlv.thaw.data.chatter.bot.impl.RSSBotFactoryEntry;
import fr.umlv.thaw.network.executor.BlockingExecutor;
import io.vertx.core.Vertx;

public class BotManager {

	private final BotsFactories factories;
	private final Vertx vertx;
	
	/**
	 * Create a thread-safe manager for bots gestion.
	 * 
	 * @param 	vertx a vertx
	 * @throws	NullPointerException if verts is null
	 */
	public BotManager(Vertx vertx) {
		this.factories = new BotsFactories();
		this.vertx = Objects.requireNonNull(vertx);
		load();
	}
	
	private void load() {
		List<? extends BotFactoryEntry> bots = List.of(
				new DefaultThawBotFactoryEntry(),
				new PingBotFactoryEntry(),
				new RSSBotFactoryEntry(),
				new GitBotFactoryEntry(),
				new GithubBotFactoyEntry());
		bots.forEach(entry -> {
			try {
				factories.register(entry);
			} catch (NameAlreadyBoundException e) {
				System.err.println("an error occurs during the " + entry.getName() + " bot loading");
				System.err.println(e.getMessage());
				System.exit(1);
			}
		});
	}
	
	/**
	 * Create an instance of a parameterized registered bot.
	 * The specified name must be know from the storer.
	 * 
	 * @param 	name the name of bot
	 * @param 	args the arguments of the bot
	 * @return	The instance of the bot.
	 * @throws	NullPointerException if name is null
	 * @throws	NullPointerException if args is null
	 * @throws 	NameNotFoundException if the name is not registered in the storer
	 * @throws 	IOException if an errors occurs during the arguments parsing
	 */
	public Bot create(String name, String ... args) throws NameNotFoundException, IOException {
		Objects.requireNonNull(name);
		Objects.requireNonNull(args);
		Bot bot = factories.get(name).create(args);
		new BlockingExecutor(vertx.createSharedWorkerExecutor("thaw-thread-worker")).execute(() -> {
			for (;;) {
				try {
					bot.run();
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		return bot;
	}
	
	/**
	 * Get the names of the available bots.
	 * The returned set is immutable.
	 * 
	 * @return	The set of the available names bots.
	 */
	public Set<String> getBots() {
		return factories.getAllNames();
	}
	
}
