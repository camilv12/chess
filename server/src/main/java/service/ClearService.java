package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamGameDao;
import dataaccess.RamUserDao;
import service.model.ClearRequest;
import service.model.ClearResult;

public class ClearService {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private final RamUserDao users = new RamUserDao();

    public ClearResult clear(ClearRequest request) throws DataAccessException {
        if (request == null){
            throw new IllegalArgumentException("Request cannot be null");
        }
        auth.clear();
        games.clear();
        users.clear();
        return new ClearResult();
    }
}
