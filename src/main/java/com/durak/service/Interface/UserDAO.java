package com.durak.service.Interface;

import com.durak.entity.User;

public interface UserDAO {
    User findUserByName(String name);
    void addNewUser(User user);
}
