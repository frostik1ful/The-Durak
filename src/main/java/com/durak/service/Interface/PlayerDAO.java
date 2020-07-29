package com.durak.service.Interface;

import com.durak.entity.Player;

import java.util.Optional;

public interface PlayerDAO {
    void save(Player player);
    Optional<Player> findPlayerByName(String name);
    Optional<Player> findPlayerById(Long id);
}
