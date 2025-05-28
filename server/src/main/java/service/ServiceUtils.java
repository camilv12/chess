package service;

import chess.ChessGame;
import com.google.gson.Gson;

public final class ServiceUtils {
    private ServiceUtils() {}

    public static final String NEW_CHESS_GAME = serialize(new ChessGame());

    public static boolean isBlank(String value){
        return value == null || value.isBlank();
    }

    public static boolean isAnyBlank(String... values) {
        for (String value : values) {
            if (isBlank(value)) { return true; }
        }
        return false;
    }

    public static String serialize(ChessGame game){
        if(game == null){
            return null;
        }
        return new Gson().toJson(game);
    }
}
