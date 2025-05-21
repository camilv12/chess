package service;

import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import dataaccess.RamUserDao;
import model.AuthData;
import model.UserData;
import service.request.RegisterRequest;
import service.result.RegisterResult;

public class RegisterService {
    private final RamUserDao userDao = new RamUserDao();
    private final RamAuthDao authDao = new RamAuthDao();

    public RegisterResult register(RegisterRequest request) throws RuntimeException, DataAccessException {
        // Check if the request is valid
        if(isBlank(request.username()) || isBlank(request.password()) || isBlank(request.email())){
            throw new BadRequestException("Error: bad request");
        }

        // Check username availability
        if(userExists(request.username())) throw new AlreadyTakenException("Error: already taken");

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

    private boolean userExists(String username){
        try{
            userDao.getUser(username);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
