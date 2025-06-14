package dataaccess;

import model.UserData;

public class SqlUserDao implements UserDao {

    public SqlUserDao() {
        try{
            DatabaseManager.createDatabase();
            DatabaseManager.initializeDatabase();
        } catch(DataAccessException e){
            throw new RuntimeException("User DAO initialization failed:", e);
        }
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES(?,?,?)");
            statement.setString(1, data.username());
            statement.setString(2, data.password());
            statement.setString(3, data.email());
            statement.executeUpdate();
        } catch(Exception e){
            throw new DataAccessException("User creation failed:", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement(
                    "SELECT username, password, email FROM users WHERE username = ?"
            );
            statement.setString(1,username);
            try(var rs = statement.executeQuery()){
                if(rs.next()){
                    var user = rs.getString("username");
                    var password = rs.getString("password");
                    var email = rs.getString("email");
                    return new UserData(user, password, email);
                }
                else{
                    throw new NotFoundException("User not found");
                }
            }
        } catch(NotFoundException e){
            throw new NotFoundException(e.getMessage());
        } catch (Exception e){
            throw new DataAccessException("User request failed:", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("DELETE FROM users");
            statement.executeUpdate();
        } catch(Exception e){
            throw new DataAccessException("User clear failed:", e);
        }
    }
}
