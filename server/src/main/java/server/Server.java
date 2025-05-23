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
        RegisterService registerService = new RegisterService();
        LoginService loginService = new LoginService();
        CreateGameService createGameService = new CreateGameService();
        ListGamesService listGamesService = new ListGamesService();
        JoinGameService joinGameService = new JoinGameService();

        // Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        LoginHandler loginHandler = new LoginHandler(loginService);
        LogoutHandler logoutHandler = new LogoutHandler(authService);
        CreateGameHandler createGameHandler = new CreateGameHandler(authService, createGameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(authService, listGamesService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);

        //This line initializes the server and can be removed once you have a functioning endpoint 
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
