package service;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDao;
import dataaccess.SqlGameDao;
import dataaccess.SqlUserDao;

public class ClearService {
    private final SqlAuthDao auth = new SqlAuthDao();
    private final SqlGameDao games = new SqlGameDao();
    private final SqlUserDao users = new SqlUserDao();

    public void clear() throws DataAccessException {
        auth.clear();
        games.clear();
        users.clear();
    }
}
