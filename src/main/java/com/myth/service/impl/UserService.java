package com.myth.service.impl;

import com.google.inject.Inject;
import com.myth.models.User;
import com.myth.repository.IUserRepository;
import com.myth.service.IUserService;

import java.util.Optional;

public class UserService implements IUserService {

    private IUserRepository userRepository;

    @Inject
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(long userId) {
        Optional<User> optionalUser = this.userRepository.getUser(userId);
        return optionalUser.isPresent() ? optionalUser.get() : null;
    }

    @Override
    public User createUser(User user) {
        return this.userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return this.userRepository.updateUser(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        return this.userRepository.deleteUser(userId);
    }
}
