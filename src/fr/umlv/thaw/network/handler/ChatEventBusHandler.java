package fr.umlv.thaw.network.handler;

import java.util.Objects;

import fr.umlv.thaw.data.channel.ChannelManager;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class ChatEventBusHandler implements Handler<RoutingContext> {

	private final SockJSHandler handler;
	
	/**
	 * Create a handler for the chatting event bus.
	 * The handler will transmit the message on the adress wich respect
	 * the regular expression of input/output channel adress.
	 * 
	 * @param	vertx a vertx
	 * @throws 	NullPointerException if vertx is null
	 */
	public ChatEventBusHandler(Vertx vertx) {
		Objects.requireNonNull(vertx);
		BridgeOptions opts = new BridgeOptions()
				.addInboundPermitted(new PermittedOptions().setAddressRegex(ChannelManager.getInputRegexChatAdress()))
				.addOutboundPermitted(new PermittedOptions().setAddressRegex(ChannelManager.getOutputRegexChatAdress()));
		this.handler = SockJSHandler.create(vertx).bridge(opts);
	}
	
	@Override
	public void handle(RoutingContext routingContext) {
		Objects.requireNonNull(routingContext);
		//try {
			handler.handle(routingContext);
		//} catch (RuntimeException e) { // Closed connection
	
		//}
	}

}
