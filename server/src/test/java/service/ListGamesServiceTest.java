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
import service.model.ListGamesRequest;
import service.model.ListGamesResult;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ListGamesServiceTest {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private ListGamesService listGamesService;

    @BeforeEach
    void setUp() throws DataAccessException {
        listGamesService = new ListGamesService();
        auth.clear();
        games.clear();
    }

    // Positive test
    @Test
    @DisplayName("Lists games successfully")
    void listGames() throws DataAccessException {
        // Setup
        auth.createAuth(new AuthData("testToken","username"));
        GameData game1 = new GameData(1234,
                "whiteUser1",
                "blackUser1",
                "gameName1",
                new ChessGame());
        GameData game2 = new GameData(5678,
                "whiteUser2",
                "blackUser2",
                "gameName2",
                new ChessGame());
        games.createGame(game1);
        games.createGame(game2);
        ListGamesResult result = listGamesService.listGames(new ListGamesRequest("testToken"));

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
    @DisplayName("Game list fails when token not found")
    public void testUnauthorizedListGames(){
        // Verify exception
        assertThrows(UnauthorizedException.class, () ->
                listGamesService.listGames(new ListGamesRequest("testToken")));
        // Verify data did not update
        ServiceTestUtils.verifyEmptyGameAndAuthDaos(games,auth);
    }

}