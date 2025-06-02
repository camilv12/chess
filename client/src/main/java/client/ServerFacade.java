
package client;
import com.google.gson.Gson;
import service.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

/**
 * A class that encapsulates HTTP requests to the server and results from the server.
 * This class handles the communication between the client and the server.
 */

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;
    }

    /**
     * Makes a login API call to the server using the login input from the client.
     * @return Result (LoginResult) from the API call to the server
     */
    public LoginResult login(String username, String password){
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a register API call to the server using the registration input from the client.
     * @return Result (RegisterResult) from the API call to the server
     */
    public RegisterResult register(String username, String password, String email) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a logout API call to the server based on client credentials
     */
    public void logout(String token) throws Exception{
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a game creation API call to the server based on the game creation input from the client
     * @return Result (CreateGameResult) from the API call to the server
     */
    public CreateGameResult createGame(String token, String name) throws Exception{
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a list game API call to the server based on client credentials
     * @return Result (ListGameResult) from the API call to the server
     */
    public ListGamesResult listGames(String token) throws Exception{
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a join game API call to the server based on input from the client.
     */
    public void joinGame(String token, String color, int id) throws Exception{
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes an HTTP request to the server and returns the deserialized response.
     *
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param path URL path (ex: "/game")
     * @param request Request body object (will be serialized to JSON). Use null for no body.
     * @param responseClass Class object for response type (ex: Response.class)
     * @return Deserialized response object of type T
     * @param <T> Type of expected response
     * @throws Exception if:
     *                   - Server returns non-200 code
     *                   - Network/Connection issues occur
     *                   - Response parsing fails
     */
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Write request body
            try (OutputStream os = http.getOutputStream()){
                String json = new Gson().toJson(request);
                os.write(json.getBytes());
            }

            // Handle response
            if (http.getResponseCode() >= 400){
                throw new Exception("HTTP Error: " + http.getResponseCode());
            }

            try (InputStream is = http.getInputStream()){
                String response = new String(is.readAllBytes());
                return new Gson().fromJson(response, responseClass);
            }
        } catch (IOException e){
            throw new Exception("Connection failed: " + e.getMessage());
        }
    }
}
