package service;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDao;
import dataaccess.SqlGameDao;
import dataaccess.SqlUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.model.JoinGameRequest;

import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTests {
    private final SqlUserDao users = new SqlUserDao();
    private final SqlAuthDao auth = new SqlAuthDao();
    private final SqlGameDao games = new SqlGameDao();
    private JoinGameService joinGameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        joinGameService = new JoinGameService();
        users.clear();
        auth.clear();
        games.clear();
    }

    // Positive test
    @Test
    @DisplayName("Successfully adds user to the game")
    void joinGame() throws DataAccessException {
        // Setup
        users.createUser(new UserData("whiteUser","wh1t3","white@mail.com"));
        users.createUser(new UserData("testUser","p@ss","test@mail.com"));
        auth.createAuth(new AuthData("testToken","testUser"));

        int id = games.createGame(new GameData(
                0,
                "whiteUser",
                null,
                "test",
                ServiceTestUtils.NEW_CHESS_GAME));
        joinGameService.joinGame(new JoinGameRequest(
                "testToken",
                "BLACK",
                id));

        // Verify data
        GameData game = games.getGame(id);
        assertEquals("testUser",game.blackUsername());
    }

    // Positive Test 2:
    @Test
    @DisplayName("Test Observe")
    void observeGame() throws DataAccessException {
        // Setup
        users.createUser(new UserData("whiteUser","wh1t3","white@mail.com"));
        users.createUser(new UserData("testUser","p@ss","test@mail.com"));
        auth.createAuth(new AuthData("testToken","testUser"));

        int id = games.createGame(new GameData(
                0,
                "whiteUser",
                null,
                "test",
                ServiceTestUtils.NEW_CHESS_GAME));
        assertDoesNotThrow(() -> joinGameService.joinGame(new JoinGameRequest(
                "testToken",
                null,
                id)));
    }

    // Negative test 1: Bad request
    @ParameterizedTest(name = "Test invalid {0}")
    @MethodSource("badRequestsProvider")
    @DisplayName("Throws exception when argument is null")
    public void testJoinGameWithBadRequests(
            String description,
            String authToken,
            String playerColor,
            int gameID
    ) throws DataAccessException {
        // Verify exception
        assertThrows(BadRequestException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(authToken, playerColor, gameID)),
                "Failed validation for: " + description);

        // Verify data was not updated
        Collection<GameData> gamesList = games.listGames();
        assertTrue(gamesList.isEmpty());
    }

    private static Stream<Arguments> badRequestsProvider(){
        String validToken = "testToken";
        String validPlayerColor = "WHITE";
        int validGameID = 1234;

        return Stream.of(
                Arguments.of("authToken", null, validPlayerColor, validGameID),
                Arguments.of("playerColor, not a color",validToken, "", validGameID),
                Arguments.of("game ID, less than 0",validToken, validPlayerColor, -32)
        );
    }

    // Negative test 2: Already taken
    @Test
    @DisplayName("Name")
    public void testJoinGameWhenColorIsFull() throws DataAccessException {
        // Setup
        users.createUser(new UserData("whiteUser","dummy-p4ss","white@mail.com"));
        users.createUser(new UserData("blackUser","dummy-p@ss","black@mail.com"));
        users.createUser(new UserData("testUser","p4ss321","test@mail.com"));
        auth.createAuth(new AuthData("testToken","testUser"));
        int id = games.createGame(new GameData(
                0,
                "whiteUser",
                "blackUser",
                "testGame",
                ServiceTestUtils.NEW_CHESS_GAME));
        // Verify results
        assertThrows(AlreadyTakenException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(
                        "testToken",
                        "WHITE",
                        id
                )));
        assertThrows(AlreadyTakenException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(
                        "testToken",
                        "BLACK",
                        id
                )));

        // Verify game data did not update
        GameData game = games.getGame(id);
        assertEquals("whiteUser", game.whiteUsername());
        assertEquals("blackUser", game.blackUsername());
    }

}