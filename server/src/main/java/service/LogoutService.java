package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import service.model.LogoutRequest;
import service.model.LogoutResult;

public class LogoutService {
    private final RamAuthDao auth = new RamAuthDao();
    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        ServiceUtils.authorize(auth,request.authToken());
        auth.deleteAuth(request.authToken());
        return new LogoutResult();
    }
}
