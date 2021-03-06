package com.durak.gameLogic;

import com.durak.entity.Card;
import com.durak.entity.Game;
import com.durak.entity.Player;
import com.durak.service.Interface.GameDAO;
import com.durak.service.Interface.PlayerDAO;
import com.durak.util.CardPathCreator;
import com.durak.viewData.CardData.*;
import com.durak.viewData.FieldCellData;
import com.durak.viewData.MainData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
@SessionScope
public class GameStatus {
    @Autowired
    private GameDAO gameDAO;
    @Autowired
    private PlayerDAO playerDAO;
    @Autowired
    private CardPathCreator cardPathCreator;

    private Player currentPlayer;
    private Player enemyPlayer;
    private Game currentGame;

    private MainData data;

    public MainData getMainData() {
        updateData();
        data = new MainData();

        //data.setCountOfEnemyHandCards(enemyPlayer.getPlayerCards().size());


        currentPlayer.getPlayerCards().forEach(card -> data.getMyHandCards().add(new CardData(card.getId(), cardPathCreator.getAbsoluteSingleCardPath(card),card.getIsTrump())));

        if (enemyPlayer != null) {
            enemyPlayer.getPlayerCards().forEach(card -> data.getEnemyHandCards().add(new EnemyHandCardData(card.getId())));

            data.setEnemyTakesPreLastCard(enemyPlayer.getTakesPreLastCard());
            data.setEnemyTakesLastCard(enemyPlayer.getTakesLastCard());

            data.setIsIWon(isIWon());
            data.setIsILose(isILose());
            data.setEnemyName(enemyPlayer.getName());
        }
        else {
            data.setEnemyName("Waiting for player");
        }


        data.setTakesPreLastCard(currentPlayer.getTakesPreLastCard());
        data.setTakesLastCard(currentPlayer.getTakesLastCard());

        data.setFieldCells(getFieldCells());
        data.setActionButtonText(getActionButtonText());
        data.setMyTurn(isMyTurn());
        data.setMyMove(isMyMove());


        return data;
    }

    private boolean isILose() {
        if (currentGame.getField().getCardCells().size() == 0 && currentGame.getDeck().getCards().size() == 0 && enemyPlayer.getPlayerCards().size() == 0) {
            if (currentGame.getPlayer1().equals(currentPlayer)) {
                currentGame.setPlayer2Wins(true);
            } else {
                currentGame.setPlayer1Wins(true);
            }
            gameDAO.save(currentGame);
            return true;
        } else {
            return false;
        }
    }

    private boolean isIWon() {
//        if (currentGame.getPlayer1Leaves()) {
//            System.out.println("player1 leaves");
//            if (currentGame.getPlayer1().equals(enemyPlayer)) {
//                System.out.println("enemy Leaves");
//            }
//        }
//        if (currentGame.getPlayer2Leaves()) {
//            System.out.println("player2 leaves");
//            if (currentGame.getPlayer1().equals(enemyPlayer)) {
//                System.out.println("enemy Leaves");
//            }
//        }
        if (currentGame.getField().getCardCells().size() == 0 && currentGame.getDeck().getCards().size() == 0 && currentPlayer.getPlayerCards().size() == 0
                || currentGame.getPlayer1().equals(enemyPlayer) && currentGame.getPlayer1Leaves()
                || currentGame.getPlayer2().equals(enemyPlayer) && currentGame.getPlayer2Leaves()) {


            if (currentGame.getPlayer1().equals(currentPlayer)) {
                currentGame.setPlayer1Wins(true);
            } else {
                currentGame.setPlayer2Wins(true);
            }
            currentGame.setFinished(true);
            gameDAO.save(currentGame);
            return true;
        } else {
            return false;
        }
    }

    public String getEnemyName() {
        try {
            return getEnemyPlayer().getName();
        } catch (NullPointerException e) {
            return "Waiting for player";
        }

//        Optional<Player> optionalCurrentPlayer = getCurrentPlayer();
//
//        if (optionalCurrentPlayer.isPresent()) {
//            Player currentPlayer = optionalCurrentPlayer.get();
//            Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(currentPlayer, currentPlayer);
//
//            if (optionalGame.isPresent()) {
//                Game game = optionalGame.get();
//                if (game.getPlayer1().getName().equals(currentPlayer.getName())) {
//                    return game.getPlayer2() == null ? "Waiting for player" : game.getPlayer2().getName();
//                } else {
//                    return game.getPlayer1().getName();
//                }
//            }
//
//        }
//        return null;
    }


    public List<FieldCellData> getFieldCells() {
        List<FieldCellData> fieldCells = new LinkedList<>();
        currentGame.getField().getCardCells().forEach(cell -> {
            Card bottomCard = cell.getBottomCard();
            Card upperCard = cell.getUpperCard();

            CardData bottomCardData;
            CardData upperCardData;
            if (upperCard != null) {
                bottomCardData = new FieldCardData(bottomCard.getId(), cardPathCreator.getSinglePath(bottomCard),bottomCard.getIsTrump());
                upperCardData = new UpperCardData(upperCard.getId(), cardPathCreator.getSinglePath(upperCard),upperCard.getIsTrump());
            } else {
                bottomCardData = new BottomCardData(bottomCard.getId(), cardPathCreator.getSinglePath(bottomCard),bottomCard.getIsTrump());
                upperCardData = null;

            }
            fieldCells.add(new FieldCellData(bottomCardData, upperCardData));
        });
        return fieldCells;

    }

