package service;

import dataaccess.DataAccessException;
import dataaccess.ram.RamGameDao;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.ListGamesResult;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ListGamesServiceTest {
    private final RamGameDao games = new RamGameDao();
    private ListGamesService listGamesService;

    @BeforeEach
    void setUp() throws DataAccessException {
        listGamesService = new ListGamesService();
        games.clear();
    }

    // Positive test
    @Test
    @DisplayName("Lists games successfully")
    void listGames() throws DataAccessException {
        // Setup
        GameData game1 = new GameData(1234,
                "whiteUser1",
                "blackUser1",
                "gameName1",
                "");
        GameData game2 = new GameData(5678,
                "whiteUser2",
                "blackUser2",
                "gameName2",
                "");
        games.createGame(game1);
        games.createGame(game2);
        ListGamesResult result = listGamesService.listGames();

        // Verify result
        assertNotNull(result, "Should return a ListGamesResult");
        assertEquals(2, result.games().size());

        // Check against data
        Set<Integer> expectedIds = Set.of(game1.gameID(), game2.gameID());
        Set<Integer> actualIds = result.games().stream()
                .map(GameData::gameID)
                .collect(Collectors.toSet());
        assertEquals(expectedIds, actualIds, "Returned game IDs should match created games");
    }

    // Negative Test: Unauthorized
    @Test
    @DisplayName("Game list empty")
    public void testUnauthorizedListGames(){
        // Verify exception
        assertDoesNotThrow(() ->
                listGamesService.listGames());
    }

}