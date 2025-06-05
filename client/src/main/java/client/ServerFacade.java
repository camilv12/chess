package client;
import com.google.gson.Gson;
import client.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.net.http.*;

/**
 * A class that encapsulates HTTP requests to the server and results from the server.
 * This class handles the communication between the client and the server.
 */

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient;

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Makes a call to the server login API using the login input from the client.
     * @return Result (LoginResult) from the API call to the server.
     */
    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", null, request, LoginResult.class);
    }

    /**
     * Makes a call to the server register API using the registration input from the client.
     * @return Result (RegisterResult) from the API call to the server.
     */
    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", null, request, RegisterResult.class);
    }

    /**
     * Makes a call to the server logout API based on client credentials
     */
    public void logout(String token) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + "/session"))
                .header("authorization", token)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        checkStatus(response.statusCode());
    }

    /**
     * Makes a call to the server game creation API based on the game creation input from the client
     * @return Result (CreateGameResult) from the API call to the server.
     */
    public CreateGameResult createGame(String token, String name) throws Exception{
        CreateGameRequest request = new CreateGameRequest(name);
        return makeRequest("POST", "/game", token, request, CreateGameResult.class);
    }

    /**
     * Makes a call to the server list API based on client credentials
     * @return Result (ListGameResult) from the API call to the server
     */
    public ListGamesResult listGames(String token) throws Exception{
        return makeRequest("GET", "/game", token, null, ListGamesResult.class);
    }

    /**
     * Makes a call to the server join game API based on input from the client.
     */
    public void joinGame(String token, String color, int id) throws Exception{
        JoinGameRequest request = new JoinGameRequest(token, color, id);
        makeRequest("PUT", "/game", token, request, null);
    }

    /**
     * Makes an HTTP request to the server and returns the deserialized response.
     *
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param path URL path (ex: "/game")
     * @param token AuthToken. Use null for no token.
     * @param request Request body object (will be serialized to JSON). Use null for no body.
     * @param responseClass Class object for response type (ex: Response.class)
     * @return Deserialized response object of type T
     * @param <T> Type of expected response
     * @throws Exception if:
     *                   - Server returns non-200 code
     *                   - Network/Connection issues occur
     *                   - Response parsing fails
     */
    private <T> T makeRequest(String method, String path, String token, Object request, Class<T> responseClass) throws Exception {
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(token != null && !token.isBlank()){
                http.setRequestProperty("authorization", token);
            }

            // Write request body
            if(request != null){
                try (OutputStream os = http.getOutputStream()){
                    String json = new Gson().toJson(request);
                    os.write(json.getBytes());
                }
            }

            // Handle response
            if (http.getResponseCode() >= 400){
                checkStatus(http.getResponseCode());
            }

            if(responseClass == null){
                return null;
            }

            try (InputStream is = http.getInputStream()){
                String response = new String(is.readAllBytes());
                return new Gson().fromJson(response, responseClass);
            }
        } catch (IOException e){
            throw new Exception("Error: Connection failed");
        }
    }

    private void checkStatus(int code) throws Exception {
        switch(code){
            case 400 -> throw new Exception("Error: Invalid Request");
            case 401 -> throw new Exception("Error: Unauthorized");
            case 403 -> throw new Exception("Error: Already Taken");
            case 500 -> throw new Exception("Error: Connection failed");
        }
    }
}
