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
                ServiceUtils.NEW_CHESS_GAME,
                false);
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

    public void updateGame(int id, ChessGame game) throws DataAccessException {
        GameData old = games.getGame(id);
        if(!old.gameOver()){
            games.updateGame(new GameData(
                    id,
                    old.whiteUsername(),
                    old.blackUsername(),
                    old.gameName(),
                    ServiceUtils.serialize(game),
                    false));
        }
    }

    public void endGame(int id) throws DataAccessException{
        GameData old = games.getGame(id);
        if(!old.gameOver()){
            games.updateGame(new GameData(
                    id,
                    old.whiteUsername(),
                    old.blackUsername(),
                    old.gameName(),
                    old.game(),
                    true));
        }
    }

}
