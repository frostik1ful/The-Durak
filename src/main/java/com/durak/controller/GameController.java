package com.durak.controller;

import com.durak.entity.*;

import com.durak.gameLogic.GameLogic;
import com.durak.gameLogic.GameStatus;
import com.durak.service.Interface.*;
import com.durak.util.CardPathCreator;
import com.durak.viewData.MainData;
import com.durak.viewData.CardPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
@RequestMapping("/game")
public class GameController {
    @Autowired
    GameDAO gameDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    DeckDAO deckDAO;
    @Autowired
    PlayerDAO playerDAO;
    @Autowired
    CardDAO cardDAO;
//    @Autowired
//    CardPathCreator cardPathCreator;
    @Autowired
    GameLogic gameLogic;
    @Autowired
    GameStatus gameStatus;

    @GetMapping("/lobby")
    public String lobby(Model model){
        model.addAttribute("games",gameDAO.getNotStartedGames());
        model.addAttribute("userName", getCurrentUser().getName());
        return "lobby";
    }

    @GetMapping("/createGame")
    public String createGame(){
        gameLogic.createGame();
        return"redirect:/game/lobby";
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public String join(@RequestParam Long id){

        if (gameLogic.tryJoinToGame(id)) {
            return "redirect:/game/game";
        }
        else {
            return "redirect:/game/lobby";
        }

    }
    @RequestMapping(value = "/update")
    @ResponseBody
    public MainData update(){
        return gameStatus.getMainData();
    }

    @RequestMapping(value = "/action")
    @ResponseBody
    public String action(){
        return gameLogic.doAction();

    }

    @RequestMapping(value = "/game")
    public String game(Model model){
        gameStatus.updateData();
        Optional<Game> game = gameStatus.getNotFinishedGame();

        if (game.isPresent()){
            model.addAttribute("cardsReceived",gameStatus.isCardsReceived());
            model.addAttribute("fieldCells",gameStatus.getFieldCells());
            model.addAttribute("enemyName",gameStatus.getEnemyName());
            model.addAttribute("enemyCards",gameStatus.getEnemyPlayerCards());
            model.addAttribute("cardsLeft",gameStatus.getCardsLeft());
//                if (deckCards.size()>15){
//                    model.addAttribute("deckSize","background-image:url('/images/cards/bigDeck.png');");
//                }
//                else if (deckCards.size()>2){
//                    model.addAttribute("deckSize","background-image:url('/images/cards/lowDeck.png');");
//                }
            model.addAttribute("lastCardData",gameStatus.getLastCardData());
            model.addAttribute("myCards",gameStatus.getCurrentPlayerCards());
        }
        else {
            return "redirect:/game/lobby";
        }

        return "game";
    }


    @RequestMapping(value = "/putCardOnTable")
    @ResponseBody
    public boolean putCardOnTable(@RequestParam long cardId){
        System.out.println("PutCardOnTable");
        return gameLogic.tryToPutCardOnTable(cardId);
    }

    @RequestMapping(value = "/putCardOnCard")
    @ResponseBody
    public boolean putCardOnCard(@RequestParam long cardToId,@RequestParam long cardId){
        System.out.println("puttingCard on card");
        return gameLogic.tryToPutCardOnCard(cardToId,cardId);
    }

    @RequestMapping(value = "/takeNewCards")
    @ResponseBody
    public CardPackage takeNewCards(){
        return gameLogic.takeNewCards();
    }


    private User getCurrentUser(){
        return userDAO.findUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    private String getUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
