package service;

import dataaccess.DataAccessException;
import dataaccess.SqlGameDao;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;
import service.model.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {
    private final SqlGameDao games = new SqlGameDao();
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameService = new GameService();
        games.clear();
    }

    // Positive test - Create Game
    @Test
    @DisplayName("Successfully creates a game")
    void createGame() throws DataAccessException {
        // Setup:
        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameResult result = gameService.createGame(request);

        // Verify result
        assertTrue(result.gameID() > 0, "Game was not created");

        // Verify game storage
        GameData game = games.getGame(result.gameID());
        assertEquals("testGame", game.gameName());
    }

    // Positive Test - List Games
    @Test
    @DisplayName("Lists games successfully")
    void listGames() throws DataAccessException {
        // Setup
        GameData game1 = new GameData(0,
                null,
                null,
                "gameName1",
                ServiceTestUtils.NEW_CHESS_GAME);
        GameData game2 = new GameData(0,
                null,
                null,
                "gameName2",
                ServiceTestUtils.NEW_CHESS_GAME);
        games.createGame(game1);
        games.createGame(game2);
        ListGamesResult result = gameService.listGames();

        // Verify result
        assertNotNull(result, "Should return a ListGamesResult");
        assertEquals(2, result.games().size());
    }

    // Negative Test: Bad CreateGame Request
    @Test
    @DisplayName("Throws exception when argument is null")
    public void testCreateGameWithBadRequest(){
        // Verify result
        assertThrows(BadRequestException.class, () ->
                gameService.createGame(new CreateGameRequest(null))
        );

        // Verify data did not update
        assertThrows(DataAccessException.class, () -> games.getGame(1));
    }

    // Negative Test: Empty List still returns
    @Test
    @DisplayName("Game list empty")
    public void testEmptyListGames(){
        // Verify exception
        assertDoesNotThrow(() ->
                gameService.listGames());
    }

}