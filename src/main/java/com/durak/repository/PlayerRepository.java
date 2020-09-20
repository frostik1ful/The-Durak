package com.durak.repository;

import com.durak.entity.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player,Long> {
    Optional<Player> findPlayerByName(String name);
}
