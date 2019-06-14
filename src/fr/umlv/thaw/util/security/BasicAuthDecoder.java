package fr.umlv.thaw.util.security;

import java.net.ProtocolException;
import java.util.Base64;
import java.util.Objects;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

public class BasicAuthDecoder {

	private final String username;
	private final String password;

	/**
	 * Create a HTTP Basic authentication decoder from the specified HTTP server request.
	 * An exception is thrown if the request have not a HTTP Authorization header,
	 * or if the Authorization is not Basic,
	 * or the login/password is bad-encoded.

	 * @param 	request the specified server
	 * @throws 	NullPointerException if request is null
	 * @throws 	ProtocolException if the request have not the HTTP Authorization header
	 * @throws 	ProtocolException if the HTTP Authorization is not Basic
	 * @throws 	ProtocolException if the login/password is bas-encoded
	 */
	public BasicAuthDecoder(HttpServerRequest request) throws ProtocolException {
		Objects.requireNonNull(request);
		String data[] = extractData(getDecodedData(request));
		this.username = data[0];
		this.password = data[1];
	}

	/**
	 * Get the decoded login from the request.
	 * 
	 * @return	The decoded login.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the decoded password from the request.
	 * 
	 * @return	The decoded password.
	 */
	public String getPassword() {
		return password;
	}

	private String getDecodedData(HttpServerRequest request) throws ProtocolException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null) {
			throw new ProtocolException("require basic HTTP auth");
		}
		if (!header.startsWith("Basic")) {
			throw new ProtocolException("require basic HTTP auth");
		}
		String encodedData = header.substring(header.indexOf(" ") + 1);
		return new String(Base64.getDecoder().decode(encodedData));
	}
	
	private String[] extractData(String decodedData) throws ProtocolException {
		int index = decodedData.indexOf(':');
		if (index == -1) {
			throw new ProtocolException("bad data-encoding for basic HTTP auth");
		}
		String[] data = new String[2];
		data[0] = decodedData.substring(0, index);
		data[1] = decodedData.substring(index + 1, decodedData.length());
		return data;
	}

}
