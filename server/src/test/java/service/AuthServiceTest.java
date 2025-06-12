package service;

import dataaccess.DataAccessException;
import dataaccess.NotFoundException;
import dataaccess.SqlAuthDao;
import dataaccess.SqlUserDao;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mindrot.jbcrypt.BCrypt;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private final SqlAuthDao auth = new SqlAuthDao();
    private final SqlUserDao users = new SqlUserDao();

    @BeforeEach
    void setUp() throws DataAccessException {
        authService = new AuthService();
        auth.clear();
        users.clear();
    }

    // Positive test - Authenticate
    @Test
    @DisplayName("Authenticate with valid token succeeds")
    public void testAuthenticate() throws DataAccessException {
        // Setup
        String validToken = "valid-token-123";
        users.createUser(new UserData("test","passw0rd","user@email.com"));
        auth.createAuth(new AuthData(validToken, "test"));

        // Verify
        assertDoesNotThrow(() ->
                authService.authenticate(new AuthRequest(validToken)));
    }

    // Positive Test - Logout
    @Test
    @DisplayName("Successfully logs out")
    public void testLogout() throws DataAccessException {
        // Setup
        String validToken = "testToken";
        users.createUser(new UserData("testUser","pass","test@mail.com"));
        auth.createAuth(new AuthData(validToken,"testUser"));
        AuthRequest request = new AuthRequest(validToken);

        // Verify result
        assertDoesNotThrow(() ->
                authService.logout(request));

        // Verify data updated
        assertThrows(NotFoundException.class, () ->
                auth.getAuth(validToken));
    }

    // Positive test - Login
    @Test
    @DisplayName("Logs in user with correct username and password")
    public void testLogin() throws DataAccessException {
        // Setup
        String password = hash("passw0rd");
        users.createUser(new UserData("testUser",password,"user@email.com"));
        LoginRequest request = new LoginRequest("testUser", "passw0rd");
        LoginResult result = authService.login(request);

        // Verify result
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());

        // Verify auth storage
        AuthData authData = auth.getAuth(result.authToken());
        assertEquals("testUser", authData.username());
    }

    // Positive Test - Register
    @Test
    @DisplayName("Successfully registers a user")
    void registerUser() throws DataAccessException, BadRequestException, AlreadyTakenException {
        // Setup:
        RegisterRequest request = new RegisterRequest("testUser","deadbeef","user@test.com");
        RegisterResult result = authService.register(request);

        // Verify result:
        assertNotNull(result.authToken(), "AuthToken not found");
        assertEquals("testUser", result.username());

        // Verify user storage
        UserData storedUser = users.getUser("testUser");
        assertEquals("user@test.com", storedUser.email());

        // Verify auth storage
        AuthData auth = this.auth.getAuth(result.authToken());
        assertEquals("testUser", auth.username());
    }

    // Negative test - Authentication
    @Test
    @DisplayName("Authenticate with incorrect token throws exception")
    public void testInvalidAuthenticate() {
        // Assert
        assertThrows(UnauthorizedException.class, () ->
                authService.authenticate(new AuthRequest("invalid-token")));
        // Verify data did not update

    }

    // Negative test - Logout
    @Test
    @DisplayName("Logout fails when token not found")
    public void testUnauthorizedLogout() {
        // Assert + Execute
        assertThrows(UnauthorizedException.class, () -> authService.logout(new AuthRequest("testToken")));

    }

    // Negative Test: Login Bad Request
    @ParameterizedTest(name = "Test null {0}")
    @MethodSource("badLoginRequestsProvider")
    @DisplayName("Throws an exception when arguments are null")
    public void testLoginWithBadRequests(
            String description,
            String username,
            String password
    ) {
        // Verify result
        assertThrows(BadRequestException.class, () ->
                        authService.login(new LoginRequest(username, password)),
                "Failed validation for: " + description);

        // Verify no data persists
        assertThrows(NotFoundException.class, ()-> users.getUser("testUser"));
    }
    private static Stream<Arguments> badLoginRequestsProvider() {
        String validUser = "testUser";
        String validPass = "passw0rd";

        return Stream.of(
                Arguments.of("username", null, validPass),
                Arguments.of("password", validUser, null)
        );
    }

    // Negative Test: Login Incorrect Username or Password
    @Test
    @DisplayName("Incorrect username or password does not log in")
    public void testIncorrectUsernameOrPassword() throws DataAccessException {
        // Setup
        String password = hash("testPass");
        users.createUser(new UserData("testUser",password,"user@test.com"));

        // Verify exceptions
        assertThrows(UnauthorizedException.class, () ->
                authService.login(new LoginRequest("fakeUser","testPass")));
        assertThrows(UnauthorizedException.class, () ->
                authService.login(new LoginRequest("testUser","fakePass")));

        // Verify data did not update
        assertThrows(NotFoundException.class, () -> users.getUser("fakeUser"));
    }

    // Negative Test: Bad Registration Request
    @ParameterizedTest(name = "Test null {0}")
    @MethodSource("badRegistrationRequestsProvider")
    @DisplayName("Throws an exception when arguments are null")
    public void testRegistrationWithBadRequests(
            String description,
            String username,
            String password,
            String email
    ) {
        // Verify result
        assertThrows(BadRequestException.class, () ->
                        authService.register(new RegisterRequest(username, password, email)),
                "Failed validation for: " + description);

        // Verify no data persists
        assertThrows(NotFoundException.class, () -> users.getUser("testUser"));
    }
    private static Stream<Arguments> badRegistrationRequestsProvider() {
        String validUser = "testUser";
        String validPass = "passw0rd";
        String validEmail = "user@test.com";

        return Stream.of(
                Arguments.of("username", null, validPass, validEmail),
                Arguments.of("password", validUser, null, validEmail),
                Arguments.of("email", validUser, validPass, null)
        );
    }

    // Negative Test 2: Already Taken
    @Test
    @DisplayName("Duplicate registration throws exception")
    public void registerDuplicateUser() throws DataAccessException, BadRequestException, AlreadyTakenException {
        // Setup
        authService.register(new RegisterRequest(
                "existing",
                "password1",
                "exist@test.com"));
        RegisterRequest duplicate = new RegisterRequest(
                "existing",
                "password2",
                "exist2@email.com");

        // Verify result
        assertThrows(AlreadyTakenException.class, () -> authService.register(duplicate));

        // Verify original user remains
        UserData originalUser = users.getUser("existing");
        assertEquals("exist@test.com", originalUser.email());
    }

    private String hash(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

}