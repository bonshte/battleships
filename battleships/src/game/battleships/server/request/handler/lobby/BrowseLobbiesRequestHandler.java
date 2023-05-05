package game.battleships.server.request.handler.lobby;

import game.battleships.server.data.ServerData;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.lobby.PlayerLobbyCapacityException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.game.storage.BattleshipGameLobbyStorage;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.stream.Collectors;

public class BrowseLobbiesRequestHandler extends AbstractRequestHandler {
    private static final String JOIN_COMMAND = "join";
    private static final String REFRESH_COMMAND = "refresh";
    private static final String MAIN_MENU = "main menu";
    private static final String SPACE = " ";
    private static final String SPECTATE_COMMAND = "spectate";
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        if (clientInputString.equals(MAIN_MENU)) {
            writeClientOutput(clientSocketChannel, ConsoleUI.MAIN_MENU);
            clientSession.setSessionStatus(SessionStatus.MAIN_MENU);
            return;
        }
        if (clientInputString.equals(REFRESH_COMMAND)) {
            writeClientOutput(clientSocketChannel,  ConsoleUI.LOBBIES_MESSAGE);
            sendClientLobbiesInformation(clientSocketChannel, serverData.getGameLobbyStorage());
            return;
        }
        String[] clientWords = clientInputString.split(SPACE);
        if (clientWords.length != 2) {
            writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                    ConsoleUI.BROWSE_LOBBIES_MENU);
            return;
        }
        if (clientWords[0].equals(JOIN_COMMAND)) {
            try {
                int lobbyID = Integer.parseInt(clientWords[1]);
                var upcomingLobbies = serverData.getGameLobbyStorage().getLobbies()
                        .stream()
                        .filter(x -> x.getGame().notStarted())
                        .collect(Collectors.toSet());
                for (var upcomingLobby : upcomingLobbies) {
                    if (upcomingLobby.getLobbyID() == lobbyID) {
                        upcomingLobby.addAsPlayer(clientSession.getAccountAssociated(),
                                clientSocketChannel, clientSession);
                        clientSession.getAccountAssociated().setCurrentGameLobby(upcomingLobby);
                        clientSession.getAccountAssociated().setAccountStatus(AccountStatus.IN_LOBBY);
                        clientSession.setSessionStatus(SessionStatus.LOBBY_MENU);
                        writeClientOutput(clientSocketChannel, ConsoleUI.LOBBY_MENU);
                        sendPlayersLobbyInformation(upcomingLobby);
                        return;
                    }
                }
                writeClientOutput(clientSocketChannel, ConsoleUI.LOBBY_NOT_FOUND_MESSAGE);

            } catch (NumberFormatException e ) {
                writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                        ConsoleUI.BROWSE_LOBBIES_MENU);
            } catch (PlayerLobbyCapacityException e) {
                writeClientOutput(clientSocketChannel, ConsoleUI.LOBBY_IS_CURRENTLY_FULL);
            }
        } else if (clientWords[0].equals(SPECTATE_COMMAND)) {
            try {
                int lobbyID = Integer.parseInt(clientWords[1]);
                var startedLobbies = serverData.getGameLobbyStorage().getLobbies();
                for (var startedLobby : startedLobbies) {
                    if (startedLobby.getLobbyID() == lobbyID) {
                        startedLobby.addAsSpectator(clientSession.getAccountAssociated(), clientSocketChannel);
                        clientSession.getAccountAssociated().setAccountStatus(AccountStatus.SPECTATING);
                        clientSession.getAccountAssociated().setCurrentGameLobby(startedLobby);
                        clientSession.setSessionStatus(SessionStatus.SPECTATING);
                        writeClientOutput(clientSocketChannel, startedLobby.getLobbyInformation() +
                                ConsoleUI.HORIZONTAL_LINE + ConsoleUI.SPECTATING_MESSAGE + ConsoleUI.SPECTATING_MENU);
                        return;
                    }
                }
                writeClientOutput(clientSocketChannel, ConsoleUI.LOBBY_NOT_FOUND_MESSAGE);
            } catch (NumberFormatException e) {
                writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                        ConsoleUI.BROWSE_LOBBIES_MENU);
            }
        } else {
            writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                    ConsoleUI.ACTIONS_ALLOWED_MESSAGE + ConsoleUI.BROWSE_LOBBIES_MENU);
        }
    }

    private void sendPlayersLobbyInformation(BattleshipGameLobby gameLobby) throws IOException {
        for (var lobbyMemberSocket : gameLobby.getLobbyMembersSocketChannels()) {
            writeClientOutput(lobbyMemberSocket, ConsoleUI.LOBBY_UPDATE + gameLobby.getLobbyInformation());
        }
    }

    private void sendClientLobbiesInformation(SocketChannel clientSocketChannel,
                                              BattleshipGameLobbyStorage gameLobbyStorage) throws IOException {
        var startedLobbies = gameLobbyStorage.getLobbies();
        for (var startedLobby : startedLobbies) {
            writeClientOutput(clientSocketChannel,  startedLobby.getLobbyInformation() +
                    ConsoleUI.HORIZONTAL_LINE);
        }
    }
}
