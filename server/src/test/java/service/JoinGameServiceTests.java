package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamGameDao;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.model.JoinGameRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTests {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private JoinGameService joinGameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        joinGameService = new JoinGameService();
        auth.clear();
        games.clear();
    }

    // Positive test
    @Test
    @DisplayName("Successfully adds user to the game")
    void joinGame() throws DataAccessException {
        // Setup
        auth.createAuth(new AuthData("testToken","testUser"));
        games.createGame(new GameData(1234,
                "whiteUser",
                null,
                "test",
                new ChessGame()));
        joinGameService.joinGame(new JoinGameRequest(
                "testToken",
                "BLACK",
                1234));

        // Verify data
        GameData game = games.getGame(1234);
        assertEquals("testUser",game.blackUsername());
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
    ) {
        // Verify exception
        assertThrows(BadRequestException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(authToken, playerColor, gameID)),
                "Failed validation for: " + description);

        // Verify data was not updated
        ServiceTestUtils.verifyEmptyGameAndAuthDaos(games, auth);
    }

    private static Stream<Arguments> badRequestsProvider(){
        String validToken = "testToken";
        String validPlayerColor = "WHITE";
        int validGameID = 1234;

        return Stream.of(
                Arguments.of("authToken", null, validPlayerColor, validGameID),
                Arguments.of("playerColor, null",validToken, null, validGameID),
                Arguments.of("playerColor, not a color",validToken, "user", validGameID),
                Arguments.of("playerColor, not a color",validToken, validPlayerColor, -32)
        );
    }

    // Negative test 2: Already taken
    @Test
    @DisplayName("Name")
    public void testJoinGameWhenColorIsFull() throws DataAccessException {
        // Setup
        auth.createAuth(new AuthData("testToken","testUser"));
        games.createGame(new GameData(
                1337,
                "whiteUser",
                "blackUser",
                "testGame",
                new ChessGame()));
        // Verify results
        assertThrows(AlreadyTakenException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(
                        "testToken",
                        "WHITE",
                        1337
                )));
        assertThrows(AlreadyTakenException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(
                        "testToken",
                        "BLACK",
                        1337
                )));

        // Verify game data did not update
        GameData game = games.getGame(1337);
        assertEquals("whiteUser", game.whiteUsername());
        assertEquals("blackUser", game.blackUsername());
    }

}