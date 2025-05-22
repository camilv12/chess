package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamUserDao;
import model.AuthData;
import model.UserData;
import service.model.RegisterRequest;
import service.model.RegisterResult;

public class RegisterService {
    private final RamUserDao userDao = new RamUserDao();
    private final RamAuthDao authDao = new RamAuthDao();

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        // Check if the request is valid
        if(ServiceUtils.isBlank(request.username())
                || ServiceUtils.isBlank(request.password())
                || ServiceUtils.isBlank(request.email())) {
            throw new BadRequestException("Invalid Request");
        }

        // Check username availability
        if(ServiceUtils.userExists(userDao, request.username())){
            throw new AlreadyTakenException("Username has already been taken");
        }

        UserData newUser = new UserData(
                request.username(),
                request.password(),
                request.email()
        );
        userDao.createUser(newUser);

        AuthData newAuth = new AuthData(
                RamAuthDao.generateToken(),
                request.username()
        );
        authDao.createAuth(newAuth);

        return new RegisterResult(
                request.username(),
                newAuth.authToken());
    }
}
