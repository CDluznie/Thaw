package fr.umlv.thaw.network.router;

import java.util.Objects;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

abstract class AbstractThawRouter implements ThawRouter {

	private final Router router;
	
	public AbstractThawRouter(Vertx vertx) {
		Objects.requireNonNull(vertx);
		this.router = Router.router(vertx);
	}
	
	@Override
	public Router vertxRouter() {
		return router;
	}
	
}
