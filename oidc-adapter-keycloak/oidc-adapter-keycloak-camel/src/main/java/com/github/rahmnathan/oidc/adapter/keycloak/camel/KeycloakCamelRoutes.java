package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.KeycloakTokenResponseParser;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClientConfig;
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

public class KeycloakCamelRoutes {
    private final Logger logger = LoggerFactory.getLogger(KeycloakCamelRoutes.class);
    private final KeycloakTokenResponseParser tokenResponseParser = new KeycloakTokenResponseParser();
    public static final String GET_TOKEN_ROUTE = "direct:getToken";
    private static final String GET_TOKEN_MICROMETER_ROUTE = "micrometer:timer:oidc.keycloak.get-token";
    public static final String REALM_PROPERTY = "realmProperty";
    private final KeycloakClientConfig config;
    private final CamelContext context;

   public KeycloakCamelRoutes(CamelContext context, KeycloakClientConfig config) {
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
                            .hystrix()
                                .hystrixConfiguration()
                                    .executionTimeoutInMilliseconds(config.getTimoutMs())
                                .end()
                                .inheritErrorHandler(true)
                                .setHeader(HttpHeaders.CONTENT_TYPE, constant(ContentType.APPLICATION_FORM_URLENCODED))
                                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                                .setHeader(Exchange.HTTP_PATH, simple("/realms/${property." + REALM_PROPERTY + "}/protocol/openid-connect/token"))
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

    private String processUrl(KeycloakClientConfig config){
       String url = config.getUrl();
        if(url.startsWith("https")){
            return url.replace("https", "https4");
        } else if (url.startsWith("http")){
            return url.replace("http", "http4");
        }

        return url;
    }
}
