package com.car_dealer_web.restful_api.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtAuthHandler {
  @Value("${jwt.private.key}")
  private RSAPublicKey publicKey;

  @Value("${jwt.public.key}")
  private RSAPrivateKey privateKey;

  @Value("${jwt.expiration}")
  private int jwtExpirationMs;

  @Value("${jwt.refresh.token.expiration}")
  private int refreshTokenExpirationMs;

  private static final Logger LOG = LoggerFactory.getLogger(JwtAuthHandler.class);

  private String createJwt(User user) throws InvalidKeyException, UnknownHostException {
    List<String> userRoles = user.getRoles()
        .stream()
        .map(role -> role.getName())
        .collect(Collectors.toList());

    byte[] bytes = new byte[16];
    SecureRandom random = new SecureRandom(bytes);
    var jwtId = Integer.toHexString(random.nextInt());

    ZoneId zoneId = ZoneId.of("Asia/Jakarta");
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));

    var exp = Date.from(now.plus(jwtExpirationMs, ChronoUnit.MILLIS).atZone(zoneId).toInstant());
    var iss = Date.from(now.atZone(zoneId).toInstant());
    var nbf = Date.from(now.atZone(zoneId).toInstant());
    var hostName = InetAddress.getLocalHost().getHostName();

    var customClaims = new HashMap<String, Object>();
    customClaims.put("name", user.getFullname());
    customClaims.put("roles", userRoles);

    return Jwts.builder()
        .claims(customClaims)
        .issuer(hostName)
        .subject(user.getId())
        .expiration(exp)
        .notBefore(nbf)
        .issuedAt(iss)
        .id(jwtId)
        .signWith(privateKey)
        .compact();
  }

  private String createRefreshToken(User user) throws InvalidKeyException, UnknownHostException {
    byte[] bytes = new byte[16];
    SecureRandom random = new SecureRandom(bytes);
    var refreshTokenId = Integer.toHexString(random.nextInt());

    ZoneId zoneId = ZoneId.of("Asia/Jakarta");
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));

    var exp = Date.from(now.plus(refreshTokenExpirationMs, ChronoUnit.MILLIS).atZone(zoneId).toInstant());
    var iss = Date.from(now.atZone(zoneId).toInstant());
    var nbf = Date.from(now.atZone(zoneId).toInstant());
    var hostName = InetAddress.getLocalHost().getHostName();

    return Jwts.builder()
        .issuer(hostName)
        .subject(user.getEmail())
        .expiration(exp)
        .notBefore(nbf)
        .issuedAt(iss)
        .id(refreshTokenId)
        .signWith(privateKey)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = decodeToken(token);
    return claimsResolver.apply(claims);
  }

  public Claims decodeToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(publicKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (JwtException e) {
      LOG.error(e.getMessage(), e);

      throw new RuntimeException("Invalid or expired token.", e);
    }
  }

  public String generateToken(User user) {
    try {
      return createJwt(user);
    } catch (InvalidKeyException | UnknownHostException e) {
      LOG.error(e.getMessage(), e);

      throw new RuntimeException(e.getMessage());
    }
  }

  public String generateRefreshToken(User user) {
    try {
      return createRefreshToken(user);
    } catch (InvalidKeyException | UnknownHostException e) {
      LOG.error(e.getMessage(), e);

      throw new RuntimeException(e.getMessage());
    }
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String email = extractUsername(token);
    return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
