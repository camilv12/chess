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
import service.model.JoinGameResult;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTests {
    private RamAuthDao authDao;
    private RamGameDao gameDao;
    private JoinGameService joinGameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDao = new RamAuthDao();
        gameDao = new RamGameDao();
        joinGameService = new JoinGameService();
        authDao.clear();
        gameDao.clear();
    }

    // Positive test
    @Test
    @DisplayName("JoinGameService adds user to the game")
    void joinGame() throws DataAccessException {
        // Setup
        authDao.createAuth(new AuthData("testToken","testUser"));
        gameDao.createGame(new GameData(1234,
                "whiteUser",
                null,
                "test",
                new ChessGame()));
        JoinGameResult result = joinGameService.joinGame(new JoinGameRequest(
                "testToken",
                "BLACK",
                1234));

        // Verify result
        assertNotNull(result, "Should return JoinGameResult object");

        // Verify data
        GameData game = gameDao.getGame(1234);
        assertEquals("testUser",game.blackUsername());
    }

    // Negative test 1: Bad request
    @ParameterizedTest(name = "Test invalid {0}")
    @MethodSource("badRequestsProvider")
    @DisplayName("CreateGameService throws exception when argument is null")
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
        ServiceTestUtils.verifyEmptyGameAndAuthDaos(gameDao, authDao);
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

    // Negative test 2: Unauthorized token
    @Test
    @DisplayName("JoinGameService throws exception when authToken is not found")
    public void testUnauthorizedJoinGame(){
        // Verify exception
        assertThrows(UnauthorizedException.class, () ->
                joinGameService.joinGame(new JoinGameRequest("token1234","WHITE",5678)));

        // Verify data did not update
        ServiceTestUtils.verifyEmptyGameAndAuthDaos(gameDao, authDao);
    }

    // Negative test 3: Already taken
    @Test
    @DisplayName("Name")
    public void testJoinGameWhenColorIsFull() throws DataAccessException {
        // Setup
        authDao.createAuth(new AuthData("testToken","testUser"));
        gameDao.createGame(new GameData(
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
        GameData game = gameDao.getGame(1337);
        assertEquals("whiteUser", game.whiteUsername());
        assertEquals("blackUser", game.blackUsername());
    }

}