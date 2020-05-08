package com.siriusxi.cloud.infra.auth.server.api;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Legacy Authorization Server (spring-security-oauth2) does not support any Token Introspection
 * endpoint.
 *
 * <p>This class adds ad-hoc support in order to better support the other samples in the repo.
 */
@FrameworkEndpoint
class IntrospectEndpoint {
  TokenStore tokenStore;

  public IntrospectEndpoint(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  @PostMapping("/introspect")
  @ResponseBody
  public Map<String, Object> introspect(@RequestParam("token") String token) {
    OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(token);
    Map<String, Object> attributes = new HashMap<>();
    if (accessToken == null || accessToken.isExpired()) {
      attributes.put("active", false);
      return attributes;
    }

    OAuth2Authentication authentication = this.tokenStore.readAuthentication(token);

    attributes.put("active", true);
    attributes.put("exp", accessToken.getExpiration().getTime());
    attributes.put("scope", String.join(" ", accessToken.getScope()));
    attributes.put("sub", authentication.getName());

    return attributes;
  }
}
