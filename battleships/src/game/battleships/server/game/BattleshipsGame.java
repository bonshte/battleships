package game.battleships.server.game;

import game.battleships.server.exception.game.GameStatusException;
import game.battleships.server.exception.ingame.NotOnTurnException;
import game.battleships.server.exception.ship.composition.ShipCompositionNotReadyException;
import game.battleships.server.game.board.BattleshipGameBoard;
import game.battleships.server.game.board.BattleshipPoint;
import game.battleships.server.game.board.ShipComposition;
import game.battleships.server.game.board.StrikeCondition;
import game.battleships.server.exception.game.GameNotStartedException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BattleshipsGame {
    private static final String BOARD = "board";
    private static final String YOUR_BOARD = "YOUR BOARD";
    private static final String ENEMY_BOARD = "ENEMY BOARD";
    private static final String TIME_CREATED = "created on: ";
    private static final String LAST_TIME_PLAYED = "last played on: ";
    private static final String GAME_ID = "game id: ";
    private static int globalGameId = 0;
    private int gameId;
    private BattleshipGameBoard firstPlayerBoard;
    private BattleshipGameBoard firstPlayerEnemyBoard;
    private BattleshipGameBoard secondPlayerBoard;
    private BattleshipGameBoard secondPlayerEnemyBoard;
    private List<StrikeStatistic> firstPlayerTurns;
    private List<StrikeStatistic> secondPlayerTurns;
    private LocalDateTime timeStarted;
    private LocalDateTime lastTimePlayed;
    private GameStatus gameStatus;
    private ShipComposition firstPlayerShipComposition;
    private ShipComposition secondPlayerShipComposition;
    private boolean firstPlayerOnTurn;
    public static void setGlobalGameId(int id) {
        globalGameId = ++id;
    }
    public BattleshipsGame() {
        this.gameId = globalGameId++;
        this.gameStatus = GameStatus.NOT_STARTED;
        firstPlayerTurns = new LinkedList<>();
        secondPlayerTurns = new LinkedList<>();
        firstPlayerEnemyBoard = new BattleshipGameBoard();
        secondPlayerEnemyBoard = new BattleshipGameBoard();
        firstPlayerOnTurn = true;
    }
    public GameStatus getGameStatus() {
        return gameStatus;
    }
    public boolean firstPlayerBoardIsSet() {
        return firstPlayerBoard != null;
    }
    public boolean secondPlayerBoardIsSet() {
        return secondPlayerBoard != null;
    }

    public boolean firstPlayerOnTurn() {
        return firstPlayerOnTurn;
    }
    public boolean isPlaying() {
        return gameStatus == GameStatus.IN_PROGRESS;
    }
    public boolean notStarted() {
        return gameStatus == GameStatus.NOT_STARTED;
    }
    public boolean gameOver() {
        return gameStatus == GameStatus.FINISHED;
    }

    public void setFirstPlayerShipComposition(ShipComposition shipComposition) {
        firstPlayerShipComposition = shipComposition;
        firstPlayerBoard = new BattleshipGameBoard(shipComposition);
    }
    public void setSecondPlayerShipComposition(ShipComposition shipComposition) {
        secondPlayerShipComposition = shipComposition;
        secondPlayerBoard = new BattleshipGameBoard(shipComposition);
    }
    public void reverseBoard() {
        swap(firstPlayerBoard, secondPlayerBoard);
        swap(firstPlayerEnemyBoard, secondPlayerEnemyBoard);
        swap(firstPlayerTurns, secondPlayerTurns);
        swap(firstPlayerShipComposition, secondPlayerShipComposition);
    }
    public int getGameId() {
        return gameId;
    }
    public String getGameInfo() {
        return GAME_ID + gameId + System.lineSeparator() +
                TIME_CREATED + timeStarted.toString() + System.lineSeparator() +
                LAST_TIME_PLAYED + lastTimePlayed.toString() + System.lineSeparator();
    }
    public void startGame() throws ShipCompositionNotReadyException, GameStatusException {
        if (!firstPlayerBoardIsSet() || !secondPlayerBoardIsSet()) {
            throw new ShipCompositionNotReadyException("not every player has a ready board");
        }
        if (gameStatus != GameStatus.SAVED && gameStatus != GameStatus.NOT_STARTED) {
            throw new GameStatusException("game is not in saved nor set up phase");
        }
        this.gameStatus = GameStatus.IN_PROGRESS;
        this.timeStarted = LocalDateTime.now();
        updateLastTimePlayed();
    }

    public StrikeCondition firstPlayerTurn(BattleshipPoint fieldToStrike)
            throws GameNotStartedException, NotOnTurnException {
        if (gameStatus != GameStatus.IN_PROGRESS) {
            throw new GameNotStartedException("game is not running");
        }
        if (fieldToStrike == null) {
            throw new IllegalArgumentException("null field passed to be struck");
        }
        if (!firstPlayerOnTurn) {
            throw new NotOnTurnException("not first player's turn");
        }
        StrikeCondition condition = secondPlayerShipComposition.hitOnMark(fieldToStrike);
        StrikeStatistic strikeStatistic = new StrikeStatistic(fieldToStrike, condition);
        firstPlayerEnemyBoard.markStrike(strikeStatistic, secondPlayerShipComposition);
        secondPlayerBoard.markStrike(strikeStatistic, secondPlayerShipComposition);
        firstPlayerTurns.add(strikeStatistic);
        firstPlayerOnTurn = false;
        updateLastTimePlayed();
        if (condition == StrikeCondition.SANK && secondPlayerShipComposition.allAreSank()) {
            gameStatus = GameStatus.FINISHED;
        }
        return condition;
    }

    public StrikeCondition secondPlayerTurn(BattleshipPoint fieldToStrike)
            throws GameNotStartedException, NotOnTurnException {
        if (gameStatus != GameStatus.IN_PROGRESS) {
            throw new GameNotStartedException("game is not running");
        }
        if (fieldToStrike == null) {
            throw new IllegalArgumentException("null field passed to be struck");
        }
        if (firstPlayerOnTurn) {
            throw new NotOnTurnException("not second player's turn");
        }
        StrikeCondition condition = firstPlayerShipComposition.hitOnMark(fieldToStrike);
        StrikeStatistic strikeStatistic = new StrikeStatistic(fieldToStrike, condition);
        firstPlayerBoard.markStrike(strikeStatistic, firstPlayerShipComposition);
        secondPlayerEnemyBoard.markStrike(strikeStatistic, firstPlayerShipComposition);
        secondPlayerTurns.add(strikeStatistic);
        firstPlayerOnTurn = true;
        updateLastTimePlayed();
        if (condition == StrikeCondition.SANK && firstPlayerShipComposition.allAreSank()) {
            gameStatus = GameStatus.FINISHED;
        }
        return condition;
    }
    public String visualizeFirstPlayerBoards() {
        StringBuilder visualizedBoards = new StringBuilder();
        visualizedBoards.append(YOUR_BOARD + System.lineSeparator());
        visualizedBoards.append(firstPlayerBoard.getVisualizedBoard());
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(ENEMY_BOARD);
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(firstPlayerEnemyBoard.getVisualizedBoard());
        return visualizedBoards.toString();
    }
    public String visualizeSecondPlayerBoards() {
        StringBuilder visualizedBoards = new StringBuilder();
        visualizedBoards.append(YOUR_BOARD + System.lineSeparator());
        visualizedBoards.append(secondPlayerBoard.getVisualizedBoard());
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(ENEMY_BOARD);
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(secondPlayerEnemyBoard.getVisualizedBoard());
        return visualizedBoards.toString();
    }
    public String visualizeSpectatorBoards(String firstPlayerUsername, String secondPlayerUsername) {
        StringBuilder visualizedBoards = new StringBuilder();
        visualizedBoards.append(firstPlayerUsername + "'s " + BOARD);
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(secondPlayerEnemyBoard.getVisualizedBoard());
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(secondPlayerUsername + "'s " + BOARD);
        visualizedBoards.append(System.lineSeparator());
        visualizedBoards.append(firstPlayerEnemyBoard.getVisualizedBoard());
        visualizedBoards.append(System.lineSeparator());
        return visualizedBoards.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleshipsGame that)) return false;
        return gameId == that.gameId;
    }
    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }
    private void updateLastTimePlayed() {
        this.lastTimePlayed = LocalDateTime.now();
    }
    private <T> void swap(T first, T second) {
        T temp = first;
        first = second;
        second = temp;
    }
}
