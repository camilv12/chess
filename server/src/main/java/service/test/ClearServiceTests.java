package service.test;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.ClearService;
import service.request.ClearRequest;
import service.result.ClearResult;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private final RamAuthDao authDao = new RamAuthDao();
    private final RamGameDao gameDao = new RamGameDao();
    private final RamUserDao userDao = new RamUserDao();
    private final ClearService clearService = new ClearService();

    @Test
    @DisplayName("ClearService clears stored data")
    public void clearDatabase() throws DataAccessException {
        // Setup DAO with test data
        authDao.createAuth(new AuthData("testToken", "testUser"));
        gameDao.createGame(new GameData(1234,
                "white",
                "black",
                "Test Game",
                new ChessGame()));
        userDao.createUser(new UserData("testUser","p@ssw0rd123","user@test.com"));

        // Execute method
        ClearResult result = clearService.clear(new ClearRequest());

        // Assert Result
        assertNotNull(result, "Should return ClearResult object");

        // Verify empty DAOs
        assertThrows(DataAccessException.class, () -> authDao.getAuth("testToken"));
        assertThrows(DataAccessException.class, () -> gameDao.getGame(1234));
        assertThrows(DataAccessException.class, () -> userDao.getUser("user"));
    }

    @Test
    @DisplayName("ClearService succeeds with an empty database")
    public void clearEmptyDatabase() {
        // Execute method
        ClearResult result = clearService.clear(new ClearRequest());

        // Assert Result
        assertNotNull(result, "Should return ClearResult object");

        // Verify empty DAOs
        assertThrows(DataAccessException.class, () -> authDao.getAuth("testToken"));
        assertThrows(DataAccessException.class, () -> gameDao.getGame(1234));
        assertThrows(DataAccessException.class, () -> userDao.getUser("user"));
    }

}
