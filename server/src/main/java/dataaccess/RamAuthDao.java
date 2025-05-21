package dataaccess;

import model.AuthData;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class RamAuthDao implements AuthDao{
    private static final Map<String, AuthData> auth = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException{
        if(auth.containsKey(authData.authToken())){
            throw new DataAccessException("AuthToken already exists");
        }
        auth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData result = auth.get(authToken);
        if(result == null) throw new DataAccessException("AuthToken not found");
        return result;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if(!auth.containsKey(authToken)){
            throw new DataAccessException("AuthToken not found");
        }
        auth.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException{
        try{
            auth.clear();
        } catch(Exception e){
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }

    public boolean isEmpty() { return auth.isEmpty(); }
}
