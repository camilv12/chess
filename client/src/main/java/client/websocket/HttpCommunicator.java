package client.websocket;

import com.google.gson.Gson;
import exception.HttpException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class HttpCommunicator {
    private final String root;
    private final Gson gson = new Gson();

    public HttpCommunicator(String root){
        this.root = root;
    }

    public <T> T post(String endpoint, String authToken, Object request, Class<T> responseClass) throws Exception{
        return makeRequest("POST", endpoint, authToken, request, responseClass);
    }

    public <T> T get(String endpoint, String authToken, Class<T> responseClass) throws Exception{
        return makeRequest("GET", endpoint, authToken, null, responseClass);
    }

    public <T> T put(String endpoint, String authToken, Object request, Class<T> responseClass) throws Exception{
        return makeRequest("PUT", endpoint, authToken, request, responseClass);
    }

    public void delete(String endpoint, String authToken) throws Exception {
        makeRequest("DELETE", endpoint, authToken, null, Void.class);
    }

    public <T> T makeRequest(String method, String endpoint, String authToken,
                             Object request, Class<T> responseClass) throws Exception {
        try{
            URI uri = new URI(root + endpoint);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", "application/json");

            // For requests with an authToken
            if (authToken != null) {
                connection.setRequestProperty("Authorization", authToken);
            }

            // For requests with a body
            if(request != null){
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(gson.toJson(request).getBytes());
                }
            }

            int responseCode = connection.getResponseCode();
            InputStream is = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            if (responseCode >= 400) {
                try (InputStreamReader reader = new InputStreamReader(is)) {
                    String errorBody = new BufferedReader(reader).lines().collect(Collectors.joining());
                    throw new HttpException(responseCode, errorBody);
                }
            }

            if (Void.class.equals(responseClass)) {
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(is)) {
                return gson.fromJson(reader, responseClass);
            }

        } catch (URISyntaxException | IOException e) {
            throw new Exception(e.getMessage());
        }
    }


}
