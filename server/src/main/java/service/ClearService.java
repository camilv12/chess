package service;

import dataaccess.DataAccessException;
import dataaccess.ram.RamAuthDao;
import dataaccess.ram.RamGameDao;
import dataaccess.ram.RamUserDao;

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
