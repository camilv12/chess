package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlGameDaoTest {
    private static SqlGameDao games;

    @BeforeEach
    void setUp() throws DataAccessException{
        games = new SqlGameDao();
        SqlDaoTestUtility.clearTables();
    }

    @Test
    void testPositiveCreateGame() throws DataAccessException {
        // Set up
        GameData testGame = new GameData(
                0,
                null,
                null,
                "testGame",
                SqlDaoTestUtility.NEW_CHESS_GAME);

        // Execute
        int id = games.createGame(testGame);

        // Assert
        GameData fetchedGame = games.getGame(id);
        assertEquals(testGame.gameName(), fetchedGame.gameName());
    }

    @Test
    void testPositiveGetGame() throws DataAccessException {
        // Set up
        GameData game = new GameData(
                0,
                null,
                null,
                "testGame",
                SqlDaoTestUtility.NEW_CHESS_GAME);
        int gameID = games.createGame(game);

        // Execute
        GameData fetchedGame = games.getGame(gameID);

        // Assert
        assertNotNull(fetchedGame);
        assertEquals(game.gameName(), fetchedGame.gameName());
    }

    @Test
    void testPositiveListGames() throws DataAccessException{
        // Set up
        games.createGame(new GameData(
                0,
                null,
                null,
                "Game1",
                SqlDaoTestUtility.NEW_CHESS_GAME
        ));
        SqlDaoTestUtility.addUser("whitePlayer");
        games.createGame(new GameData(
                0,
                "whitePlayer",
                null,
                "Game2",
                SqlDaoTestUtility.NEW_CHESS_GAME
        ));
        SqlDaoTestUtility.addUser("blackPlayer");
        games.createGame(new GameData(
                0,
                null,
                "blackPlayer",
                "Game3",
                SqlDaoTestUtility.NEW_CHESS_GAME
        ));

        // Execute
        Collection<GameData> gamesList = games.listGames();

        // Assert
        assertEquals(3, gamesList.size());
    }

    @Test
    void testPositiveUpdateGame() throws DataAccessException {
        // Set up
        GameData originalGame = new GameData(
                0,
                null,
                null,
                "test",
                SqlDaoTestUtility.NEW_CHESS_GAME
        );
        int id = games.createGame(originalGame);
        SqlDaoTestUtility.addUser("whitePlayer");
        SqlDaoTestUtility.addUser("blackPlayer");
        GameData updatedGame = new GameData(
                id,
                "whitePlayer",
                "blackPlayer",
                "test",
                SqlDaoTestUtility.NEW_CHESS_GAME
        );

        // Execute
        games.updateGame(updatedGame);

        // Assert
        GameData fetchedGame = games.getGame(id);
        assertEquals("whitePlayer", fetchedGame.whiteUsername());
        assertEquals("blackPlayer", fetchedGame.blackUsername());
    }

    @Test
    void testClear() throws DataAccessException {
        // Set up
        int id = games.createGame(new GameData(0,
                null,
                null,
                "ClearGame",
                SqlDaoTestUtility.NEW_CHESS_GAME
        ));

        // Execute
        games.clear();

        // Assert
        Collection<GameData> remainingGames = games.listGames();
        assertThrows(DataAccessException.class, () -> games.getGame(id));
        assertTrue(remainingGames.isEmpty());
    }

    // Negative tests
    @Test
    void testCreateGameWithNullGame() {
        // Set up
        GameData game = new GameData(
                0,
                null,
                null,
                "DuplicateGame",
                null
        );
        // Assert + Execute
        assertThrows(DataAccessException.class, () -> games.createGame(game));
    }

    @Test
    void testGetGameNotFound(){
        // Assert + Execute
        assertThrows(DataAccessException.class, () -> games.getGame(9999));
    }

    @Test
    void testListGamesOnEmptyDatabase() throws DataAccessException {
        // Execute
        Collection<GameData> gamesList = games.listGames();

        // Assert
        assertNotNull(gamesList);
        assertTrue(gamesList.isEmpty());
    }

    @Test
    void testUpdateGameInvalidID() throws DataAccessException {
        // Set up
        GameData fakeGame = new GameData(
                9999,
                null,
                null,
                "FakeGame",
                ""
        );
        games.updateGame(fakeGame);
        // Assert + Execute
        assertThrows(DataAccessException.class, () -> games.getGame(9999));
    }
}