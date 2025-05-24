package service;

import dataaccess.DataAccessException;
import dataaccess.UserDao;

public final class ServiceUtils {
    private ServiceUtils() {}

    public static boolean isBlank(String value){
        return value == null || value.isBlank();
    }

    public static boolean isAnyBlank(String... values) {
        for (String value : values) {
            if (isBlank(value)) { return true; }
        }
        return false;
    }

    public static boolean userExists(UserDao users, String username){
        try{
            users.getUser(username);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

}
