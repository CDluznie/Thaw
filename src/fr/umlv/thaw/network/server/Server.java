package fr.umlv.thaw.network.server;

import java.rmi.ServerException;
import java.sql.SQLException;

import fr.umlv.thaw.data.channel.ChannelManager;
import fr.umlv.thaw.network.ressources.Ressources;
import fr.umlv.thaw.network.router.MainRouter;
import fr.umlv.thaw.util.database.DataBaseConnection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;

public class Server extends AbstractVerticle {

	public static final int IP_PORT = 8181;
	
	@Override
	public void start() throws Exception {
		System.out.println("server creating");
		Ressources ressources = getRessources();
		Router router = new MainRouter(vertx, ressources).vertxRouter();
		vertx
			.createHttpServer() 
			.requestHandler(router::accept)
			.listen(IP_PORT);
		deleteUnusedPeriodic(ressources.getChannelManager(), 60000);
		System.out.println("server listening port " + IP_PORT);
	}	
	
	private Ressources getRessources() {
		DataBaseConnection connection = null; // Because System.exit
		try {
			connection = new DataBaseConnection();
		} catch (SQLException e) {
			System.err.println("an error occurs during the database connection");
			System.exit(1);
		}
		return new Ressources(connection, vertx);
	}
	
	private void deleteUnusedPeriodic(ChannelManager channelManager, long delay) {
		vertx.setPeriodic(delay, __ -> {
			channelManager.getAllChannels().stream().filter(channel -> channel.getChatters().isEmpty()).forEach(channel -> {
				try {
					channelManager.remove(channel);
				} catch (ServerException e) {

				}
			});
		});
	}
	
	public static void main(String[] args) {
		System.out.println("-- rest server --");
		Server server = new Server();
		VertxOptions options = new VertxOptions(); 
		options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
		Vertx vertx = Vertx.vertx(options);
		vertx.deployVerticle(server);
	}
	
}
