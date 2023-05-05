package game.battleships.server.request.ui;

public class ConsoleUI {
    public static final String WELCOME_TEXT_MESSAGE = """
            Welcome to Battleships 1.0
            (currently beta)
            """;
    public static final String INVALID_COMMAND = """
            invalid command
            """;

    public static final String AUTHENTICATION_MENU = """
          register - Create account to battle with millions of players
          log in - Already have an account?
          quit - exit the application
          """;
    public static final String ACTIONS_ALLOWED_MESSAGE = """
            actions allowed:
            """;
    public static final String USERNAME_FORM = """
            username:
            """;
    public static final String PASSWORD_FORM = """
           password:
           """;

    public static final String INVALID_USERNAME_FORMAT_MESSAGE = """
            usernames can contain only numbers and latin letters,
            also the length of the username should be at least 6 and no more than 20
            """;
    public static final String USERNAME_TAKEN_MESSAGE = """
            this username is already taken, try a new one
            """;

    public static final String REGISTRATION_COMPLETE_MESSAGE = """
            Congratulations, your account has been created!
            """;
    public static final String TRY_AGAIN_MESSAGE = """
            try again
            """;
    public static final String INVALID_PASSWORD_FORMAT_MESSAGE = """
            password must be 6-15 characters long,
            at least one: upper latin letter, lower latin letter, number
            """;
    public static final String CANNOT_REGISTER_NOW_MESSAGE = """
            registration is impossible currently
            """;
    public static final String CANNOT_LOGIN_NOW_MESSAGE = """
            login is impossible currently
            """;

    public static final String INVALID_CREDENTIALS_MESSAGE = """
            invalid login credentials
            """;

    public static final String MAIN_MENU = """
            MAIN MENU
            create lobby
            browse lobbies
            friends
            friend requests
            saved games
            profile
            log out
            """;
    public static final String ACCOUNT_ALREADY_LOGGED_MESSAGE = """
        account logged in from another machine.
        """;

    public static final String LOGGED_OUT_MESSAGE = """
            logged out successfully!
            """;

    public static final String FUNCTIONALITY_IN_DEVELOPMENT_MESSAGE = """
            this functionality is still in development.
            """;

    public static final String LOBBIES_MESSAGE = """
            lobbies:
            """;

    public static final String BROWSE_LOBBIES_MENU = """
            spectate id - to spectate a game by lobby id
            join id - to join lobby by id
            refresh
            main menu
            """;
    public static final String SAVED_GAMES_MESSAGE = """
            saved games:
            """;
    public static final String SAVED_GAMES_MENU = """
            continue id - continue game by id
            main menu
            """;
    public static final String SAVED_GAME_NOT_FOUND_MESSAGE = """
            saved game not found
            """;
    public static final String LOBBY_MENU = """ 
           ready
           not ready
           make ship composition
           random ship composition
           leave
           """;
    public static final String LOBBY_IS_CURRENTLY_FULL = """
            lobby is currently full
            """;
    public static final String LOBBY_NOT_FOUND_MESSAGE = """
            lobby not found
            """;
    public static final String SPECTATING_MESSAGE = """
            started spectating!
            """;
    public static final String SPECTATING_MENU = """
            stop spectating - to return to main menu
            """;
    public static final String LOBBY_UPDATE = """
            lobby update:
            """;
    public static final String HORIZONTAL_LINE = """
            --------------------------------------
            """;

    public static final String IS_READY_MESSAGE = """
             is ready!
            """;
    public static final String IS_NOT_READY_MESSAGE = """
             is not ready!
            """;
    public static final String SHIP_COMPOSITION_MISSING_MESSAGE = """
            ship composition missing!
            """;
    public static final String GAME_STARTED_MESSAGE = """
            game started!
            """;

    public static final String GENERAL_ERROR = """
            General Error
            """;
    public static final String CANNOT_COMPOSE_WHEN_READY = """
            cannot make ship composition when ready
            """;
    public static final String MAKING_COMPOSITION_MESSAGE = """
            started making composition!
            LEGEND
            --------------------
            one 5 length ship
            two 4 length ships
            three 3 length ships
            four 2 length ships
            ONLY HORIZONTALLY AND VERTICALLY
            --------------------
            """;
    public static final String LEFT_THE_GAME_MESSAGE = "left the game";

    public static final String INVALID_SHIP_MESSAGE = """
            invalid ship
            """;
    public static final String INVALID_SHIP_LENGTH_MESSAGE = """
            ship length is incorrect
            """;
    public static final String GENERATED_SHIP_COLLIDES_MESSAGE = """
            generated ship collides with previously generated
            """;
    public static final String COMPOSITION_NOT_READY_MESSAGE = """
            ship composition is not ready
            """;
    public static final String BEGIN_FROM_START_MESSAGE = """
            begin from the start
            """;
    public static final String ENTER_TWO_LENGTH_SHIP = """
            enter end points for two length ship
            """;
    public static final String ENTER_THREE_LENGTH_SHIP = """
            enter end points for three length ship
            """;
    public static final String ENTER_FOUR_LENGTH_SHIP = """
            enter end points for four length ship
            """;
    public static final String ENTER_FIVE_LENGTH_SHIP = """
            enter end points for five length ship""";
    public static final String PRESS_COMPLETE_MESSAGE = """
            press complete if you are satisfied with your composition
            """;
    public static final String SHIP_COMPOSITION_BUILT_MESSAGE = """
            ship composition already built
            """;
    public static final String YOUR_COMPOSITION_IS_SAVED_MESSAGE = """
            your composition is saved
            """;
    public static final String SHIP_CONSTRUCTION_TIP = """
            To create a ship enter the two endpoints of the ship,
            example: A1-A2
            """;
    public static final String MAKING_SHIP_COMPOSITION_MENU = """
            leave - to return to lobby
            remove last - to remove last ship added
            complete - when you are happy with your ships
            """;
    public static final String WAIT_FOR_TURN_MESSAGE = """
            wait for your turn!
            """;
    public static final String INVALID_COORDINATES_MESSAGE = """
            invalid coordinates
            """;

}
