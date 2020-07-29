package com.durak.util;

import com.durak.service.Interface.UserDAO;
import com.durak.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {
    @Autowired
    private UserDAO userDAO;
    private List<Error> errors = new ArrayList<>(4);

    public void validate(User user, String confirmPassword) {
        errors.clear();
        if (userDAO.findUserByName(user.getName()) != null) {
            errors.add(new Error("User with this name already exists"));
        }
        if (user.getName().length() < 3 || user.getName().length() > 20) {
            errors.add(new Error("name should be between 3 and 20"));
        }
        if (user.getPassword().length() < 6 || user.getPassword().length() > 20) {
            errors.add(new Error("password should be between 6 and 20"));
        }
        if (!user.getPassword().equals(confirmPassword)) {
            errors.add(new Error("passwords not match"));
        }
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public List<Error> getErrors() {
        return errors;
    }

    private class Error {
        private String errorMessage;

        private Error(String message) {
            this.errorMessage = message;
        }

        @Override
        public String toString() {
            return errorMessage;
        }
    }
}
