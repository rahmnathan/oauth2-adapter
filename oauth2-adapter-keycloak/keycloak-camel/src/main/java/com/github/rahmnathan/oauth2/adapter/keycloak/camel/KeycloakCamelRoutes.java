package com.github.rahmnathan.oauth2.adapter.keycloak.camel;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.rahmnathan.oauth2.adapter.domain.AccessTokenResponseParser;
import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2ClientConfig;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeycloakCamelRoutes {
    private final Logger logger = LoggerFactory.getLogger(KeycloakCamelRoutes.class);
    private final AccessTokenResponseParser tokenResponseParser = new AccessTokenResponseParser();
    public static final String GET_TOKEN_ROUTE = "direct:getToken";
    private static final String GET_TOKEN_MICROMETER_ROUTE = "micrometer:timer:oauth2.keycloak.get-token";
    public static final String REALM_PROPERTY = "realmProperty";
    private final OAuth2ClientConfig config;
    private final CamelContext context;

   public KeycloakCamelRoutes(CamelContext context, OAuth2ClientConfig config) {
        this.config = config;
        this.context = context;
    }

    public void configure()  {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    onException(HttpOperationFailedException.class)
                            .onWhen(exchange -> exchange.getException(HttpOperationFailedException.class).getStatusCode() >= 500)
                            .useExponentialBackOff()
                            .redeliveryDelay(config.getInitialRetryDelay())
                            .maximumRedeliveries(config.getRetryCount())
                            .end();

                    from(GET_TOKEN_ROUTE)
                            .circuitBreaker()
                                .resilience4jConfiguration()
                                    .timeoutDuration(config.getTimoutMs())
                                    .timeoutEnabled(true)
                                .end()
                                .inheritErrorHandler(true)
                                .setHeader(HttpHeaders.CONTENT_TYPE, constant(ContentType.APPLICATION_FORM_URLENCODED))
                                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                                .setHeader(Exchange.HTTP_PATH, simple("/realms/${property." + REALM_PROPERTY + "}/protocol/openid-connect/token"))
                                .to(GET_TOKEN_MICROMETER_ROUTE + "?action=start")
                                .to(config.getUrl())
                                .to(GET_TOKEN_MICROMETER_ROUTE + "?action=stop")
                                .unmarshal().json(JsonLibrary.Jackson, JsonNode.class)
                                .process(exchange -> {
                                    SignedJWT accessToken = tokenResponseParser.extractAccessToken(exchange.getIn().getBody(JsonNode.class));
                                    exchange.getIn().setBody(accessToken);
                                })
                            .endCircuitBreaker()
                            .end();
                }
            });
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
