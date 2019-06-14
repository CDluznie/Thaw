package fr.umlv.thaw.network.router;

import fr.umlv.thaw.network.ressources.Ressources;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class PublicRouter extends AbstractThawRouter {

	/**
	 * Construct a new router for logged and unlogged clients management.
	 * 
	 * @param 	vertx a vertx
	 * @param	ressources the server ressources
	 * @throws 	NullPointerException if vertx is null
	 * @throws 	NullPointerException if ressources is null
	 */
	public PublicRouter(Vertx vertx, Ressources ressources) {
		super(vertx);
		Router router = vertxRouter();
		router.get("/").handler(rc -> rc.response().sendFile("web/src/index.html"));
	}

}
