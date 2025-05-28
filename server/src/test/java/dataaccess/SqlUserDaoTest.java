package dataaccess;

import dataaccess.sql.SqlUserDao;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDaoTest {
    private SqlUserDao users;

    @BeforeAll
    static void setupDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            users = new SqlUserDao(conn);
            SqlDaoTestUtility.clearTables();
        } catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    // Positive Tests

    @Test
    void testPositiveCreateUser() throws DataAccessException {
        // Set up
        String username = "testUser";
        UserData testUser = new UserData(username, "p@ssw0rd123", "user@test.com");

        // Execute
        users.createUser(testUser);

        // Assert
        UserData fetchedUser = users.getUser(username);
        assertEquals(testUser.username(),fetchedUser.username());
        assertEquals(testUser.email(),fetchedUser.email());
    }

    @Test
    void testPositiveGetUser() throws DataAccessException {
        // Set up
        String username = "user";
        UserData user = new UserData(username,"pass","user@mail.com");
        users.createUser(user);

        // Execute
        UserData fetchedUser = users.getUser(username);

        // Assert
        assertNotNull(fetchedUser);
        assertEquals(user.email(), fetchedUser.email());
    }

    @Test
    void testPositiveUsersClear() throws DataAccessException {
        // Set up
        String username = "dummy";
        UserData user = new UserData(username,"pass","test@mail.com");
        users.createUser(user);

        // Execute
        users.clear();

        // Assert
        assertThrows(DataAccessException.class, () -> users.getUser(username));
    }

    // Negative Tests

    @Test
    void testDuplicateCreateUser() throws DataAccessException{
        // Set up
        UserData user = new UserData("dupe","p@ss123","dupe@mail.com");
        users.createUser(user);

        // Assert
        assertThrows(DataAccessException.class, () -> users.createUser(user));
    }

    @Test
    void testNonExistentGetUser(){
        // Set up and assert
        assertThrows(DataAccessException.class, () -> users.getUser("ghost"));
    }
}