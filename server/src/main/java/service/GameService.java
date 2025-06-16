package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SqlGameDao;
import exception.BadRequestException;
import model.GameData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.ListGamesResult;


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

    public String getGame(int id){
        try{
            return games.getGame(id).game();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
