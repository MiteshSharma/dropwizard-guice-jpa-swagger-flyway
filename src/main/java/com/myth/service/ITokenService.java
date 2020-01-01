package com.myth.service;

import com.google.inject.ImplementedBy;
import com.myth.models.User;
import com.myth.service.impl.TokenService;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Map;
import java.util.Optional;

@ImplementedBy(TokenService.class)
public interface ITokenService {
    String createToken(String subject, Map<String, String> claimMap);
    Optional<User> authenticate(JwtContext context);
}
