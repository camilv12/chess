package dataaccess;

import model.AuthData;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class RamAuthDao implements AuthDao{
    private static final Map<String, AuthData> AUTH_DATA_MAP = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException{
        if(AUTH_DATA_MAP.containsKey(authData.authToken())){
            throw new DataAccessException("AuthToken already exists");
        }
        AUTH_DATA_MAP.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData result = AUTH_DATA_MAP.get(authToken);
        if(result == null) { throw new DataAccessException("AuthToken not found"); }
        return result;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if(!AUTH_DATA_MAP.containsKey(authToken)){
            throw new DataAccessException("AuthToken not found");
        }
        AUTH_DATA_MAP.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException{
        try{
            AUTH_DATA_MAP.clear();
        } catch(Exception e){
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }

    public boolean isEmpty() { return AUTH_DATA_MAP.isEmpty(); }
}
