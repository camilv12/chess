package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.model.LoginRequest;
import service.model.LoginResult;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTests {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamUserDao users = new RamUserDao();
    private LoginService loginService;

    @BeforeEach
    void setUp() throws DataAccessException {
        loginService = new LoginService();
        auth.clear();
        users.clear();
    }
    // Positive Test
    @Test
    @DisplayName("Logs in user with correct username and password")
    public void testLogin() throws DataAccessException {
        // Setup
        users.createUser(new UserData("testUser","passw0rd","user@email.com"));
        LoginRequest request = new LoginRequest("testUser", "passw0rd");
        LoginResult result = loginService.login(request);

        // Verify result
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());

        // Verify auth storage
        AuthData authData = auth.getAuth(result.authToken());
        assertEquals("testUser", authData.username());

    }

    // Negative Test 1: Bad Request
    @ParameterizedTest(name = "Test null {0}")
    @MethodSource("badRequestsProvider")
    @DisplayName("Throws an exception when arguments are null")
    public void testLoginWithBadRequests(
            String description,
            String username,
            String password
    ) {
        // Verify result
        assertThrows(BadRequestException.class, () ->
                        loginService.login(new LoginRequest(username, password)),
                "Failed validation for: " + description);

        // Verify no data persists
        ServiceTestUtils.verifyEmptyUserAndAuthDaos(users, auth);
    }
    private static Stream<Arguments> badRequestsProvider() {
        String validUser = "testUser";
        String validPass = "passw0rd";

        return Stream.of(
                Arguments.of("username", null, validPass),
                Arguments.of("password", validUser, null)
        );
    }

    // Negative Test 2: Incorrect Username or Password
    @Test
    @DisplayName("Incorrect username or password does not log in")
    public void testIncorrectUsernameOrPassword() throws DataAccessException {
        // Setup
        users.createUser(new UserData("testUser","testPass","user@test.com"));

        // Verify exceptions
        assertThrows(UnauthorizedException.class, () ->
                loginService.login(new LoginRequest("fakeUser","testPass")));
        assertThrows(UnauthorizedException.class, () ->
                loginService.login(new LoginRequest("testUser","fakePass")));

        // Verify data did not update
        assertThrows(DataAccessException.class, () ->
                users.getUser("fakeUser"));
    }
}