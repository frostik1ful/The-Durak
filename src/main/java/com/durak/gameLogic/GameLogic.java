package com.durak.gameLogic;

import com.durak.entity.*;
import com.durak.service.Interface.*;
import com.durak.util.CardPathCreator;
import com.durak.viewData.CardData.CardData;
import com.durak.viewData.CardPackage;
import com.durak.viewData.FieldCellData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GameLogic {
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
    @Autowired
    FieldDAO fieldDAO;
    @Autowired
    CardPathCreator cardPathCreator;

    private Player player1;
    private Player player2;
    private int currentPlayerNumber;
    private Game game;


    public GameLogic() {

    }

    public void addGameData(Game game, String curPlayerName) {
        if (game.getPlayer1().getName().equals(curPlayerName)) {
            this.player1 = game.getPlayer1();
            this.player2 = game.getPlayer2();
        } else {
            this.player1 = game.getPlayer2();
            this.player2 = game.getPlayer1();
        }
        this.game = game;
    }

    public void createGame() {
        Optional<Player> optionalPlayer = playerDAO.findPlayerByName(getUserName());
        optionalPlayer.ifPresent(player -> {

            player1 = optionalPlayer.get();
            currentPlayerNumber = 1;
            leaveFromOtherGames(player1);
            game = new Game(player1, deckDAO, fieldDAO);
            player1.setGame(game);

            gameDAO.save(game);
            playerDAO.save(player1);
        });

    }

    public boolean tryJoinToGame(Long gameId) {
        int whoseFirstTurn = 0;
        Optional<Game> optionalGame = gameDAO.findGameById(gameId);
        Optional<Player> optionalPlayer = playerDAO.findPlayerByName(getUserName());

        if (optionalGame.isPresent() && optionalPlayer.isPresent()) {
            leaveFromOtherGames(optionalPlayer.get());
            game = optionalGame.get();
            if (!game.isStarted() && !game.isFinished() && game.getPlayer2() == null) {
                player1 = game.getPlayer1();
                player2 = optionalPlayer.get();
                currentPlayerNumber = 2;
                game.setPlayer2(player2);
                giveCardsToPlayers();
                whoseFirstTurn = whoseFirstTurn();
                game.setWhichPlayerTurn(whoseFirstTurn);
                game.setWhichPlayerMove(whoseFirstTurn);
                gameDAO.save(game);
                return true;
            }

        }
        return false;
    }

    public void giveCardsToPlayers() {
        updateData();
        LinkedList<Card> deckCards = new LinkedList<>(game.getDeck().getCards());

        addCards(player1, deckCards);
        addCards(player2, deckCards);

        game.getDeck().getCards().clear();

        game.getDeck().getCards().addAll(deckCards);

        gameDAO.save(game);

    }

    private List<Card> addCards(Player player, LinkedList<Card> deckCards) {
        int cardsLeft = deckCards.size();
        int cardsToGive = 6 - player.getPlayerCards().size();
        cardsToGive = cardsLeft - cardsToGive >= 0 ? cardsToGive : cardsLeft;
        List<Card> newCards = new LinkedList<>();
        if (cardsToGive > 0) {
            for (int i = 0; i < cardsToGive; i++) {
                Card card = deckCards.pollFirst();
                newCards.add(card);
                player.getPlayerCards().add(card);
                card.setPlayer(player);
                card.setDeck(null);
            }

        }
        return newCards;
    }

    public CardPackage takeNewCards() {
        updateData();
        List<Card> deckCards = game.getDeck().getCards();
        int cardsLeft = deckCards.size();
        List<CardData> newCardsData = new LinkedList<>();
        List<Card> newCards = addCards(getCurrentPlayer(), new LinkedList<>(deckCards));

        newCards.forEach(card -> newCardsData.add(new CardData(card.getId(), cardPathCreator.getAbsoluteSingleCardPath(card))));
        gameDAO.save(game);
        return new CardPackage(cardsLeft, newCardsData);
    }

    public int whoseFirstTurn() {
        Card.Suit trump = game.getDeck().getTrump();
        Optional<Card> curPlayerCard = player1.getPlayerCards().stream().
                filter(card -> card.getSuit() == trump).
                min(Comparator.comparingInt(o -> o.getValue().getVal()));
        Optional<Card> enemyPlayerCard = player2.getPlayerCards().stream().
                filter(card -> card.getSuit() == trump).
                min(Comparator.comparingInt(o -> o.getValue().getVal()));
        if (curPlayerCard.isPresent() && enemyPlayerCard.isPresent()) {
            return curPlayerCard.get().getValue().getVal() < enemyPlayerCard.get().getValue().getVal() ? 1 : 2;
        } else if (curPlayerCard.isPresent()) {
            return 1;
        } else if (enemyPlayerCard.isPresent()) {
            return 2;
        } else {
            return new Random().nextInt(2) + 1;
        }


    }

    public boolean tryToPutCardOnTable(long cardId) {
        updateData();
        if (currentPlayerNumber == game.getWhichPlayerMove()) {
            Player currentPlayer = getCurrentPlayer();

            Optional<Card> optionalCard = currentPlayer.getPlayerCards().stream()
                    .filter(card -> card.getId() == cardId).findFirst();

            if (optionalCard.isPresent()) {
                Card card = optionalCard.get();

                Field gameField = game.getField();
                CardCell cardCell = new CardCell(card);
                cardCell.setField(gameField);
                gameField.getCardCells().add(cardCell);
                removeCardFromPlayer(currentPlayer, card);

                gameDAO.save(game);
                return true;
            }
            return false;
        }

        return false;
    }


    public boolean tryToPutCardOnCard(long bottomCardId, long upperCardId) {
        updateData();
        if (currentPlayerNumber != game.getWhichPlayerTurn() && currentPlayerNumber == game.getWhichPlayerMove()) {
            Player currentPlayer = getCurrentPlayer();

            Optional<Card> optionalCard = currentPlayer.getPlayerCards()
                    .stream().filter(card -> card.getId() == upperCardId).findFirst();


            Field gameField = game.getField();
            Optional<CardCell> optionalCardCell = gameField.getCardCells().stream()
                    .filter(cardCell -> cardCell.getBottomCard().getId() == bottomCardId)
                    .findFirst();


            if (optionalCardCell.isPresent() && optionalCard.isPresent()) {
                CardCell cardCell = optionalCardCell.get();

                Card upperCard = optionalCard.get();
                Card bottomCard = cardCell.getBottomCard();

                Card.Suit trump = game.getDeck().getTrump();

                if (upperCard.getSuit() == bottomCard.getSuit() && upperCard.getValue().getVal() > bottomCard.getValue().getVal()
                        || upperCard.getSuit() == trump && bottomCard.getSuit() != trump) {

                    cardCell.setUpperCard(upperCard);
                    removeCardFromPlayer(currentPlayer, upperCard);
                    gameDAO.save(game);

                    return true;
                }

            }

        }
        return false;
    }

    public boolean tryToTakeCardsFromField() {
        updateData();
        List<Card> upperCards = new LinkedList<>();
        List<Card> bottomCards = new LinkedList<>();

        Player player = getCurrentPlayer();
        Field field = game.getField();
        field.getCardCells().forEach(cardCell -> {
            Card upperCard = cardCell.getUpperCard();
            Card bottomCard = cardCell.getBottomCard();

            cardCell.setField(null);
            bottomCard.setPlayer(player);

            bottomCards.add(bottomCard);
            if (upperCard != null) {
                upperCard.setPlayer(player);
                upperCards.add(upperCard);
            }
        });

        player.getPlayerCards().addAll(upperCards);
        player.getPlayerCards().addAll(bottomCards);

        gameDAO.save(game);

        return true;
    }


    public String doAction() {
        updateData();

        boolean isGameFieldHasUnbeatenCards = isGameFieldHasUnbeatenCards();
        if (currentPlayerNumber == game.getWhichPlayerTurn()) {
            if (currentPlayerNumber == game.getWhichPlayerMove()) {
                if (isGameFieldHasUnbeatenCards) {
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerMove(1);
                    }
                } else {
                    //game.getField().getCardCells().clear();
                    game.getField().getCardCells().forEach(cardCell -> cardCell.setField(null));
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerTurn(2);
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerTurn(1);
                        game.setWhichPlayerMove(1);
                    }
                    addCards(getCurrentPlayer(), new LinkedList<>(game.getDeck().getCards()));
                    addCards(getEnemyPlayer(), new LinkedList<>(game.getDeck().getCards()));
                }
            }

        } else {
            if (currentPlayerNumber == game.getWhichPlayerMove()) {
                if (isGameFieldHasUnbeatenCards) {
                    tryToTakeCardsFromField();
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerTurn(2);
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerTurn(1);
                        game.setWhichPlayerMove(1);
                    }

                } else {
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerMove(1);
                    }

                }
            }
        }


        gameDAO.save(game);
        return "";
    }

    private boolean isGameFieldHasUnbeatenCards() {
        for (CardCell cell : game.getField().getCardCells()) {
            if (cell.getUpperCard() == null) {
                return true;
            }
        }

        return false;
    }

    private void removeCardFromPlayer(Player player, Card card) {
        card.setPlayer(null);
        player.setPlayerCards(player.getPlayerCards().stream()
                .filter(card1 -> card1.getId() != card.getId())
                .collect(Collectors.toList()));
    }

    private void leaveFromOtherGames(Player player) {
        Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(player, player);
        optionalGame.ifPresent(game -> {
            if (game.getPlayer1().getId() == player.getId()) {
                game.setPlayer1Leaves(true);
            } else {
                game.setPlayer2Leaves(true);
            }
            gameDAO.save(game);
        });
    }

    private void updateData() {

        try {
            Optional<Game> optionalGame = gameDAO.findGameById(game.getId());
            optionalGame.ifPresent(game -> {
                this.game = game;
                this.player1 = this.game.getPlayer1();
                this.player2 = this.game.getPlayer2();
                currentPlayerNumber = game.getPlayer1().getName().equals(getUserName()) ? 1 : 2;
            });
        } catch (NullPointerException e) {
            updateFields();
            updateData();
        }

    }

    private void updateFields() {
        Optional<Player> optionalPlayer = playerDAO.findPlayerByName(getUserName());

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            Optional<Game> optionalGame = gameDAO.findGameByPlayer1OrPlayer2(player, player);
            optionalGame.ifPresent(game -> {
                this.game = game;
                player1 = this.game.getPlayer1();
                player2 = this.game.getPlayer2();
            });
            currentPlayerNumber = game.getPlayer1().getName().equals(getUserName()) ? 1 : 2;


        }
    }

    private Player getCurrentPlayer() {
        switch (currentPlayerNumber) {
            case 1:
                return player1;
            case 2:
                return player2;

            default:
                updateFields();
                return getCurrentPlayer();
        }

    }

    private Player getEnemyPlayer() {
        switch (currentPlayerNumber) {
            case 1:
                return player2;
            case 2:
                return player1;

            default:
                updateFields();
                return getEnemyPlayer();
        }
    }

    private String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
