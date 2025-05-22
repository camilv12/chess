package service;

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
import service.model.CreateGameRequest;
import service.model.CreateGameResult;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTests {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private CreateGameService createGameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        createGameService = new CreateGameService();
        auth.clear();
        games.clear();
    }

    // Positive test
    @Test
    @DisplayName("Successfully creates a game")
    void createGame() throws DataAccessException {
        // Setup:
        auth.createAuth(new AuthData("testToken", "testUser"));
        CreateGameRequest request = new CreateGameRequest("testToken","testGame");
        CreateGameResult result = createGameService.createGame(request);

        // Verify result
        assertTrue(result.gameID() > 0, "Game was not created");

        // Verify game storage
        GameData game = games.getGame(result.gameID());
        assertEquals("testGame", game.gameName());
    }

    // Negative Test 1: Bad Request
    @ParameterizedTest(name = "Test null {0}")
    @MethodSource("badRequestsProvider")
    @DisplayName("Throws exception when argument is null")
    public void testCreateGameWithBadRequests(
            String description,
            String authToken,
            String gameName
    ){
        // Verify result
        assertThrows(BadRequestException.class, () ->
                createGameService.createGame(new CreateGameRequest(authToken, gameName)),
                "Failed validation for: " + description);

        // Verify data did not update
        ServiceTestUtils.verifyEmptyGameAndAuthDaos(games, auth);
    }

    private static Stream<Arguments> badRequestsProvider(){
        String validToken = "testToken";
        String validGameName = "testGame";

        return Stream.of(
                Arguments.of("authToken", null, validGameName),
                Arguments.of("gameName",validToken, null)
        );
    }

    // Negative Test 2: Unauthorized
    @Test
    @DisplayName("Throws exception when token is not found")
    public void testUnauthorizedCreateGame() {
        // Setup
        assertThrows(UnauthorizedException.class, () ->
                createGameService.createGame(new CreateGameRequest("test1234","game1234")));

        // Verify data did not update
        ServiceTestUtils.verifyEmptyGameAndAuthDaos(games, auth);
    }

}