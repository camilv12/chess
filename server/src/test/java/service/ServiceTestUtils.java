package service;

import chess.ChessGame;

public class ServiceTestUtils {
    private ServiceTestUtils() {}
    public static final String NEW_CHESS_GAME;

    static {
        NEW_CHESS_GAME = ServiceUtils.serialize(new ChessGame());
    }
}
