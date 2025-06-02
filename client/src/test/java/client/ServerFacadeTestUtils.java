package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacadeTestUtils {
    private static String SERVER_URL;

    public static void setTestUrl(int port){
        SERVER_URL = "http://localhost:" + port;
    }

    // Test-only method to clear the server
    public static void clear() throws Exception {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(SERVER_URL + "/db")).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }



}
