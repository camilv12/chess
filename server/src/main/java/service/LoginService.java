package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamUserDao;
import model.AuthData;
import model.UserData;
import service.model.LoginRequest;
import service.model.LoginResult;

public class LoginService {
    private final RamAuthDao auth = new RamAuthDao();
    private final RamUserDao users = new RamUserDao();

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if(ServiceUtils.isAnyBlank(request.username(), request.password())){
            throw new BadRequestException("Invalid request");
        }
        if(!ServiceUtils.userExists(users, request.username())){
            throw new UnauthorizedException("Incorrect username or password");
        }
        UserData user = users.getUser(request.username());
        if(!user.password().equals(request.password())){
            throw new UnauthorizedException("Incorrect username or password");
        }

        AuthData newAuth = new AuthData(
                RamAuthDao.generateToken(),
                request.username()
        );
        auth.createAuth(newAuth);

        return new LoginResult(request.username(), newAuth.authToken());
    }
}
