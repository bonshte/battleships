package game.battleships.server.game.storage;

import game.battleships.server.exception.authentication.InvalidCredentialsException;
import game.battleships.server.exception.authentication.UsernameAlreadyTakenException;
import game.battleships.server.account.Account;
import game.battleships.server.account.AccountCredentials;
import java.util.HashSet;
import java.util.Set;

public class AccountStorage {
    private Set<Account> users;

    public AccountStorage() {
        users = new HashSet<>();
    }
    public AccountStorage(Set<Account> users) {
        this.users = users;
    }

    public Set<Account> getUsers() {
        return users;
    }

    public boolean usernameTaken(String username) {
        if (username == null) {
            throw new IllegalArgumentException("null passed to method");
        }
        for (var user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    public void registerUser(Account user) throws UsernameAlreadyTakenException {
        if (user == null) {
            throw new IllegalArgumentException("null passed to method");
        }
        if (usernameTaken(user.getUsername())) {
            throw new UsernameAlreadyTakenException("this username is already taken");
        }
        users.add(user);
    }

    public Account getUserByCredentials(AccountCredentials accountCredentials) throws InvalidCredentialsException {
        if (accountCredentials == null) {
            throw new IllegalArgumentException("null passed to method");
        }
        for (var user : users) {
            if (user.matchesCredentials(accountCredentials)) {
                return user;
            }
        }
        throw new InvalidCredentialsException("invalid credentials");
    }
}
