package dataaccess;

import dataaccess.sql.SqlAuthDao;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDaoTest {
    private SqlAuthDao auth;

    @BeforeAll
    static void setupDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
    }

    @BeforeEach
    void setUp() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            auth = new SqlAuthDao(conn);
            SqlDaoTestUtility.clearTables();
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    // Positive Tests

    @Test
    void testPositiveCreateAuth() throws DataAccessException{
        // Set up
        String authToken = "test-token";
        AuthData testAuth = new AuthData(authToken, "testUser");

        // Execute
        auth.createAuth(testAuth);

        // Assert
        AuthData fetchedAuth = auth.getAuth("test-token");
        assertEquals(testAuth.username(), fetchedAuth.username());
    }

    @Test
    void testPositiveGetAuth() throws DataAccessException {
        // Set up
        String authToken = "dummy-token";
        AuthData testAuth = new AuthData(authToken,"dummyUser");
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
        AuthData dummyAuth = new AuthData(authToken,"delUser");
        auth.createAuth(dummyAuth);

        // Execute
        auth.deleteAuth(authToken);

        // Assert
        assertThrows(DataAccessException.class, () -> auth.getAuth(authToken));
    }

    @Test
    void testPositiveClear() throws DataAccessException {
        // Set up
        String authToken = "auth-token";
        auth.createAuth(new AuthData(authToken, "clear-test"));

        // Execute
        auth.clear();

        // Assert
        assertThrows(DataAccessException.class, () -> auth.getAuth(authToken));
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
        assertThrows(DataAccessException.class, () -> auth.getAuth("ghost-token"));
    }

    @Test
    void testDeleteAuthMissingAuthToken() {
        assertThrows(DataAccessException.class, () -> auth.deleteAuth("ghost-token"));
    }
}