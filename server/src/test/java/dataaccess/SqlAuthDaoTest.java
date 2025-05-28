package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDaoTest {
    private SqlAuthDao auth;

    @BeforeEach
    void setUp() throws DataAccessException{
        auth = new SqlAuthDao();
        SqlDaoTestUtility.clearTables();
    }

    // Positive Tests

    @Test
    void testPositiveCreateAuth() throws DataAccessException{
        // Set up
        String authToken = "test-token";
        String username = "testUser";
        AuthData testAuth = new AuthData(authToken, username);
        SqlDaoTestUtility.addUser(username);
        // Execute
        auth.createAuth(testAuth);

        // Assert
        AuthData fetchedAuth = auth.getAuth("test-token");
        assertEquals(testAuth.username(), fetchedAuth.username());
    }

    @Test
    void testPositiveGetAuth() throws DataAccessException {
        // Set up
        String username = "dummyUser";
        String authToken = "dummy-token";
        AuthData testAuth = new AuthData(authToken,username);
        SqlDaoTestUtility.addUser(username);
        auth.createAuth(testAuth);

        // Execute
        AuthData fetchedAuth = auth.getAuth(authToken);

        // Assert
        assertNotNull(fetchedAuth);
        assertEquals(testAuth.username(),fetchedAuth.username());
    }

    @Test
    void testPositiveDeleteAuth() throws DataAccessException {
        // Set up
        String authToken = "delete-this";
        String username = "delUser";
        AuthData dummyAuth = new AuthData(authToken,username);
        SqlDaoTestUtility.addUser(username);
        auth.createAuth(dummyAuth);

        // Execute
        auth.deleteAuth(authToken);

        // Assert
        assertThrows(NotFoundException.class, () -> auth.getAuth(authToken));
    }

    @Test
    void testPositiveClear() throws DataAccessException {
        // Set up
        String username = "clear-test";
        String authToken = "auth-token";
        SqlDaoTestUtility.addUser(username);
        auth.createAuth(new AuthData(authToken, "clear-test"));

        // Execute
        auth.clear();

        // Assert
        assertThrows(NotFoundException.class, () -> auth.getAuth(authToken));
    }

    // Negative Tests
    @Test
    void testCreateAuthDuplicateKey(){
        // Set up
        AuthData testAuth = new AuthData("duplicate-token","dupe");

        // Assert
        assertThrows(DataAccessException.class, () -> auth.createAuth(testAuth));
    }

    @Test
    void testGetAuthMissingAuthToken() {
        assertThrows(NotFoundException.class, () -> auth.getAuth("ghost-token"));
    }

    @Test
    void testDeleteAuthMissingAuthToken() throws DataAccessException {
        // Set up
        String authToken = "real-token";
        String username = "test";
        AuthData testAuth = new AuthData(authToken, username);
        SqlDaoTestUtility.addUser(username);
        auth.createAuth(testAuth);

        // Assert
        assertThrows(NotFoundException.class, () -> auth.deleteAuth("ghost-token"));
        assertDoesNotThrow(() -> auth.getAuth(authToken));
    }
}