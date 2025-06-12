package client.websocket;

import com.google.gson.Gson;
import exception.HttpException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

public class HttpCommunicator {
    private final String root;
    private final Gson gson = new Gson();

    public HttpCommunicator(String root){
        this.root = root;
    }

    public <T> T post(String endpoint, Object request, Class<T> responseClass) throws Exception{
        try{
            URI uri = new URI(root + endpoint);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(gson.toJson(request).getBytes());
            }

            int responseCode = connection.getResponseCode();
            InputStream is = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            if (responseCode >= 400) {
                try (InputStreamReader reader = new InputStreamReader(is)) {
                    String errorBody = new BufferedReader(reader).lines().collect(Collectors.joining());
                    throw new HttpException(responseCode, errorBody);
                }
            }

            try (InputStreamReader reader = new InputStreamReader(is)) {
                return gson.fromJson(reader, responseClass);
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }


}
