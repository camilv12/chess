package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;

public final class ServiceUtils {
    private ServiceUtils() {}

    public static boolean isBlank(String value){
        return value == null || value.isBlank();
    }

    public static boolean userExists(UserDao userDao, String username){
        try{
            userDao.getUser(username);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public static boolean authTokenExists(AuthDao authDao, String authToken){
        try{
            authDao.getAuth(authToken);
            return true;
        } catch (DataAccessException e){
            return false;
        }
    }

}
