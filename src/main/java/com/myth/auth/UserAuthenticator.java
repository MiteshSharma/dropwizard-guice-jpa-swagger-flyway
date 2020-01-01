package com.myth.auth;

import com.google.inject.Inject;
import com.myth.models.User;
import com.myth.service.ITokenService;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Optional;

public class UserAuthenticator implements Authenticator<JwtContext, User> {
    private ITokenService tokenService;

    @Inject
    public UserAuthenticator(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Optional<User> authenticate(JwtContext context) {
       return this.tokenService.authenticate(context);
    }
}
