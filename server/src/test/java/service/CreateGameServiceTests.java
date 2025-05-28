package service;

import dataaccess.DataAccessException;
import dataaccess.ram.RamGameDao;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTests {
    private final RamGameDao games = new RamGameDao();
    private CreateGameService createGameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        createGameService = new CreateGameService();
        games.clear();
    }

    // Positive test
    @Test
    @DisplayName("Successfully creates a game")
    void createGame() throws DataAccessException {
        // Setup:
        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameResult result = createGameService.createGame(request);

        // Verify result
        assertTrue(result.gameID() > 0, "Game was not created");

        // Verify game storage
        GameData game = games.getGame(result.gameID());
        assertEquals("testGame", game.gameName());
    }

    // Negative Test: Bad Request
    @Test
    @DisplayName("Throws exception when argument is null")
    public void testCreateGameWithBadRequest(){
        // Verify result
        assertThrows(BadRequestException.class, () ->
                createGameService.createGame(new CreateGameRequest(null))
        );

        // Verify data did not update
        ServiceTestUtils.verifyEmptyGameDao(games);
    }

}