package server;

import service.*;
import spark.*;
import server.handler.*;

import static spark.Spark.webSocket;

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

        // Create WebSocket handler
        ChessWebSocketHandler chessWebSocketHandler = new ChessWebSocketHandler(
                authService,
                gameService,
                gameSessionService);

        // Register WebSocket endpoint
        Spark.webSocket("/connect", chessWebSocketHandler);

        // Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(authService);
        LoginHandler loginHandler = new LoginHandler(authService);
        LogoutHandler logoutHandler = new LogoutHandler(authService);
        CreateGameHandler createGameHandler = new CreateGameHandler(authService, gameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(authService, gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameSessionService);

        // Endpoints
        webSocket("/ws", chessWebSocketHandler);
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
