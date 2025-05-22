package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamGameDao;
import model.GameData;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;

import java.util.Collection;
import java.util.Objects;

public class CreateGameService {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        if(ServiceUtils.isAnyBlank(request.authToken(), request.gameName())){
            throw new BadRequestException("Invalid Request");
        }
        ServiceUtils.authorize(auth, request.authToken());

        int gameID = generateGameID(request.gameName(), games);
        GameData game = new GameData(
                gameID,
                null,
                null,
                request.gameName(),
                new ChessGame()
        );
        games.createGame(game);

        return new CreateGameResult(gameID);
    }

    private int generateGameID(String name, RamGameDao games) throws DataAccessException {
        int gameID;
        Collection<Integer> usedIds = games.getGameIds();
        do{
            gameID = Objects.hash(name, System.currentTimeMillis()) & 0x7FFFFFFF; // Always positive
        } while(usedIds.contains(gameID)); // Avoid collision

        return gameID;
    }
}
