package com.siriusxi.cloud.infra.auth.server.api;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * Legacy Authorization Server (spring-security-oauth2) does not support any <a href target="_blank"
 * href="https://tools.ietf.org/html/rfc7517#section-5">JWK Set</a> endpoint.
 *
 * <p>This class adds ad-hoc support in order to better support the other samples in the repo.
 */
@FrameworkEndpoint
class JwkSetEndpoint {
  KeyPair keyPair;

  public JwkSetEndpoint(KeyPair keyPair) {
    this.keyPair = keyPair;
  }

  @GetMapping("/.well-known/jwks.json")
  @ResponseBody
  public Map<String, Object> getKey() {
    RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
    RSAKey key = new RSAKey.Builder(publicKey).build();
    return new JWKSet(key).toJSONObject();
  }
}
