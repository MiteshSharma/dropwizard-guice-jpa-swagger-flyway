package com.myth.repository.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.myth.models.User;
import com.myth.repository.IUserRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Singleton
@Transactional
public class UserRepository extends BaseRepository<User> implements IUserRepository {
    @Inject
    public UserRepository(Provider<EntityManager> entityManager) {
        super(entityManager);
    }

    @Override
    public Optional<User> getUser(long userId) {
        return findById(User.class, userId);
    }

    @Override
    public User createUser(User user) {
        this.persist(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        return this.merge(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        Optional<User> user = this.getUser(userId);
        if (user.isPresent()) {
            this.remove(user.get());
            return true;
        }
        return false;
    }
}
