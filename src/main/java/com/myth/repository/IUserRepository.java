package com.myth.repository;

import com.google.inject.ImplementedBy;
import com.myth.models.User;
import com.myth.repository.cache.UserCacheRepository;
import com.myth.repository.impl.UserRepository;

import java.util.Optional;

@ImplementedBy(UserCacheRepository.class)
public interface IUserRepository {
    Optional<User> getUser(long userId);
    User createUser(User user);
    User updateUser(User user);
    boolean deleteUser(long userId);
}
