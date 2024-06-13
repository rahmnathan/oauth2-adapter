package com.github.rahmnathan.oauth2.adapter.keycloak.resilience4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rahmnathan.oauth2.adapter.domain.AccessTokenResponseParser;
import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2Client;
import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2ClientConfig;
import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;
import com.nimbusds.jwt.SignedJWT;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Supplier;

public class KeycloakClientResilience4j extends OAuth2Client {
    private final Logger logger = LoggerFactory.getLogger(KeycloakClientResilience4j.class);
    private final AccessTokenResponseParser responseParser = new AccessTokenResponseParser();

    public KeycloakClientResilience4j(OAuth2ClientConfig config){
        super(config);
    }

    @Override
    public SignedJWT getAccessToken(String requestBody, String realm) throws OAuth2AdapterException {
        logger.info("Logging in with Keycloak");

        Supplier<SignedJWT> supplier = () -> getAccessTokenInternal(requestBody, realm);

        // Create a CircuitBreaker (use default configuration)
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("Keycloak");
        // Create a Retry with at most 3 retries and a fixed time interval between retries of 500ms
        Retry retry = Retry.ofDefaults("Keycloak");

        Supplier<SignedJWT> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);

        // Execute the decorated supplier and recover from any exception
        return decoratedSupplier.get();
    }

    public SignedJWT getAccessTokenInternal(String requestBody, String realm) {
        String urlString = config.getUrl() + "/realms/" + realm + "/protocol/openid-connect/token";

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setFixedLengthStreamingMode(requestBody.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                wr.write(requestBody.getBytes());
            }

            StringBuilder result = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                br.lines().forEachOrdered(result::append);
            } finally {
                urlConnection.disconnect();
            }

            JsonNode response = new ObjectMapper().readValue(result.toString(), JsonNode.class);
            return responseParser.extractAccessToken(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
