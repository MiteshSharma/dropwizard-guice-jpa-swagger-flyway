package com.myth.service;

import com.google.inject.ImplementedBy;
import com.myth.models.User;
import com.myth.service.impl.UserService;

@ImplementedBy(UserService.class)
public interface IUserService {
    User getUser(long userId);
    User createUser(User user);
    User updateUser(User user);
    boolean deleteUser(long userId);
}
