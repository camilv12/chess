package service;

import chess.ChessGame;
import dataaccess.ram.RamAuthDao;
import dataaccess.ram.RamGameDao;
import dataaccess.ram.RamUserDao;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTestUtils {
    private ServiceTestUtils() {}
    public static final String NEW_CHESS_GAME = ServiceUtils.serialize(new ChessGame());

    public static void verifyEmptyAuthDao(RamAuthDao auth){
        assertTrue(auth.isEmpty(), "Auth DAO not empty");
    }
    public static void verifyEmptyGameDao(RamGameDao games){
        assertTrue(games.isEmpty(), "Game DAO not empty");
    }
    public static void verifyEmptyGameAndAuthDaos(RamGameDao games, RamAuthDao auth){
        verifyEmptyGameDao(games);
        verifyEmptyAuthDao(auth);
    }
}
