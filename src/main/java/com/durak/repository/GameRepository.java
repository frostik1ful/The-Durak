package com.durak.repository;

import com.durak.entity.Game;
import com.durak.entity.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends CrudRepository<Game,Long> {
    Optional<Game> findGameById(Long id);
    Optional<Game> findGameByPlayer1OrPlayer2(Player player1, Player player2);
    List<Game> findGamesByIsStarted(boolean isStarted);

    @Query("SELECT g FROM Game g WHERE  (g.player1.id = (select g.player1.id FROM Game g WHERE g.player1.name = ?1 AND g.isFinished = false) AND g.isFinished = false)" +
            " OR (g.player2.id = (select g.player2.id FROM Game g WHERE g.player2.name = ?1 AND g.isFinished = false) AND g.isFinished = false)")
    Optional<Game> findByPlayerName(@Param("playerName")String playerName);

    @Query("SELECT g FROM Game g WHERE (g.player1.name = ?1 AND g.player1Leaves = false) OR (g.player2.name = ?1 AND g.player2Leaves = false)")
    Optional<Game> findGameByPlayerNameToLeave(String playerName);


    @Query("SELECT g FROM Game g WHERE  (g.player1.id = (select g.player1.id FROM Game g WHERE g.player1.name = ?1 AND g.isFinished = false) )" +
            " OR (g.player2.id = (select g.player2.id FROM Game g WHERE g.player2.name = ?1 AND g.isFinished = false))")
    List<Optional<Game>> findTestGame(String name);
}
