package com.durak.security;

import com.durak.entity.User;
import com.durak.service.Interface.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("SecurityUserService")
public class SecurityUserService implements UserDetailsService{
    private final UserDAO userDAO;
    @Autowired
    public SecurityUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDAO.findUserByName(s);
        if (user==null){
            throw new UsernameNotFoundException("User not found");
        }
        return new AppUser(user);
    }
}
