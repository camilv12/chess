package dataaccess.ram;

import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class RamUserDao implements UserDao {
    private static final Map<String, UserData> USER_DATA_MAP = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (USER_DATA_MAP.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        USER_DATA_MAP.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        UserData result = USER_DATA_MAP.get(username);
        if(result == null) { throw new DataAccessException("User not found"); }
        return result;
    }

    @Override
    public void clear() throws DataAccessException{
        try{
            USER_DATA_MAP.clear();
        } catch (Exception e){
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }
}
