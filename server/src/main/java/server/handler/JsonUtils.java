package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;

public final class JsonUtils {
    private static final Gson GSON = new GsonBuilder().create();
    private record ErrorResponse(String message) {}

    private JsonUtils() {}

    // Deserialize request body to Java object
    public static<T> T fromJson(Request request, Class<T> type){
        return GSON.fromJson(request.body(), type);
    }

    // Deserialize JSON string to Java object
    public static<T> T fromJson(String json, Class<T> type){
        return GSON.fromJson(json, type);
    }

    // Serialize Java object to JSON string
    public static String toJson(Object object){
        return GSON.toJson(object);
    }

    public static String errorResponse(String message) {
        return GSON.toJson(new ErrorResponse(message));
    }
}
