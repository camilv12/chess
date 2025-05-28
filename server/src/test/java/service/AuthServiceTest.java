package service;

import dataaccess.DataAccessException;
import dataaccess.ram.RamAuthDao;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.AuthRequest;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private final RamAuthDao auth = new RamAuthDao();

    @BeforeEach
    void setUp() throws DataAccessException {
        authService = new AuthService();
        auth.clear();
    }

    // Positive test - Authenticate
    @Test
    @DisplayName("Authenticate with valid token succeeds")
    public void testAuthenticate() throws DataAccessException {
        // Setup
        String validToken = "valid-token-123";
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
        auth.createAuth(new AuthData(validToken,"testUser"));
        AuthRequest request = new AuthRequest(validToken);

        // Verify result
        assertDoesNotThrow(() ->
                authService.logout(request));

        // Verify data updated
        assertThrows(DataAccessException.class, () ->
                auth.getAuth(validToken));
    }

    // Negative test - Authentication
    @Test
    @DisplayName("Authenticate with incorrect token throws exception")
    public void testInvalidAuthenticate() {
        // Assert
        assertThrows(UnauthorizedException.class, () ->
                authService.authenticate(new AuthRequest("invalid-token")));
        // Verify data did not update
        ServiceTestUtils.verifyEmptyAuthDao(auth);
    }

    // Negative test - Logout

    @Test
    @DisplayName("Logout fails when token not found")
    public void testUnauthorizedLogout(){
        // Verify exception
        assertThrows(UnauthorizedException.class, () ->
                authService.logout(new AuthRequest("testToken")));
        // Verify data did not update
        ServiceTestUtils.verifyEmptyAuthDao(auth);
    }


}