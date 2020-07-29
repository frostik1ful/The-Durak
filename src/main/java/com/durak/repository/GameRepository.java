package com.durak.repository;

import com.durak.entity.Game;
import com.durak.entity.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game,Long> {
    Optional<Game> findGameById(Long id);
    Optional<Game> findGameByPlayer1OrPlayer2(Player player1, Player player2);

}
