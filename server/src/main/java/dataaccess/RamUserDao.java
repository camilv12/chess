package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class RamUserDao implements UserDao {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try{
            return users.get(username);
        } catch(Exception e){
            throw new DataAccessException("User not found");
        }
    }

    @Override
    public void clear() throws DataAccessException{
        try{
            users.clear();
        } catch (Exception e){
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }
}
