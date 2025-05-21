package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamGameDao;
import dataaccess.RamUserDao;
import service.model.ClearResult;

public class ClearService {
    private final RamAuthDao ramAuthDao = new RamAuthDao();
    private final RamGameDao ramGameDao = new RamGameDao();
    private final RamUserDao ramUserDao = new RamUserDao();

    public ClearResult clear() throws DataAccessException {
        ramAuthDao.clear();
        ramGameDao.clear();
        ramUserDao.clear();
        return new ClearResult();
    }
}
