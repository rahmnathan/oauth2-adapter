package com.github.rahmnathan.oauth2.adapter.keycloak.camel;

import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2Client;
import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2ClientConfig;
import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.rahmnathan.oauth2.adapter.keycloak.camel.KeycloakCamelRoutes.GET_TOKEN_ROUTE;
import static com.github.rahmnathan.oauth2.adapter.keycloak.camel.KeycloakCamelRoutes.REALM_PROPERTY;

public class KeycloakClientCamel extends OAuth2Client {
    private final Logger logger = LoggerFactory.getLogger(KeycloakClientCamel.class);
    private final ProducerTemplate template;

    public KeycloakClientCamel(ProducerTemplate template, CamelContext context, OAuth2ClientConfig config) {
        super(config);
        new KeycloakCamelRoutes(context, config).configure();
        this.template = template;
    }

    @Override
    public SignedJWT getAccessToken(String requestBody, String realm) throws OAuth2AdapterException {
        logger.debug("Attempting to acquire access token.");
        logger.debug("Realm: {} Request body: {}", realm, requestBody);

        Exchange responseExchange = template.request(GET_TOKEN_ROUTE, exchange -> {
            exchange.setProperty(REALM_PROPERTY, realm);
            exchange.getIn().setBody(requestBody);
        });

        return parseResponse(responseExchange);
    }

    private SignedJWT parseResponse(Exchange responseExchange) throws OAuth2AdapterException {
        Exception responseException = responseExchange.getException();
        if(responseException != null){
            throw new OAuth2AdapterException(responseException);
        }

        return responseExchange.getMessage().getBody(SignedJWT.class);
    }
}
