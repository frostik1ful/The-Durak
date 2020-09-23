package com.durak.gameLogic;

import com.durak.entity.*;
import com.durak.service.Interface.*;
import com.durak.util.CardPathCreator;
import com.durak.viewData.CardData.CardData;
import com.durak.viewData.CardPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;
import java.util.stream.Collectors;

@Component
@SessionScope
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
            currentPlayerNumber = 1;
            leaveFromOtherGames(player);
            game = new Game(player, deckDAO, fieldDAO);
            player.setGame(game);
            gameDAO.save(game);
            playerDAO.save(player);
        });

    }

    public boolean tryJoinToGame(Long gameId) {
        int whoseFirstTurn = 0;
        Optional<Game> optionalGame = gameDAO.findGameById(gameId);
        Optional<Player> optionalPlayer = playerDAO.findPlayerByName(getUserName());

        if (optionalGame.isPresent() && optionalPlayer.isPresent()) {

            game = optionalGame.get();
            if (!game.isStarted() && game.getPlayer2() == null) {
                player1 = game.getPlayer1();
                player2 = optionalPlayer.get();

                leaveFromOtherGames(player2);

                currentPlayerNumber = 2;

                giveCardsToPlayers(player1,player2);
                game.setPlayer2(player2);


                whoseFirstTurn = whoseFirstTurn();
                game.setWhichPlayerTurn(whoseFirstTurn);
                game.setWhichPlayerMove(whoseFirstTurn);
                gameDAO.save(game);
                return true;
            }

        }
        return false;
    }

    private void giveCardsToPlayers(Player player1,Player player2) {
        addCards(player2);
        addCards(player1);
    }

    private List<Card> addCards(Player player) {
        LinkedList<Card> deckCards = new LinkedList<>(game.getDeck().getCards());
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
            game.getDeck().getCards().clear();
            game.getDeck().getCards().addAll(deckCards);
            //game.getDeck().setCards(deckCards);


            if (deckCards.size() == 1) {
                player.setTakesPreLastCard(true);
            }
            if (deckCards.size() == 0){
                if (cardsToGive > 1) {
                    player.setTakesPreLastCard(true);
                }
                player.setTakesLastCard(true);
            }
//
        }
        return newCards;
    }

    public CardPackage takeNewCards() {
        updateData();
        List<Card> deckCards = game.getDeck().getCards();
        int cardsLeft = deckCards.size();
        List<CardData> newCardsData = new LinkedList<>();
        List<Card> newCards = addCards(getCurrentPlayer());
        newCards.forEach(card -> newCardsData.add(new CardData(card.getId(), cardPathCreator.getAbsoluteSingleCardPath(card),card.getIsTrump())));
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
        if (currentPlayerNumber == game.getWhichPlayerMove() && currentPlayerNumber == game.getWhichPlayerTurn()) {
            Player currentPlayer = getCurrentPlayer();

            Optional<Card> optionalCard = currentPlayer.getPlayerCards().stream()
                    .filter(card -> card.getId() == cardId).findFirst();

            if (optionalCard.isPresent()) {
                Card card = optionalCard.get();

                Field gameField = game.getField();
                Optional<CardCell> cardWithSameValue = gameField.getCardCells().stream()
                        .filter(cardCell ->
                                cardCell.getBottomCard().getValue() == card.getValue()
                                || cardCell.getUpperCard() != null
                                && cardCell.getUpperCard().getValue() == card.getValue()).findFirst();

                if (gameField.getCardCells().size() == 0 || cardWithSameValue.isPresent()){
                    CardCell cardCell = new CardCell(card);
                    cardCell.setField(gameField);
                    gameField.getCardCells().add(cardCell);
                    removeCardFromPlayer(currentPlayer, card);

                    gameDAO.save(game);
                    return true;
                }


            }
            return false;
        } else {
            //not my turn
            //System.out.println("not your turn");
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

    private boolean tryToTakeCardsFromField() {
        //updateData();
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
        if (currentPlayerNumber == game.getWhichPlayerTurn()) {// my turn
            if (currentPlayerNumber == game.getWhichPlayerMove()) {// my move
                if (isGameFieldHasUnbeatenCards) {
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerMove(1);
                    }
                } else {
                    Player currentPlayer = getCurrentPlayer();
                    Player enemyPlayer = getEnemyPlayer();

                    game.getField().getCardCells().forEach(cardCell -> cardCell.setField(null));
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerTurn(2);
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerTurn(1);
                        game.setWhichPlayerMove(1);
                    }
                    giveCardsToPlayers(currentPlayer,enemyPlayer);

                }
            } else {// not my turn but my move
                //---------------------------waiting

            }
        } else {// not my turn
            if (currentPlayerNumber == game.getWhichPlayerMove()) {// my move
                if (isGameFieldHasUnbeatenCards) {
                    tryToTakeCardsFromField();
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerTurn(2);
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerTurn(1);
                        game.setWhichPlayerMove(1);
                    }
                    giveCardsToPlayers(game.getPlayer1(),game.getPlayer2());
                    //take cards
                } else {
                    if (game.getWhichPlayerMove() == 1) {
                        game.setWhichPlayerMove(2);
                    } else {
                        game.setWhichPlayerMove(1);
                    }
                    //finish move
                }
            } else { //not my turn, not my move
                //-----------------------------waiting
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
        Optional<Game> optionalGame = gameDAO.findGameByPlayerNameToLeave(player.getName());
        player.getPlayerCards().forEach(card -> card.setPlayer(null));
        player.getPlayerCards().clear();

        player.setTakesLastCard(false);
        player.setTakesPreLastCard(false);

        optionalGame.ifPresent(game -> {
            if (game.getPlayer1().getId() == player.getId()) {
                game.setPlayer1Leaves(true);
            } else {
                game.setPlayer2Leaves(true);
            }
            game.setFinished(true);
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
            Optional<Game> optionalGame = gameDAO.findNotFinishedGameByPlayerName(getUserName());
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
