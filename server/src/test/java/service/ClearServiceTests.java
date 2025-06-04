package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private final SqlUserDao users = new SqlUserDao();
    private final SqlGameDao games = new SqlGameDao();
    private final SqlAuthDao auth = new SqlAuthDao();

    private final ClearService clearService = new ClearService();

    @BeforeEach
    void setUp() throws DataAccessException {
        users.clear();
        games.clear();
        auth.clear();
    }

    @Test
    @DisplayName("Clears stored data")
    public void clearDatabase() throws DataAccessException {
        // Setup DAO with test data
        users.createUser(new UserData("testUser","p@ssw0rd123","user@test.com"));
        auth.createAuth(new AuthData("testToken", "testUser"));
        games.createGame(new GameData(0,
                "testUser",
                null,
                "Test Game",
                ServiceTestUtils.NEW_CHESS_GAME));

        // Execute method
        clearService.clear();

        // Verify empty DAOs
        assertThrows(NotFoundException.class, () -> users.getUser("testUser"));
        assertThrows(NotFoundException.class, () -> auth.getAuth("testToken"));
        Collection<GameData> remainingGames = games.listGames();
        assertTrue(remainingGames.isEmpty());

    }

    @Test
    @DisplayName("Succeeds with an empty database")
    public void clearEmptyDatabase() {
        assertDoesNotThrow(clearService::clear);
    }

}
