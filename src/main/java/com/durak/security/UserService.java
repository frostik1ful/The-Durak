package com.durak.security;

import com.durak.entity.User;
import com.durak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService implements UserDetailsService{
    @Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = repository.findUserByName(s);
        if (user==null){
            throw new UsernameNotFoundException("User not found");
        }
        return new AppUser(user);
    }
}
