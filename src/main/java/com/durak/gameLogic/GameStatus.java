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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
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

        data.setCountOfEnemyHandCards(enemyPlayer.getPlayerCards().size());
        enemyPlayer.getPlayerCards().forEach(card -> data.getEnemyHandCards().add(new EnemyHandCardData(card.getId())));
        currentPlayer.getPlayerCards().forEach(card -> data.getMyHandCards().add(new CardData(card.getId(), cardPathCreator.getAbsoluteSingleCardPath(card))));
        data.setCardsLeftInDeck(currentGame.getDeck().getCards().size());
        data.setFieldCells(getFieldCells());
        data.setActionButtonText(getActionButtonText());
        data.setMyTurn(isMyTurn());
        data.setMyMove(isMyMove());

        return data;
    }

    public String getEnemyName() {
        Optional<Player> optionalCurrentPlayer = getCurrentPlayer();

        if (optionalCurrentPlayer.isPresent()) {
            Player currentPlayer = optionalCurrentPlayer.get();
            Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(currentPlayer, currentPlayer);

            if (optionalGame.isPresent()) {
                Game game = optionalGame.get();
                if (game.getPlayer1().getName().equals(currentPlayer.getName())) {
                    return game.getPlayer2() == null ? "Waiting for player" : game.getPlayer2().getName();
                } else {
                    return game.getPlayer1().getName();
                }
            }

        }
        return null;
    }


    public List<FieldCellData> getFieldCells() {
        List<FieldCellData> fieldCells = new LinkedList<>();
        currentGame.getField().getCardCells().forEach(cell -> {
            Card bottomCard = cell.getBottomCard();
            Card upperCard = cell.getUpperCard();

            CardData bottomCardData;
            CardData upperCardData;
            if (upperCard != null) {
                bottomCardData = new FieldCardData(bottomCard.getId(), cardPathCreator.getSinglePath(bottomCard));
                upperCardData = new UpperCardData(upperCard.getId(), cardPathCreator.getSinglePath(upperCard));
            } else {
                bottomCardData = new BottomCardData(bottomCard.getId(), cardPathCreator.getSinglePath(bottomCard));
                upperCardData = null;
                //upperCardData = new EmptyCardData();
            }
            fieldCells.add(new FieldCellData(bottomCardData, upperCardData));
        });
        return fieldCells;

    }

    public CardData getLastCardData() {
        Optional<Game> optionalGame = getCurrentGame();
        if (optionalGame.isPresent()) {
            List<Card> cards = optionalGame.get().getDeck().getCards();
            return cards.size() > 0 ? cardPathCreator.getSingleCardData(cards.get(cards.size() - 1)) : null;
        }
        return null;
    }

    public int getCardsLeft() {
        Optional<Game> optionalGame = getCurrentGame();
        return optionalGame.map(game -> game.getDeck().getCards().size()).orElse(0);
    }


    public List<CardData> getCurrentPlayerCards() {
        Optional<Player> optionalPlayer = getCurrentPlayer();
        return optionalPlayer.map(player -> cardPathCreator.getCardsData(player.getPlayerCards())).orElse(null);
    }

    public List<CardData> getEnemyPlayerCards() {
        Player enemyPlayer = getEnemyPlayer();
        return cardPathCreator.getCardsData(enemyPlayer.getPlayerCards());
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
        Optional<Player> optionalCurrentPlayer = getCurrentPlayer();
        if (optionalCurrentPlayer.isPresent()) {
            Player currentPlayer = optionalCurrentPlayer.get();
            Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(currentPlayer, currentPlayer);
            if (optionalGame.isPresent()) {
                Game game = optionalGame.get();
                if (game.getPlayer1().equals(currentPlayer) && game.getPlayer2() != null) {
                    return game.getPlayer2();
                } else if (game.getPlayer1() != null) {
                    return game.getPlayer1();
                }
            }
        }
        return null;

    }

    public Optional<Game> getCurrentGame() {
        Optional<Player> optionalPlayer = getCurrentPlayer();
        return optionalPlayer.map(player -> gameDAO.findGameByPlayer1OrPlayer2(player, player)).orElse(null);
    }

    public void updateData() {
        Optional<Player> optionalCurrentPlayer = playerDAO.findPlayerByName(getUserName());
        if (optionalCurrentPlayer.isPresent()) {
            currentPlayer = optionalCurrentPlayer.get();
            Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(currentPlayer, currentPlayer);
            if (optionalGame.isPresent()) {
                currentGame = optionalGame.get();
                if (currentGame.getPlayer1().equals(currentPlayer)) {
                    currentPlayer = currentGame.getPlayer1();
                    if (currentGame.getPlayer2() != null) {
                        enemyPlayer = currentGame.getPlayer2();
                    }
                } else {
                    currentPlayer = currentGame.getPlayer2();
                    enemyPlayer = currentGame.getPlayer1();
                }
            }
        }

    }

    private boolean isGameFieldHasUnbeatenCards() {
        for (FieldCellData cell : data.getFieldCells()) {
            if (cell.getUpperCard() == null) {
                return true;
            }
        }

        return false;
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

}
