package server;

import service.*;
import spark.*;
import server.handler.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Services
        AuthService authService = new AuthService();
        ClearService clearService = new ClearService();
        GameService gameService = new GameService();
        GameSessionService gameSessionService = new GameSessionService();

        // Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(authService);
        LoginHandler loginHandler = new LoginHandler(authService);
        LogoutHandler logoutHandler = new LogoutHandler(authService);
        CreateGameHandler createGameHandler = new CreateGameHandler(authService, gameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(authService, gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameSessionService);

        // Endpoints
        Spark.webSocket("/ws", ChessWebSocketHandler.class);
        Spark.delete("/db", clearHandler::handle);
        Spark.post("/user", registerHandler::handle);
        Spark.post("/session", loginHandler::handle);
        Spark.delete("/session", logoutHandler::handle);
        Spark.get("/game", listGamesHandler::handle);
        Spark.post("/game", createGameHandler::handle);
        Spark.put("/game", joinGameHandler::handle);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
