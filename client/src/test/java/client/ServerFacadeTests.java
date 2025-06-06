package client;

import model.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;

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
        ServerFacadeTestUtils.setTestUrl(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // Register Tests
    @BeforeEach
    public void clearDatabase() throws Exception {
        ServerFacadeTestUtils.clear();
    }

    @Test
    public void testRegisterValid() throws Exception {
        // Set up and Execute
        var result = facade.register("player1", "password", "test@mail.com");

        // Assert
        assertNotNull(result);
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void testRegisterAlreadyTaken() throws Exception {
        // Setup
        registerTestUser();

        // Assert and Execute
        assertThrows(Exception.class, () ->
                facade.register("testUser","password","test@mail.com"));
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
        assertThrows(Exception.class, () -> facade.logout("fakeToken"));
    }

    // Login Tests

    @Test
    public void testLoginValidCredentials() throws Exception {
        // Set up
        var user = registerTestUser();
        String token = user.authToken();
        facade.logout(token);

        // Execute
        var result = facade.login("testUser","p@ssw0rd");

        // Assert
        assertNotEquals(token, result.authToken()); // Should return a different authToken
        assertEquals(user.username(), result.username());
    }

    @Test
    public void testUnauthorizedLogin() throws Exception{
        // Set up
        registerTestUser();

        // Assert and Execute
        assertThrows(Exception.class, () ->
            facade.login("testUser", "wrong"));
        assertThrows(Exception.class, () ->
                facade.login("wrongUser", "fake"));
    }

    // List Game Tests

    @Test
    public void testValidListGames() throws Exception{
        // Set up
        var token = registerTestUser().authToken();
        facade.createGame(token, "Game1");

        // Execute
        var result = facade.listGames(token);

        // Assert
        assertFalse(result.games().isEmpty());
    }

    @Test
    public void testUnauthorizedListGames(){
        // Assert and Execute
        assertThrows(Exception.class, () -> facade.listGames("wrong-token"));
    }

    // Create Game Tests

    @Test
    public void testValidCreateGames() throws Exception{
        // Set up
        var token = registerTestUser().authToken();

        // Execute
        var result = facade.createGame(token, "MyGame");

        // Assert
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void testUnauthorizedCreateGames(){
        // Execute and Assert
        assertThrows(Exception.class, () -> facade.createGame("wrong-token", "TestGame"));
    }

    // Join Game Tests

    @Test
    public void testValidJoinGame() throws Exception{
        // Set up
        var token = registerTestUser().authToken();
        int id = facade.createGame(token, "TestGame").gameID();

        // Assert and Execute
        assertDoesNotThrow(() -> facade.joinGame(token, "WHITE", id));
    }

    @Test
    public void testJoinGameTakenColor() throws Exception{
        // Set up
        var oldToken = registerTestUser().authToken();
        int id = facade.createGame(oldToken,"TestGame").gameID();
        facade.joinGame(oldToken, "WHITE", id); // Player takes white

        var token = facade.register("myUser","p4ss","user@mail.com").authToken();

        // Assert and Execute
        assertThrows(Exception.class, () ->
                facade.joinGame(token, "WHITE", id));
    }

    // Helper method
    private RegisterResult registerTestUser() throws Exception{
        return facade.register("testUser", "p@ssw0rd", "test@user.com");
    }

}
