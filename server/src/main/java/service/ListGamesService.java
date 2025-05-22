package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamGameDao;
import service.model.ListGamesRequest;
import service.model.ListGamesResult;

public class ListGamesService {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        if(ServiceUtils.isBlank(request.authToken())){
            throw new BadRequestException("Missing authToken");
        }
        ServiceUtils.authorize(auth, request.authToken());
        return new ListGamesResult(games.listGames());
    }
}
