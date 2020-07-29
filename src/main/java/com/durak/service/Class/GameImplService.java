package com.durak.service.Class;

import com.durak.entity.Game;
import com.durak.entity.Player;
import com.durak.repository.GameRepository;
import com.durak.service.Interface.GameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Service
public class GameImplService implements GameDAO {
    @Autowired
    GameRepository gameRepository;

    @Override
    public List<Game> getAllGames() {
        List<Game> games = new LinkedList<>();
        Iterator<Game> iterator = gameRepository.findAll().iterator();
        while (iterator.hasNext()) {
            games.add(iterator.next());
        }

        return games.size() == 0 ? null : games;
    }

    @Override
    public void save(Game game) {
        gameRepository.save(game);
    }

    @Override
    public Optional<Game> findGameById(Long id) {
        return gameRepository.findGameById(id);
    }

    @Override
    public Optional<Game> findGameByPlayer1OrPlayer2(Player player1, Player player2) {
        return gameRepository.findGameByPlayer1OrPlayer2(player1, player2);
    }

}
