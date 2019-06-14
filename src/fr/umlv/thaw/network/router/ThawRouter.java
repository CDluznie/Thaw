package fr.umlv.thaw.network.router;

import io.vertx.ext.web.Router;

public interface ThawRouter {
	
	/**
	 * Get the vertx router from the thaw router.
	 * 
	 * @return	The vertx router.
	 */
	public Router vertxRouter();
	
}
