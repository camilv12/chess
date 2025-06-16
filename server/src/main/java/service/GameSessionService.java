package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.GameData;
import model.JoinGameRequest;

import java.util.Set;

public class GameSessionService {
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

    public void leaveGame(String authToken, int id) throws DataAccessException {
        // Validate request
        authorize(auth, authToken);

        // Get requested game
        GameData game = games.getGame(id);

        // Remove user
        String username = auth.getAuth(authToken).username();
        removePlayerFromGame(username, game);
    }

    public void resignGame(String username, int gameID) throws DataAccessException {
        GameData game = games.getGame(gameID);

        // Only allow active players to resign
        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            throw new IllegalArgumentException("Only players can resign");
        }

        GameData updated = new GameData(gameID,
                null,
                null,
                game.gameName(),
                game.game(),
                true);
        games.updateGame(updated);
    }

    private void validateRequest(JoinGameRequest request) throws BadRequestException {
        boolean badToken = ServiceUtils.isBlank(request.authToken());
        boolean badId = request.gameID() == null || request.gameID() < 0;
        boolean badColor = request.playerColor() == null || !VALID_COLORS.contains(request.playerColor());

        if(badToken || badColor || badId){
            throw new BadRequestException("Invalid Request");
        }
    }

    private void removePlayerFromGame(String username, GameData game) throws DataAccessException {
        String newWhite = username.equals(game.whiteUsername()) ? game.whiteUsername() : null;
        String newBlack = username.equals(game.blackUsername()) ? game.blackUsername() : null;
        games.updateGame(new GameData(game.gameID(), newWhite, newBlack, game.gameName(), game.game(), false));

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

        return new GameData(game.gameID(), newWhite, newBlack, game.gameName(), game.game(), false);
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