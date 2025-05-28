package service;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDao;
import dataaccess.SqlUserDao;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.model.*;

import java.util.UUID;

public class AuthService {
    private final SqlAuthDao auth = new SqlAuthDao();
    private final SqlUserDao users = new SqlUserDao();

    public void authenticate(AuthRequest request){
        try{
            if(ServiceUtils.isBlank(request.authToken())){
                throw new UnauthorizedException("Missing Authentication Token");
            }
            auth.getAuth(request.authToken());
        }catch (DataAccessException e){
            throw new UnauthorizedException("Unauthorized Request");
        }
    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        if(ServiceUtils.isAnyBlank(request.username(), request.password())){
            throw new BadRequestException("Invalid Request");
        }
        if(!userExists(request.username()) || !verifyPassword(request.username(), request.password())){
            throw new UnauthorizedException("Incorrect username or password");
        }
        AuthData newAuth = new AuthData(generateToken(), request.username());
        auth.createAuth(newAuth);
        return new LoginResult(request.username(), newAuth.authToken());
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if(ServiceUtils.isAnyBlank(request.username(), request.password(), request.email())){
            throw new BadRequestException("Invalid Request");
        }
        if(userExists(request.username())){
            throw new AlreadyTakenException("Username has already been taken");
        }
        users.createUser(new UserData(
                request.username(),
                hashPassword(request.password()),
                request.email()
        ));
        AuthData newAuth = new AuthData(generateToken(), request.username());
        auth.createAuth(newAuth);

        return new RegisterResult(request.username(), newAuth.authToken());
    }

    public void logout(AuthRequest request){
        try{
            auth.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Unauthorized Request");
        }
    }

    private boolean userExists(String username){
        try{
            users.getUser(username);
            return true;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    private boolean verifyPassword(String username, String plainTextPassword) throws DataAccessException {
        var hashedPassword = users.getUser(username).password();
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    private String generateToken(){ return UUID.randomUUID().toString(); }
}
