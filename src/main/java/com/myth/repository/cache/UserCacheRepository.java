package com.myth.repository.cache;

import com.google.inject.Provider;
import com.myth.db.IRedisClient;
import com.myth.models.User;
import com.myth.repository.impl.UserRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Optional;

public class UserCacheRepository extends UserRepository {

    private IRedisClient redisClient;
    private static final String USER_KEY_PREFIX = "USER::";
    private static final int USER_REDIS_ONE_DAY_EXPIRY = 86400;

    @Inject
    public UserCacheRepository(Provider<EntityManager> entityManager, IRedisClient redisClient) {
        super(entityManager);
        this.redisClient = redisClient;
    }

    @Override
    public Optional<User> getUser(long userId) {
        Optional<User> optionalUser = null;
        User user = this.redisClient.get(this.getUserKey(userId), User.class);
        if (user == null) {
            optionalUser = super.getUser(userId);
            user = optionalUser.get();
            if (user != null) {
                this.redisClient.set(this.getUserKey(userId), user, USER_REDIS_ONE_DAY_EXPIRY);
            }
        } else {
            optionalUser = Optional.of(user);
        }
        return optionalUser;
    }

    private String getUserKey(long userId) {
        return USER_KEY_PREFIX + userId;
    }
}
