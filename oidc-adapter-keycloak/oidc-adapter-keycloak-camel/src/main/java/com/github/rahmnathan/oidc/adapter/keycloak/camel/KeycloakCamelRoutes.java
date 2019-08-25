package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.KeycloakTokenResponseParser;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.rahmnathan.oidc.adapter.keycloak.domain.KeycloakConstants.KEYCLOAK_TOKEN_ENDPOINT;

public class KeycloakCamelRoutes {
    private final Logger logger = LoggerFactory.getLogger(KeycloakCamelRoutes.class);
    private final KeycloakTokenResponseParser tokenResponseParser = new KeycloakTokenResponseParser();
    public static final String GET_TOKEN_ROUTE = "direct:getToken";
    private static final String GET_TOKEN_MICROMETER_ROUTE = "micrometer:timer:oidc.keycloak.get-token";
    private final KeycloakConfiguration config;
    private final CamelContext context;

   public KeycloakCamelRoutes(CamelContext context, KeycloakConfiguration config) {
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
                            .redeliveryDelay(500)
                            .maximumRedeliveries(3)
                            .end();

                    from(GET_TOKEN_ROUTE)
                            .hystrix()
                                .hystrixConfiguration()
                                    .executionTimeoutInMilliseconds(5000)
                                .end()
                                .inheritErrorHandler(true)
                                .setHeader(HttpHeaders.CONTENT_TYPE, constant(ContentType.APPLICATION_FORM_URLENCODED))
                                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                                .to(GET_TOKEN_MICROMETER_ROUTE + "?action=start")
                                .to(processUrl(config))
                                .to(GET_TOKEN_MICROMETER_ROUTE + "?action=stop")
                                .unmarshal().json(JsonLibrary.Jackson, JsonNode.class)
                                .process(exchange -> {
                                    SignedJWT accessToken = tokenResponseParser.extractAccessToken(exchange.getIn().getBody(JsonNode.class));
                                    exchange.getIn().setBody(accessToken);
                                })
                            .endHystrix()
                            .end();
                }
            });
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String processUrl(KeycloakConfiguration config){
       String url = config.getUrl();
        if(url.startsWith("https")){
            return url.replace("https", "https4") + String.format(KEYCLOAK_TOKEN_ENDPOINT, config.getRealm());
        } else if (url.startsWith("http")){
            return url.replace("http", "http4") + String.format(KEYCLOAK_TOKEN_ENDPOINT, config.getRealm());
        }

        return url;
    }
}
