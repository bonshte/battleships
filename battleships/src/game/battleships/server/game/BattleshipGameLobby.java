package game.battleships.server.game;

import game.battleships.server.account.Account;
import game.battleships.server.exception.game.GameNotStartedException;
import game.battleships.server.exception.ingame.NotOnTurnException;
import game.battleships.server.exception.game.GameStatusException;
import game.battleships.server.exception.lobby.PlayerLobbyCapacityException;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.exception.ship.composition.ShipCompositionNotReadyException;
import game.battleships.server.game.board.BattleshipFieldPoint;
import game.battleships.server.game.board.ShipComposition;
import game.battleships.server.game.board.StrikeCondition;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BattleshipGameLobby {
    private static final String LOBBY_ID_IS = "lobby id: ";
    private static final String COMMA = ",";
    private static final String NO_PLAYERS = "no players";
    private static final String NO_SPECTATORS = "no spectators";
    private static final String PLAYERS = "players: ";
    private static final String SPECTATORS = "spectators: ";
    private static int globalLobbyId = 0;
    private final int lobbyID;
    private final BattleshipsGame game;

    private Account firstPlayer;
    private transient Session firstPlayerSession;
    private transient SocketChannel firstPlayerSocketChannel;
    private boolean firstPlayerReady;

    private Account secondPlayer;
    private transient Session secondPlayerSession;
    private transient SocketChannel secondPlayerSocketChannel;

    private boolean secondPlayerReady;
    private Map<Account, SocketChannel> spectatorsToSocketChannels;


    public BattleshipGameLobby(Account owner, SocketChannel clientSocketChannel, Session clientSession) {
        this.firstPlayer = owner;
        this.firstPlayerSocketChannel = clientSocketChannel;
        this.firstPlayerSession = clientSession;
        this.lobbyID = globalLobbyId++;
        this.game = new BattleshipsGame();
        this.spectatorsToSocketChannels = new HashMap<>();
    }
    public BattleshipGameLobby(Account creator, BattleshipsGame game,
                               SocketChannel clientSocketChannel, Session clientSession) {
        this.firstPlayer = creator;
        this.firstPlayerSocketChannel = clientSocketChannel;
        this.firstPlayerSession = clientSession;
        this.lobbyID = globalLobbyId++;
        this.game = game;
        this.spectatorsToSocketChannels = new HashMap<>();
    }
    public String getFirstPlayerUsername() {
        return firstPlayer.getUsername();
    }
    public String getSecondPlayerUsername() {
        return secondPlayer.getUsername();
    }

    public SocketChannel getFirstPlayerSocketChannel() throws PlayerNotInLobbyException {
        if (!hasFirstPlayer()) {
            throw new PlayerNotInLobbyException("no first player in lobby");
        }
        return firstPlayerSocketChannel;
    }
    public SocketChannel getSecondPlayerSocketChannel()  throws PlayerNotInLobbyException {
        if (!hasSecondPlayer()) {
            throw new PlayerNotInLobbyException("no second player in lobby");
        }
        return secondPlayerSocketChannel;
    }

    public List<SocketChannel> getSpectatorsSocketChannels() {
        return spectatorsToSocketChannels.values().stream().toList();
    }
    public List<SocketChannel> getLobbyMembersSocketChannels() {
        List<SocketChannel> membersSocketChannels = new LinkedList<>();
        if (hasFirstPlayer()) {
            membersSocketChannels.add(firstPlayerSocketChannel);
        }
        if (hasSecondPlayer()) {
            membersSocketChannels.add(secondPlayerSocketChannel);
        }
        for (var spectatorSocketChannel : getSpectatorsSocketChannels()) {
            membersSocketChannels.add(spectatorSocketChannel);
        }
        return membersSocketChannels;
    }
    public void removePlayer(Account account) throws PlayerNotInLobbyException {
        if (account == null) {
            BattleshipLogger.getBattleshipLogger().warning("null passed to method removePlayer");
            throw new IllegalArgumentException("null passed");
        }
        if (hasFirstPlayer() && firstPlayer.equals(account)) {
            firstPlayer = null;
            firstPlayerReady = false;
            firstPlayerSession = null;
            firstPlayerSocketChannel = null;
        } else if (hasSecondPlayer() && secondPlayer.equals(account)) {
            secondPlayer = null;
            secondPlayerReady = false;
            secondPlayerSession = null;
            secondPlayerSocketChannel = null;
        } else {
            throw new PlayerNotInLobbyException("player is not in the lobby");
        }
    }
    public void removeSpectator(Account account) {
        if (account == null) {
            BattleshipLogger.getBattleshipLogger().warning("null passed to RemoveSpectator");
            throw new IllegalArgumentException("null argument passed");
        }
        spectatorsToSocketChannels.remove(account);
    }

    public Account getFirstPlayer() {
        return firstPlayer;
    }
    public boolean isEmpty() {
        return !hasFirstPlayer() && !hasSecondPlayer() && spectatorsToSocketChannels.isEmpty();
    }

    public boolean playerIsReady(Account account) throws PlayerNotInLobbyException {
        if (account == null) {
            BattleshipLogger.getBattleshipLogger().warning("null passed to playerIsReady");
            throw new IllegalArgumentException("null argument passed");
        }
        if (hasFirstPlayer() && account.equals(firstPlayer)) {
            return firstPlayerReady;
        } else if (hasSecondPlayer() && account.equals(secondPlayer)) {
            return secondPlayerReady;
        }
        throw new PlayerNotInLobbyException("account passed is not a player in the lobby");
    }

    public void setShipComposition(ShipComposition shipComposition, Account account)
            throws PlayerNotInLobbyException {
        if (hasFirstPlayer() && account.equals(firstPlayer)) {
            game.setFirstPlayerShipComposition(shipComposition);
        } else if (hasSecondPlayer() && account.equals(secondPlayer)) {
            game.setSecondPlayerShipComposition(shipComposition);
        } else {
            throw new PlayerNotInLobbyException("account passed is not a player in the lobby");
        }
    }
    public void notReady(Account account) throws PlayerNotInLobbyException {
        if (hasFirstPlayer() && account.equals(firstPlayer)) {
            firstPlayerReady = false;
        } else if (hasSecondPlayer() && account.equals(secondPlayer)) {
            secondPlayerReady = false;
        } else {
            throw new PlayerNotInLobbyException("account passed is not a player in the game");
        }

    }
    public void movePlayersToMainMenu() {
        if (hasFirstPlayer()) {
            firstPlayerSession.setSessionStatus(SessionStatus.MAIN_MENU);
        }
        if (hasSecondPlayer()) {
            secondPlayerSession.setSessionStatus(SessionStatus.MAIN_MENU);
        }

    }
    public void clearLobby() {
        firstPlayer = null;
        firstPlayerReady = false;
        firstPlayerSession = null;
        firstPlayerSocketChannel = null;
        secondPlayer = null;
        secondPlayerReady = false;
        secondPlayerSession = null;
        secondPlayerSocketChannel = null;
        spectatorsToSocketChannels = null;
    }

    public StrikeStatistic strikeEnemy(BattleshipFieldPoint fieldPoint, Account account)
            throws GameNotStartedException, PlayerNotInLobbyException, NotOnTurnException {
        if (hasFirstPlayer() && firstPlayer.equals(account)) {
            StrikeCondition condition = game.firstPlayerTurn(fieldPoint);
            return new StrikeStatistic(fieldPoint, condition);
        } else if (hasSecondPlayer() && secondPlayer.equals(account)) {
            StrikeCondition condition = game.secondPlayerTurn(fieldPoint);
            return new StrikeStatistic(fieldPoint, condition);
        }
        throw new PlayerNotInLobbyException("account passed is not player in the lobby");
    }



    public void ready(Account account) throws ShipCompositionNotReadyException, PlayerNotInLobbyException {
        if (hasFirstPlayer() && account.equals(firstPlayer)) {
            if (!game.firstPlayerBoardIsSet()) {
                throw new ShipCompositionNotReadyException("player does not have a composition");
            }
            firstPlayerReady = true;

        } else if (hasSecondPlayer() && account.equals(secondPlayer)) {
            if (!game.secondPlayerBoardIsSet()) {
                throw new ShipCompositionNotReadyException("player does not have a composition");
            }
            secondPlayerReady = true;
        } else {
            throw new PlayerNotInLobbyException("account passed is not a player in the game");
        }
        if (playersAreReady()) {
            try {
                game.startGame();

            } catch (GameStatusException e) {
                BattleshipLogger.getBattleshipLogger().warning(e.getMessage());
            }
            movePlayersInGame();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleshipGameLobby gameLobby)) return false;
        return lobbyID == gameLobby.lobbyID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobbyID);
    }

    public int getLobbyID() {
        return lobbyID;
    }
    public BattleshipsGame getGame() {
        return game;
    }
    public void addAsPlayer(Account account, SocketChannel clientSocketChannel, Session clientSession)
            throws PlayerLobbyCapacityException {
        if (isFull()) {
            throw new PlayerLobbyCapacityException("player capacity is reached");
        }
        if (!hasFirstPlayer()) {
            firstPlayer = account;
            firstPlayerSocketChannel = clientSocketChannel;
            firstPlayerSession = clientSession;
        } else {
            secondPlayer = account;
            secondPlayerSocketChannel = clientSocketChannel;
            secondPlayerSession = clientSession;
        }
    }
    public void addAsSpectator(Account account, SocketChannel clientSocketChannel) {
        if (account == null || clientSocketChannel == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        spectatorsToSocketChannels.put(account, clientSocketChannel);
    }
    public boolean isFull() {
        return hasFirstPlayer() && hasSecondPlayer();
    }
    public String getLobbyInformation() {
        return game.getGameStatus() + System.lineSeparator() +
                LOBBY_ID_IS + lobbyID + System.lineSeparator() +
                PLAYERS + getPlayersUsernames() + System.lineSeparator() +
                SPECTATORS + getSpectatorsUsernames() + System.lineSeparator();
    }
    private String getPlayersUsernames() {
        StringBuilder playersUsernames = new StringBuilder();
        if (hasFirstPlayer()) {
            playersUsernames.append(firstPlayer.getUsername());
            playersUsernames.append(COMMA);
        }
        if (hasSecondPlayer()) {
            playersUsernames.append(secondPlayer.getUsername());
        }
        if (playersUsernames.isEmpty()) {
            return NO_PLAYERS;
        }
        return playersUsernames.toString();
    }
    private String getSpectatorsUsernames() {
        if (spectatorsToSocketChannels.isEmpty()) {
            return NO_SPECTATORS;
        }
        StringBuilder spectatorsUsernames = new StringBuilder();
        var iterator = spectatorsToSocketChannels.keySet().iterator();
        while (iterator.hasNext()) {
            var spectator = iterator.next();
            spectatorsUsernames.append(spectator.getUsername());
            if (iterator.hasNext()) {
                spectatorsUsernames.append(COMMA);
            }
        }
        return spectatorsUsernames.toString();
    }

    private void movePlayersInGame() {
        firstPlayerSession.setSessionStatus(SessionStatus.IN_GAME);
        secondPlayerSession.setSessionStatus(SessionStatus.IN_GAME);
    }
    private boolean isOnTurn(Account account) throws GameNotStartedException, PlayerNotInLobbyException {
        if (!game.isPlaying()) {
            throw new GameNotStartedException("game is not running");
        }
        if (hasFirstPlayer() && firstPlayer.equals(account)) {
            return game.firstPlayerOnTurn();
        } else if (hasSecondPlayer() && secondPlayer.equals(account)) {
            return !game.firstPlayerOnTurn();
        }
        throw new PlayerNotInLobbyException("player is not in the game lobby");
    }
    private boolean playersAreReady() {
        return firstPlayerReady && secondPlayerReady;
    }
    private boolean hasSecondPlayer() {
        return secondPlayer != null;
    }
    private boolean hasFirstPlayer() {
        return firstPlayer != null;
    }
}
