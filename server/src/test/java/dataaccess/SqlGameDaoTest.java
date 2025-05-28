package dataaccess;

import chess.ChessGame;
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
        int gameID = 1;
        GameData testGame = new GameData(
                gameID,
                null,
                null,
                "testGame",
                new ChessGame());

        // Execute
        games.createGame(testGame);

        // Assert
        GameData fetchedGame = games.getGame(gameID);
        assertEquals(testGame.gameName(), fetchedGame.gameName());
    }

    @Test
    void testPositiveGetGame() throws DataAccessException {
        // Set up
        int gameID = 1;
        GameData game = new GameData(
                gameID,
                null,
                null,
                "testGame",
                new ChessGame());
        games.createGame(game);

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
                1,
                null,
                null,
                "Game1",
                new ChessGame()
        ));
        SqlDaoTestUtility.addUser("whitePlayer");
        games.createGame(new GameData(
                2,
                "whitePlayer",
                null,
                "Game2",
                new ChessGame()
        ));
        SqlDaoTestUtility.addUser("blackPlayer");
        games.createGame(new GameData(
                3,
                null,
                "blackPlayer",
                "Game3",
                new ChessGame()
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
                1,
                null,
                null,
                "OldName",
                new ChessGame()
        );
        games.createGame(originalGame);
        SqlDaoTestUtility.addUser("whitePlayer");
        SqlDaoTestUtility.addUser("blackPlayer");
        GameData updatedGame = new GameData(
                1,
                "whitePlayer",
                "blackPlayer",
                "NewName",
                new ChessGame()
        );

        // Execute
        games.updateGame(updatedGame);

        // Assert
        GameData fetchedGame = games.getGame(1);
        assertEquals("NewName", fetchedGame.gameName());
        assertEquals("whitePlayer", fetchedGame.whiteUsername());
        assertEquals("blackPlayer", fetchedGame.blackUsername());
    }

    @Test
    void testClear() throws DataAccessException {
        // Set up
        games.createGame(new GameData(1,
                null,
                null,
                "ClearGame",
                new ChessGame())
        );

        // Execute
        games.clear();

        // Assert
        Collection<GameData> remainingGames = games.listGames();
        assertThrows(DataAccessException.class, () -> games.getGame(1));
        assertTrue(remainingGames.isEmpty());
    }

    // Negative tests
    @Test
    void testCreateDuplicateGame() throws DataAccessException {
        // Set up
        GameData game = new GameData(
                1,
                null,
                null,
                "DuplicateGame",
                new ChessGame()
        );

        games.createGame(game);
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
    void testUpdateGameInvalidID(){
        // Set up
        GameData fakeGame = new GameData(
                9999,
                null,
                null,
                "FakeGame",
                new ChessGame()
        );

        // Assert + Execute
        assertThrows(DataAccessException.class, () -> games.updateGame(fakeGame));
    }
}