/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.tfd.sm.proxy;

import java.security.SignatureException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.tfd.sm.api.proxy.ProxyPreProcessor;

/**
 * This pre processor adds a header to the proxy request that is picked up by
 * the far end to identify the users. The far end has to a) share the same
 * shared token and b) have something to decode the token. The class was
 * originally designed to work with a TrustedTokenLoginFilter for Sakai 2, but
 * the handshake protocol is so simple it could be used with any end point.
 * There is one configuration item, the sharedSecret that must match the far
 * end. At the moment this component is configured to be a singleton service but
 * if this mechanism of authenticating proxies becomes wide spread we may want
 * this class to be come a service factory so that we can support many trust
 * relationships.
 * 
 */
@Service(value = ProxyPreProcessor.class)
@Component(metatype = true, immediate = true)
@Properties(value = {
		@Property(name = "service.description", value = { "Pre processor for proxy requests to Sakai 2 instance with a trusted token filter." }),
		@Property(name = "service.vendor", value = { "The Sakai Foundation" }) })
public class TrustedLoginTokenProxyPreProcessor implements ProxyPreProcessor {

	public static final String SECURE_TOKEN_HEADER_NAME = "x-sakai-token";
	public static final String TOKEN_SEPARATOR = ";";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TrustedLoginTokenProxyPreProcessor.class);

	@Property(name = "sharedSecret")
	private String sharedSecret = "e2KS54H35j6vS5Z38nK40";

	@Property(name = "port", intValue = 80)
	protected int port;

	@Property(name = "hostname", value = { "localhost" })
	protected String hostname;

	public String getName() {
		return "trusted-token";
	}

	public void preProcessRequest(HttpServletRequest request,
			Map<String, Object> headers, Map<String, Object> templateParams) {

		String user = request.getRemoteUser();
		String hmac;
		final String message = user + TOKEN_SEPARATOR
				+ System.currentTimeMillis();
		try {
			hmac = Signature.calculateRFC2104HMAC(message, sharedSecret);
		} catch (SignatureException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new Error(e);
		}
		final String token = hmac + TOKEN_SEPARATOR + message;
		headers.put(SECURE_TOKEN_HEADER_NAME, token);

		templateParams.put("port", port);
		templateParams.put("hostname", hostname);
	}

	/**
	 * When the bundle gets activated we retrieve the OSGi properties.
	 * 
	 * @param context
	 */
	protected void activate(Map<String, Object> props) {
		// Get the properties from the console.
		sharedSecret = toString(props.get("sharedSecret"),
				"e2KS54H35j6vS5Z38nK40");
		hostname = toString(props.get("hostname"), "localhost");
		LOGGER.info(" Trusted hostname: " + hostname);
		port = toInteger(props.get("port"), 80);
		LOGGER.info("Trusted port: " + port);
	}

	private int toInteger(Object object, int defaultValue) {
		if ( object == null ) {
			return defaultValue;
		}
		return Integer.parseInt(String.valueOf(object));
	}

	private String toString(Object object, String defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		return String.valueOf(object);
	}

}
