package game.battleships.server.game.storage;

import game.battleships.server.exception.game.GameNotFoundException;
import game.battleships.server.game.BattleshipGameLobby;

import java.util.HashSet;
import java.util.Set;

public class BattleshipGameLobbyStorage implements GameStorage {
    private Set<BattleshipGameLobby> lobbies;


    public BattleshipGameLobbyStorage() {
        lobbies = new HashSet<>();
    }

    public Set<BattleshipGameLobby> getLobbies() {
        return lobbies;
    }

    public void addLobby(BattleshipGameLobby gameLobby) {
        lobbies.add(gameLobby);
    }
    public void removeLobby(BattleshipGameLobby gameLobby) {
        lobbies.remove(gameLobby);
    }

    public BattleshipGameLobby findRandomLobbyToPlay() throws GameNotFoundException {
        if (lobbies.isEmpty()) {
            throw new GameNotFoundException("no game lobbies started at the moment");
        }
        for (var lobby : lobbies) {
            if (lobby.getGame().notStarted() && !lobby.isFull()) {
                return lobby;
            }
        }
        throw new GameNotFoundException("no game lobbies started at the moment");
    }

    public BattleshipGameLobby findRandomLobbyToSpectate() throws GameNotFoundException {
        if (lobbies.isEmpty()) {
            throw new GameNotFoundException("no game lobbies started at the moment");
        }
        for (var lobby : lobbies) {
            if (lobby.getGame().isPlaying()) {
                return lobby;
            }
        }
        throw new GameNotFoundException("no game lobbies started at the moment");
    }

}
