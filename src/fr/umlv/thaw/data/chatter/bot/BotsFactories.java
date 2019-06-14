package fr.umlv.thaw.data.chatter.bot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

public class BotsFactories {

	@FunctionalInterface
	static interface BotFactory {
		
		Bot create(String ... args) throws IOException;
		
	}
	
	private final HashMap<String,BotFactory> factories;
	
	/**
	 * Create a structure that contains some factories of bots.
	 */
	public BotsFactories() {
		this.factories = new HashMap<>();
	}
	
	/**
	 * Register in the structure the specified new bot factory entry
	 * parameterized by the specified factory options
	 * 
	 * @param 	factoryEntry the specified bot factory entry
	 * @throws	NullPointerException if factoryOption is null
	 * @throws 	NameAlreadyBoundException if the structure already contains the name of the bot of factory options
	 */
	public void register(BotFactoryEntry factoryEntry) throws NameAlreadyBoundException {
		Objects.requireNonNull(factoryEntry);
		String name = factoryEntry.getName();
		BotFactory botFactory = (args -> new Bot(name, factoryEntry.getBehavior(args)));
		if (name == null || botFactory == null) {
			throw new NullPointerException();
		}
		if (factories.putIfAbsent(name, botFactory) != null) {
			throw new NameAlreadyBoundException("name conflict for '" + name + "'");
		}
	}
	
	/**
	 * Get the factory for create a bot with the specified name
	 * 
	 * @param 	name the name of bot
	 * @return	The factory for the specified name.
	 * @throws	NullPointerException if name is null
	 * @throws 	NameNotFoundException if the structure do not contains factory for specified name
	 */
	public BotFactory get(String name) throws NameNotFoundException {
		Objects.requireNonNull(name);
		BotFactory factory = factories.get(name);
		if (factory == null) {
			throw new NameNotFoundException("no factory for '" + name + "'");
		}
		return factory;
	}
	
	/**
	 * Get the names of the available bots.
	 * The returned set is immutable.
	 * 
	 * @return	The set of the available names bots.
	 */
	public Set<String> getAllNames() {
		return factories.keySet();
	}
	
}
