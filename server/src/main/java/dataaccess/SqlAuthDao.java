package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SqlAuthDao implements AuthDao {

    public SqlAuthDao() {
        try{
            DatabaseManager.createDatabase();
            DatabaseManager.initializeDatabase();
        }catch (DataAccessException e){
            throw new RuntimeException("Auth DAO initialization failed:", e);
        }
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO auth (token, username) VALUES(?,?)");
            statement.setString(1, data.authToken());
            statement.setString(2, data.username());
            statement.executeUpdate();
        } catch(SQLException e){
            throw new DataAccessException("Auth creation failed:", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT token, username FROM auth WHERE token=?");
            statement.setString(1, authToken);
            try (var rs = statement.executeQuery()){
                if(rs.next()){
                    var token = rs.getString("token");
                    var username = rs.getString("username");

                    return new AuthData(token, username);
                }
                else {
                    throw new DataAccessException("Auth token not found");
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Auth request failed:", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("DELETE FROM auth WHERE token=?");
            statement.setString(1, authToken);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0){
                throw new DataAccessException("Auth Token not found");
            }
        } catch(SQLException e){
            throw new DataAccessException("Auth request failed:", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("DELETE FROM auth");
            statement.executeUpdate();
        } catch(SQLException e){
            throw new DataAccessException("Auth clear failed:", e);
        }
    }
}
