package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClient;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClientConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.rahmnathan.oidc.adapter.keycloak.camel.KeycloakCamelRoutes.GET_TOKEN_ROUTE;
import static com.github.rahmnathan.oidc.adapter.keycloak.camel.KeycloakCamelRoutes.REALM_PROPERTY;

public class KeycloakClientCamel extends KeycloakClient {
    private final Logger logger = LoggerFactory.getLogger(KeycloakClientCamel.class);
    private final ProducerTemplate template;

    public KeycloakClientCamel(ProducerTemplate template, CamelContext context, KeycloakClientConfig config) {
        super(config);
        new KeycloakCamelRoutes(context, config).configure();
        this.template = template;
    }

    @Override
    public SignedJWT getAccessToken(String requestBody, String realm) throws TokenProviderException {
        logger.debug("Attempting to acquire access token.");
        logger.debug("Realm: {} Request body: {}", realm, requestBody);

        Exchange responseExchange = template.request(GET_TOKEN_ROUTE, exchange -> {
            exchange.setProperty(REALM_PROPERTY, realm);
            exchange.getIn().setBody(requestBody);
        });

        return parseResponse(responseExchange);
    }

    private SignedJWT parseResponse(Exchange responseExchange) throws TokenProviderException {
        Exception responseException = responseExchange.getException();
        if(responseException != null){
            throw new TokenProviderException(responseException);
        }

        return responseExchange.getOut().getBody(SignedJWT.class);
    }
}
