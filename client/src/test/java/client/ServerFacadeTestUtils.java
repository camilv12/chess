package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacadeTestUtils {
    private static String serverUrl;

    public static void setTestUrl(int port){
        serverUrl = "http://localhost:" + port;
    }

    // Test-only method to clear the server
    public static void clear() throws Exception {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverUrl + "/db")).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }



}
