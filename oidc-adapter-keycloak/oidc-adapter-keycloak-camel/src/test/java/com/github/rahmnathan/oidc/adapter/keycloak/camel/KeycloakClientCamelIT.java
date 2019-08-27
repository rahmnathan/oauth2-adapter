package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.OidcService;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.OidcClientConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.Duration;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredentialJwt;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.time.temporal.ChronoUnit;

public class KeycloakClientCamelIT extends CamelTestSupport {
    private final String url = "http://localhost:8080/auth";
    private KeycloakClientCamel keycloakClientCamel;
    private OidcService keycloakService;

    @Before
    public void init() {
        OidcClientConfig clientConfig = OidcClientConfig.builder()
                .url(url)
                .initialRetryDelay(400)
                .retryCount(2)
                .timoutMs(4000)
                .build();

        this.keycloakClientCamel = new KeycloakClientCamel(template, context, clientConfig);

        OidcCredentialJwt jwtConfig = OidcCredentialJwt.builder()
                .keyAlg("RSA")
                .privateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCZceQf20lV3Ba34bV44tegIJHsRWM3LDDqOJwldmkS/ohp1wwHkaqacstiem9pLzkzXxU7hhS/Hhm88t/DBInNpX0/n3ecNd+ejScRI1Yctz2JyXsjHD5aOZJ/lD/YoOeQuWZS2AEX64aua4oqJCFDl/W/xXzO0OYOAlTRMcOBRbguRV61IVFzzWTRVZsvMVGs2O1k0utzmkM7OYWwDLzarB1HDMaSTGpbtr/z1tIcRWZuVd3APotjE3Jsu8sIf50tfG+AS6Q/w7qWNSmtBDU1iUiq+IuAoWdxp1FooMYW/UH15b8Wlm+WDg2WttQstUrEC0tBuSTnF0PxPoI+3r25AgMBAAECggEBAISvJRIe7rUd0U9ED0Xu8YF3aHckMPzea5W9SdhjIczY6GCNIcvRu/I93XzUXk5YFWBEkfmcoYf5oUvvwGnEHN0Egt9nJ2iedTWG3QdGxQmjscTZwm7D127xZaqIKQVLbbGJf9+XBKsIcLeWKizyfQkT997SCHBV1vi6L1mNbH70y3YuR1x3PaUidrw0KMg0LXk1h5oYxZGBGRkQlWpQFGEnYTNesTAKV+65YdPCPy/3mn4jtZ0W5zsOyODjKQnHSv1axZ3DRE6DT+VJrIOJUzHrd6V5iqgO81V0+/P4+VwGU7wJK50Dz7hQXD9oxbBc7Dac0F1aHNzCo6N2UoLj1bkCgYEA8G3CcBXYzDv6wondn/Y2u3qtQVqFHoouHPC7OASaP+miP5iRvMqTbgo311T/2d8Slft/ArpkfmgmFjriXaINEjd7pSgP9r52aXCGx/EqbmbW1RUTsuN3t46zF9LwOEj5xylQdG2M93WH6Dq9XgB4L9lGshKoo3pReFCcfdvj7asCgYEAo2H2veT05zUYAy7anADp7+VX7f7mfUvVylNNv5gR5z9/XIa4F7Vdt3EHuDdRr0FZ1VQaI3gRIAswj5zwLV3YR6GHdUxLUQ2opcXqJcqtbkCIN0iDzZyDnOAki6b1vsJ1pN9V0vLoZhDkvMPObuBqjCI+7LWgRle3APM/No0bdisCgYBO/gDQCp18HIi6uckS8TXySs/5lN//tK6J253nPbvgG8au1lWrjXL6yGioWsjksCHPHR4Rq2OxFE7PJvGQidl5jrkU/iCglNsasVnW/ylNtIQI5i/eqlV+/WdiAG/kTjWidbR1TDjwZLEOX54v8D+MmPpdLzww2dNCkFmaEFDGmQKBgHxree+akDYSZLrnz2rF92DNEm0XlIcOXTy6u5aQQ/IKlP7tSPwlDb1IwkwzNG12IucmKNGh+cihPNXg+bZSpQXHqNRWyXRsJmY3ldw5wGEZm6IeAuFts3yf5LTE7JfclqJ7wWvnt7siWk61/lrJY9pbqfexSODIo4CBvT3Zr3QJAoGBALvqHnW7DfUcYNbxL4LnzyL1gbAhYhht6OQaVspKEAeYBmgaUVpJaFfMAH+p4j73Aa0uywvFkOQrbBuNS08iQSNS1Z/jVCefvmehyOdeZixhDGp9y9LE7qYPD5JzFrG+DEH+qAwhJaSuQ1U7lFL9xbXddAR095jZxIbitHpUnqOv")
                .ttl(new Duration(ChronoUnit.SECONDS, 30))
                .url(url)
                .tokenRefreshThreshold(new Duration(ChronoUnit.SECONDS, 30))
                .clientId("movieuser")
                .realm("LocalMovies")
                .build();

        this.keycloakService = new OidcService(jwtConfig, keycloakClientCamel);
    }

    @Test
    public void getAccessTokenTest() throws Exception {
        SignedJWT signedJWT = keycloakService.getAccessToken();

        System.out.println("blah");
    }
}
