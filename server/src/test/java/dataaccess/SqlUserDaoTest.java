package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDaoTest {
    private SqlUserDao users;

    @BeforeEach
    void setUp() throws DataAccessException {
        users = new SqlUserDao();
        SqlDaoTestUtility.clearTables();
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
        assertThrows(NotFoundException.class, () -> users.getUser(username));
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
        assertThrows(NotFoundException.class, () -> users.getUser("ghost"));
    }
}