package com.car_dealer_web.restful_api.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;

public final class RSAKeyGenerator {
  private RSAKeyGenerator() {
  }

  public static PrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
    ClassPathResource resource = new ClassPathResource(privateKeyPath);
    String keyContent = new String(resource.getInputStream().readAllBytes());
    String pemFormattedKey = keyContent
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");

    byte[] decodedKey = Base64.getDecoder().decode(pemFormattedKey);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(keySpec);
  }

  public static PublicKey loadPublicKey(String publicKeyPath) throws Exception {
    ClassPathResource resource = new ClassPathResource(publicKeyPath);
    String keyContent = new String(resource.getInputStream().readAllBytes());
    String pemFormattedKey = keyContent
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");

    byte[] decodedKey = Base64.getDecoder().decode(pemFormattedKey);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }
}
