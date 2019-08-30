package com.github.rahmnathan.oauth2.adapter.domain;

import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2Credential;
import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;

public interface RequestBodyProvider {
    String buildRequestBody(OAuth2Credential config) throws OAuth2AdapterException;
}
