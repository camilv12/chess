package service;
import dataaccess.DataAccessException;
import dataaccess.RamAuthDao;
import service.model.AuthRequest;

public class AuthService {
    private final RamAuthDao auth = new RamAuthDao();

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

    public void logout(AuthRequest request){
        try{
            auth.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Unauthorized Request");
        }
    }
}
