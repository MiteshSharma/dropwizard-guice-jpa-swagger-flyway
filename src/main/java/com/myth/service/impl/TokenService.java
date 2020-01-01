package com.myth.service.impl;

import com.google.inject.Inject;
import com.myth.MobileServerConfiguration;
import com.myth.models.User;
import com.myth.service.ITokenService;
import com.myth.service.IUserService;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import javax.ws.rs.BadRequestException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class TokenService implements ITokenService {

    private MobileServerConfiguration configuration;
    private IUserService userService;

    @Inject
    public TokenService(MobileServerConfiguration configuration, IUserService userService) {
        this.configuration = configuration;
        this.userService = userService;
    }

    @Override
    public String createToken(String subject, Map<String, String> claimMap) {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(subject);
        claims.setExpirationTimeMinutesInTheFuture(600);
        for (Map.Entry<String, String> entry : claimMap.entrySet()) {
            claims.setClaim(entry.getKey(), entry.getValue());
        }

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(new HmacKey(configuration.getJwtTokenSecret()));

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            String exceptionMessage = "Exception during JWT token generation";
            throw new RuntimeException(exceptionMessage, e);
        }
    }

    public Optional<User> authenticate(JwtContext context) {
        Optional<User> optionalUser = Optional.empty();
        try {
            NumericDate expirationTime = context.getJwtClaims().getExpirationTime();
            if (expirationTime.isBefore(NumericDate.now())) {
                throw new InvalidJwtException("expired at " + expirationTime,  Collections.emptyList(), context); // or whatever
            }
            final String subject = context.getJwtClaims().getSubject();
            User user = this.userService.getUser(1);
            optionalUser = Optional.of(user);
        } catch (MalformedClaimException e) {
            throw new BadRequestException("Claim is malformed", e);
        } catch (InvalidJwtException e) {
            throw new BadRequestException("Invalid token", e);
        }
        return optionalUser;
    }
}
