package com.durak.service.Class;

import com.durak.service.Interface.UserDAO;
import com.durak.entity.User;
import com.durak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserImplService implements UserDAO {
    private UserRepository userRepository;

    @Autowired
    public UserImplService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserByName(String name) {
        return userRepository.findUserByName(name);
    }

    @Override
    public void addNewUser(User user) {
        userRepository.save(user);
    }
}
