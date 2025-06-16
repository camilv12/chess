package server.handler;

import com.google.gson.*;
import spark.Request;
import websocket.commands.JoinCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.lang.reflect.Type;

public final class JsonUtils {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UserGameCommand.class, new CommandDeserializer())
            .create();

    private static final Gson DEFAULT_GSON = new GsonBuilder().create();

    private record ErrorResponse(String message) {}

    private JsonUtils() {}

    // Deserialize request body to Java object
    public static<T> T fromJson(Request request, Class<T> type){
        return DEFAULT_GSON.fromJson(request.body(), type);
    }

    // Deserialize JSON string to Java object
    public static<T> T fromJson(String json, Class<T> type){
        return DEFAULT_GSON.fromJson(json, type);
    }

    // Serialize Java object to JSON string
    public static String toJson(Object object){
        return DEFAULT_GSON.toJson(object);
    }

    public static String errorResponse(String message) {
        return DEFAULT_GSON.toJson(new ErrorResponse(message));
    }

    public static UserGameCommand commandFromJson(String json) {
        return GSON.fromJson(json, UserGameCommand.class);
    }

    private static class CommandDeserializer implements JsonDeserializer<UserGameCommand> {
        @Override
        public UserGameCommand deserialize(JsonElement jsonElement, Type type,
                                           JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String typeStr = obj.get("commandType").getAsString();

            return switch (UserGameCommand.CommandType.valueOf(typeStr)) {
                case CONNECT -> jsonDeserializationContext.deserialize(jsonElement, JoinCommand.class);
                case MAKE_MOVE -> jsonDeserializationContext.deserialize(jsonElement, MakeMoveCommand.class);
                default -> jsonDeserializationContext.deserialize(jsonElement, UserGameCommand.class);
        };
    }
    }
}
