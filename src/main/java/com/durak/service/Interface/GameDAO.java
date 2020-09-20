package com.durak.service.Interface;

import com.durak.entity.Game;
import com.durak.entity.Player;

import java.util.List;
import java.util.Optional;


public interface GameDAO {
    List<Game> getAllGames();
    List<Game> getNotStartedGames();
    void save(Game game);
    Optional<Game> findGameById(Long id);
    Optional<Game> findGameByPlayer1OrPlayer12(Player player1, Player player2);
    Optional<Game> findNotFinishedGameByPlayerName(String playerName);
    Optional<Game> findGameByPlayerNameToLeave(String playerName);
    List<Optional<Game>> findTestGame(String name);
}
