package service;

import dataaccess.DataAccessException;
import dataaccess.SqlGameDao;
import model.GameData;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;
import service.model.ListGamesResult;


public class GameService {
    private final SqlGameDao games = new SqlGameDao();

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        if(ServiceUtils.isBlank(request.gameName())){
            throw new BadRequestException("Invalid Request");
        }
        GameData game = new GameData(
                0,
                null,
                null,
                request.gameName(),
                ServiceUtils.NEW_CHESS_GAME
        );
        int id = games.createGame(game);
        return new CreateGameResult(id);
    }

    public ListGamesResult listGames() throws DataAccessException {
        return new ListGamesResult(games.listGames());
    }

}
