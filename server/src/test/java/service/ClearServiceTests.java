package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ClearServiceTests {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private final RamUserDao users = new RamUserDao();
    private final ClearService clearService = new ClearService();

    @Test
    @DisplayName("Clears stored data")
    public void clearDatabase() throws DataAccessException {
        // Setup DAO with test data
        auth.createAuth(new AuthData("testToken", "testUser"));
        games.createGame(new GameData(1234,
                "white",
                "black",
                "Test Game",
                new ChessGame()));
        users.createUser(new UserData("testUser","p@ssw0rd123","user@test.com"));

        // Execute method
        clearService.clear();

        // Verify empty DAOs
        verifyEmpty();
    }

    @Test
    @DisplayName("Succeeds with an empty database")
    public void clearEmptyDatabase() throws DataAccessException {
        // Execute method
        clearService.clear();

        // Verify empty DAOs
        verifyEmpty();
    }

    private void verifyEmpty(){
        ServiceTestUtils.verifyEmptyAuthDao(auth);
        ServiceTestUtils.verifyEmptyGameDao(games);
        ServiceTestUtils.verifyEmptyUserDao(users);
    }

}
