package service;

import dataaccess.*;
import model.GameData;
import model.JoinGameRequest;

import java.util.Set;

public class JoinGameService {
    private final SqlAuthDao auth = new SqlAuthDao();
    private final SqlGameDao games = new SqlGameDao();
    private static final Set<String> VALID_COLORS = Set.of("WHITE", "BLACK","OBSERVE");

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        // Validate request
        validateRequest(request);
        authorize(auth, request.authToken());

        // Get requested game
        GameData game = games.getGame(request.gameID());

        // Validate player color
        if(isPlayerJoinRequest(request)) {
            handlePlayerJoin(request, game);
        }
    }

    private void validateRequest(JoinGameRequest request) throws BadRequestException{
        boolean badToken = ServiceUtils.isBlank(request.authToken());
        boolean badId = request.gameID() == null || request.gameID() < 0;
        boolean badColor = request.playerColor() == null || !VALID_COLORS.contains(request.playerColor());

        if(badToken || badColor || badId){
            throw new BadRequestException("Invalid Request");
        }
    }

    private void handlePlayerJoin(JoinGameRequest request, GameData game) throws DataAccessException {
        // Check if the name is null
        String colorName = request.playerColor().equals("WHITE") ? game.whiteUsername() : game.blackUsername();
        if (colorName != null) {
            throw new AlreadyTakenException("Color is already taken");
        }

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

    private void authorize(AuthDao authDao, String authToken) throws DataAccessException{
        try{
            authDao.getAuth(authToken);
        } catch (NotFoundException e){
            throw new UnauthorizedException("Unauthorized request");
        }
    }

    private boolean isPlayerJoinRequest(JoinGameRequest request){
        return !request.playerColor().equals("OBSERVE");
    }
}