package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.KeycloakClient;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.rahmnathan.oidc.adapter.keycloak.camel.KeycloakCamelRoutes.GET_TOKEN_ROUTE;

public class KeycloakClientCamel implements KeycloakClient {
    private final Logger logger = LoggerFactory.getLogger(KeycloakClientCamel.class);
    private final ProducerTemplate template;
    private final CamelContext context;

    public KeycloakClientCamel(ProducerTemplate template, CamelContext context) {
        this.template = template;
        this.context = context;
    }

    @Override
    public SignedJWT getAccessToken(String requestBody) throws TokenProviderException {
       logger.debug("Request body: {}", requestBody);

        Exchange responseExchange = template.request(GET_TOKEN_ROUTE, exchange -> exchange.getIn().setBody(requestBody));

       return parseResponse(responseExchange);
    }

    @Override
    public void init(KeycloakConfiguration configuration) {
        new KeycloakCamelRoutes(context, configuration).configure();
    }

    private SignedJWT parseResponse(Exchange responseExchange) throws TokenProviderException {
        Exception responseException = responseExchange.getException();
        if(responseException != null){
            throw new TokenProviderException(responseException);
        }

        return responseExchange.getOut().getBody(SignedJWT.class);
    }
}
