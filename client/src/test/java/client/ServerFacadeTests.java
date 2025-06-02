package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.AlreadyTakenException;
import service.UnauthorizedException;
import service.model.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // Register Tests

    @Test
    public void testRegisterValidCredentials() throws Exception {
        // Set up and Execute
        var result = facade.register(new RegisterRequest(
                "player1",
                "password",
                "test@mail.com"));

        // Assert
        assertNotNull(result);
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void testRegisterAlreadyTaken() throws Exception {
        // Setup
        registerTestUser();

        // Assert and Execute
        assertThrows(AlreadyTakenException.class, () ->
                facade.register(new RegisterRequest(
                        "testUser",
                        "password",
                        "test@mail.com"
                )));
    }

    // Logout Tests

    @Test
    public void testValidLogout() throws Exception {
        // Set up
        var result = registerTestUser();

        // Assert and Execute
        assertDoesNotThrow(() -> facade.logout(result.authToken()));
    }

    @Test
    public void testInvalidLogout() {
        // Assert and Execute
        assertThrows(UnauthorizedException.class, () -> facade.logout("fakeToken"));
    }

    // Login Tests

    @Test
    public void testLoginValidCredentials() throws Exception {
        // Set up
        var user = registerTestUser();
        String token = user.authToken();
        facade.logout(token);

        // Execute
        var result = facade.login(new LoginRequest("testUser","p@ssw0rd"));

        // Assert
        assertNotEquals(token, result.authToken()); // Should return a different authToken
        assertEquals(user.username(), result.username());
    }

    @Test
    public void testUnauthorizedLogin() throws Exception{
        // Set up
        registerTestUser();

        // Assert and Execute
        assertThrows(UnauthorizedException.class, () ->
            facade.login(new LoginRequest("testUser", "wrong")));
        assertThrows(UnauthorizedException.class, () ->
                facade.login(new LoginRequest("wrongUser", "fake")));
    }

    // List Game Tests

    @Test
    public void testValidListGames() throws Exception{
        // Set up
        var token = registerTestUser().authToken();
        facade.createGame(token, new CreateGameRequest("Game1"));

        // Execute
        var result = facade.listGames(token);

        // Assert
        assertFalse(result.games().isEmpty());
    }

    @Test
    public void testUnauthorizedListGames(){
        // Assert and Execute
        assertThrows(UnauthorizedException.class, () -> facade.listGames("wrong-token"));
    }

    // Create Game Tests

    @Test
    public void testValidCreateGames() throws Exception{
        // Set up
        var token = registerTestUser().authToken();

        // Execute
        var result = facade.createGame(token, new CreateGameRequest("MyGame"));

        // Assert
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void testUnauthorizedCreateGames(){
        // Execute and Assert
        assertThrows(UnauthorizedException.class, () -> facade.createGame(
                "wrong-token",
                new CreateGameRequest("TestGame")));
    }

    // Join Game Tests

    @Test
    public void testValidJoinGame() throws Exception{
        // Set up
        var token = registerTestUser().authToken();
        int id = facade.createGame(token, new CreateGameRequest("TestGame")).gameID();

        // Assert and Execute
        assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(token, "WHITE", id)));
    }

    @Test
    public void testJoinGameTakenColor() throws Exception{
        // Set up
        var oldToken = registerTestUser().authToken();
        int id = facade.createGame(oldToken, new CreateGameRequest("TestGame")).gameID();
        facade.joinGame(new JoinGameRequest(oldToken, "WHITE", id)); // Player takes white

        var request = new RegisterRequest("myUser","p4ss","user@mail.com");
        var token = facade.register(request).authToken();

        // Assert and Execute
        assertThrows(AlreadyTakenException.class, () ->
                facade.joinGame(new JoinGameRequest(token, "WHITE", id)));
    }

    // Helper method
    private RegisterResult registerTestUser() throws Exception{
        return facade.register(new RegisterRequest(
                "testUser",
                "p@ssw0rd",
                "test@user.com"
        ));
    }

}