    public CardData getLastCardData() {
        List<Card> cards = currentGame.getDeck().getCards();
        return cards.size() > 0 ? cardPathCreator.getSingleCardData(cards.get(cards.size() - 1)) : null;
    }

    public int getCardsLeft() {
        return currentGame.getDeck().getCards().size();
    }


    public List<CardData> getCurrentPlayerCards() {
        Optional<Player> optionalPlayer = getCurrentPlayer();
        return optionalPlayer.map(player -> cardPathCreator.getCardsData(player.getPlayerCards())).orElse(null);
    }

    public List<CardData> getEnemyPlayerCards() {
        return enemyPlayer == null ? null : cardPathCreator.getCardsData(enemyPlayer.getPlayerCards());
        //return cardPathCreator.getCardsData(enemyPlayer.getPlayerCards());
    }

    private Optional<Player> getCurrentPlayer() {
        return playerDAO.findPlayerByName(getUserName());
    }

    private String getActionButtonText() {
        if (isMyTurn()) {
            if (isMyMove()) {
                if (isGameFieldHasUnbeatenCards()) {
                    return "Finish move";
                } else {
                    return "Finish turn";
                }

            } else {
                return "Waiting for enemy";
            }
        } else {
            if (isMyMove()) {
                if (isGameFieldHasUnbeatenCards()) {
                    return "Take cards";
                } else {
                    return "Finish move";
                }
            } else {
                return "Waiting for enemy";
            }
        }

    }

    private Player getEnemyPlayer() {
        updateData();
        return enemyPlayer;
//        Optional<Player> optionalCurrentPlayer = getCurrentPlayer();
//        if (optionalCurrentPlayer.isPresent()) {
//            Player currentPlayer = optionalCurrentPlayer.get();
//            Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(currentPlayer, currentPlayer);
//            if (optionalGame.isPresent()) {
//                Game game = optionalGame.get();
//                if (game.getPlayer1().equals(currentPlayer) && game.getPlayer2() != null) {
//                    return game.getPlayer2();
//                } else if (game.getPlayer1() != null) {
//                    return game.getPlayer1();
//                }
//            }
//        }
//        return null;

    }

    private Optional<Game> getCurrentGame(long gameId) {
        return gameDAO.findGameById(gameId);
        //return gameDAO.findGameByPlayerName(getUserName());
    }

    public Optional<Game> getNotFinishedGame() {
        Optional<Game> optionalGame = gameDAO.findNotFinishedGameByPlayerName(getUserName());
        optionalGame.ifPresent(game1 -> currentGame = game1);
        return optionalGame;
    }

    public void updateData() {
        enemyPlayer = null;
        Optional<Game> optionalGame;
        try {
            optionalGame = getCurrentGame(currentGame.getId());
        } catch (NullPointerException e) {
            optionalGame = getNotFinishedGame();
        }

        if (optionalGame.isPresent()) {
            currentGame = optionalGame.get();
            if (currentGame.getPlayer1().equals(currentPlayer) || currentGame.getPlayer1().getName().equals(getUserName())) {
                currentPlayer = currentGame.getPlayer1();
                if (currentGame.getPlayer2() != null) {
                    enemyPlayer = currentGame.getPlayer2();
                }
            } else {
                currentPlayer = currentGame.getPlayer2();
                enemyPlayer = currentGame.getPlayer1();
            }
        }
//        else {
//            System.out.println("game not founded");
//        }
    }

    private boolean isGameFieldHasUnbeatenCards() {
        Optional<FieldCellData> optionalCell = data.getFieldCells().
                stream().filter(cell -> cell.getUpperCard() == null).findFirst();
        return optionalCell.isPresent();
    }

    private boolean isMyTurn() {
        if (currentGame.getPlayer1().equals(currentPlayer) && currentGame.getWhichPlayerTurn() == 1) {
            return true;
        } else if (currentGame.getPlayer2() != null && currentGame.getPlayer2().equals(currentPlayer) && currentGame.getWhichPlayerTurn() == 2) {
            return true;
        }

        return false;
    }

    private boolean isMyMove() {
        if (currentGame.getPlayer1().equals(currentPlayer) && currentGame.getWhichPlayerMove() == 1) {
            return true;
        } else if (currentGame.getPlayer2() != null && currentGame.getPlayer2().equals(currentPlayer) && currentGame.getWhichPlayerMove() == 2) {
            return true;
        }

        return false;
    }

    private String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isCardsReceived() {
        if (!currentPlayer.isCardsReceived()){
            currentPlayer.setCardsReceived(true);
            playerDAO.save(currentPlayer);
            return false;
        }
        return true;

    }

}
