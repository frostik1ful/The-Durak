package com.durak.service.Class;

import com.durak.entity.Player;
import com.durak.repository.PlayerRepository;
import com.durak.service.Interface.PlayerDAO;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerImplService implements PlayerDAO {
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void save(Player player) {
        playerRepository.save(player);
    }

    @Override
    public Optional<Player> findPlayerByName(String name) {
        return playerRepository.findPlayerByName(name);

        //        if (player.isPresent()) {
//            return player.get();
//        } else {
//            throw new NullPointerException("Player Not Founded in dataBase");
//        }

    }

    @Override
    public Optional<Player> findPlayerById(Long id) {
        return playerRepository.findById(id);
    }
}
