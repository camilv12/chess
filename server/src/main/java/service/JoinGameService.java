package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.ram.RamAuthDao;
import dataaccess.ram.RamGameDao;
import model.GameData;
import service.model.JoinGameRequest;

import java.util.Set;

public class JoinGameService {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private static final Set<String> VALID_COLORS = Set.of("WHITE", "BLACK");

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        // Validate request
        if(ServiceUtils.isAnyBlank(request.authToken(), request.playerColor()) ||
                request.gameID() < 1 ||
                !VALID_COLORS.contains(request.playerColor())){
            throw new BadRequestException("Invalid Request");
        }
        authorize(auth, request.authToken());

        // Get requested game
        GameData game = games.getGame(request.gameID());

        // Validate player color
        String colorName = (request.playerColor().equals("WHITE")) ? game.whiteUsername() : game.blackUsername();
        if(colorName != null) { throw new AlreadyTakenException("Color is already taken"); }

        // Update game
        String username = auth.getAuth(request.authToken()).username();
        GameData newGame = updateGame(request.playerColor(), username, game);
        games.updateGame(newGame);
    }

    private GameData updateGame(String color, String username, GameData game){
        String newWhite = game.whiteUsername();
        String newBlack = game.blackUsername();

        if(color.equals("WHITE")) { newWhite = username; }
        else { newBlack = username; }

        return new GameData(game.gameID(), newWhite, newBlack, game.gameName(), game.game());

    }

    private void authorize(AuthDao authDao, String authToken){
        try{
            authDao.getAuth(authToken);
        } catch (DataAccessException e){
            throw new UnauthorizedException("Unauthorized request");
        }
    }
}