package com.myth.auth;

import com.google.inject.Singleton;
import com.myth.models.User;
import io.dropwizard.auth.Authorizer;

@Singleton
public class UserAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user != null;
    }
}
