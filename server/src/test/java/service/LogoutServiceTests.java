package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.LogoutRequest;
import service.model.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTests {
    private final RamAuthDao auth = new RamAuthDao();
    private LogoutService logoutService;

    @BeforeEach
    void setUp() throws DataAccessException {
        logoutService = new LogoutService();
        auth.clear();
    }

    // Positive test
    @Test
    @DisplayName("Successfully logs out")
    public void testLogout() throws DataAccessException {
        // Setup
        auth.createAuth(new AuthData("testToken","testUser"));
        LogoutRequest request = new LogoutRequest("testToken");
        LogoutResult result = logoutService.logout(request);

        // Verify result
        assertNotNull(result, "Should return LogoutResult object");

        // Verify data updated
        assertThrows(DataAccessException.class, () ->
                auth.getAuth(request.authToken()));
    }

    // Negative test: Token not found
    @Test
    @DisplayName("Logout fails when token not found")
    public void testUnauthorizedLogout(){
        // Verify exception
        assertThrows(UnauthorizedException.class, () ->
                logoutService.logout(new LogoutRequest("testToken")));
        // Verify data did not update
        ServiceTestUtils.verifyEmptyAuthDao(auth);
    }




}