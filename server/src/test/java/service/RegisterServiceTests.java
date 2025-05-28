package service;

import dataaccess.DataAccessException;
import dataaccess.ram.RamAuthDao;
import dataaccess.ram.RamUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.model.RegisterRequest;
import service.model.RegisterResult;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {
    private final RamUserDao users = new RamUserDao();
    private final RamAuthDao auth = new RamAuthDao();
    private RegisterService registerService;

    @BeforeEach
    void setup() throws DataAccessException {
        registerService = new RegisterService();
        users.clear();
        auth.clear();
    }

    // Positive Test
    @Test
    @DisplayName("Successfully registers a user")
    void registerUser() throws DataAccessException, BadRequestException, AlreadyTakenException {
        // Setup:
        RegisterRequest request = new RegisterRequest("testUser","deadbeef","user@test.com");
        RegisterResult result = registerService.register(request);

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

    // Negative Test 1: Bad Request
    @ParameterizedTest(name = "Test null {0}")
    @MethodSource("badRequestsProvider")
    @DisplayName("Throws an exception when arguments are null")
    public void testRegistrationWithBadRequests(
            String description,
            String username,
            String password,
            String email
    ) {
        // Verify result
        assertThrows(BadRequestException.class, () ->
                registerService.register(new RegisterRequest(username, password, email)),
                "Failed validation for: " + description);

        // Verify no data persists
        ServiceTestUtils.verifyEmptyUserAndAuthDaos(users, auth);
    }
    private static Stream<Arguments> badRequestsProvider() {
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
        registerService.register(new RegisterRequest(
                "existing",
                "password1",
                "exist@test.com"));
        RegisterRequest duplicate = new RegisterRequest(
                "existing",
                "password2",
                "exist2@email.com");

        // Verify result
        assertThrows(AlreadyTakenException.class, () -> registerService.register(duplicate));

        // Verify original user remains
        UserData originalUser = users.getUser("existing");
        assertEquals("exist@test.com", originalUser.email());
    }

}
