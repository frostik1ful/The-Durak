package com.durak.repository;

import com.durak.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long>{

    User findUserByName(String name);
}
