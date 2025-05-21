package service.test;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.RegisterService;
import service.request.RegisterRequest;
import service.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {
    private RamUserDao userDao;
    private RamAuthDao authDao;
    private RegisterService registerService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDao = new RamUserDao();
        authDao = new RamAuthDao();
        registerService = new RegisterService();
        userDao.clear();
        authDao.clear();
    }

    // Positive Test
    @Test
    @DisplayName("RegisterService registers a user in the database")
    void registerUser() throws DataAccessException{
        // Setup:
        RegisterRequest request = new RegisterRequest("testUser","deadbeef","user@test.com");
        RegisterResult result = registerService.register(request);

        // Verify result:
        assertNotNull(result.authToken(), "AuthToken not found");
        assertEquals("testUser", result.username());

        // Verify user storage
        UserData storedUser = userDao.getUser("testUser");
        assertEquals("user@test.com", storedUser.email());

        // Verify auth storage
        AuthData auth = authDao.getAuth(result.authToken());
        assertEquals("testUser", auth.username());
    }

    // Negative Test 1: Bad Request
    @Test
    @DisplayName("RegisterService with bad request throws BadRequestException")
    public void registerBadRequest() {
        // Setup
        RegisterRequest badRequest = new RegisterRequest("user", "pass","email");

        // Verify result
        assertThrows(BadRequestException.class, () -> {
           registerService.register(badRequest);
        });

        // Verify no data persists
        assertThrows(DataAccessException.class, () -> userDao.getUser("user"));
        assertThrows(DataAccessException.class, () -> authDao.getAuth("anyToken"));
    }

    // Negative Test 2: Already Taken
    @Test
    @DisplayName("Duplicate registration throws AlreadyTakenException")
    public void registerDuplicateUser() throws DataAccessException {
        // Setup
        registerService.register(new RegisterRequest(
                "existing",
                "password1",
                "exist@test.com"));
        RegisterRequest duplicate = new RegisterRequest(
                "existing",
                "password2",
                "exist2@email.com");

        // Verify result
        assertThrows(AlreadyTakenException.class, () -> {
           registerService.register(duplicate);
        });

        // Verify original user remains
        UserData originalUser = userDao.getUser("existing");
        assertEquals("exist@test.com", originalUser.email());
    }

}
