package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamGameDao;
import dataaccess.RamUserDao;

public class ClearService {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamGameDao games = new RamGameDao();
    private final RamUserDao users = new RamUserDao();

    public void clear() throws DataAccessException {
        auth.clear();
        games.clear();
        users.clear();
    }
}
