package dataaccess;

import chess.ChessGame;
import service.ServiceUtils;

import java.sql.SQLException;

public class SqlDaoTestUtility {
    public static final String NEW_CHESS_GAME = ServiceUtils.serialize(new ChessGame());

    public static void clearTables() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            String[] clearStatements = {
                    "DELETE FROM auth",
                    "DELETE FROM games",
                    "DELETE FROM users"
            };
            for(var statement : clearStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Failed to clear tables:",e);
        }
    }

    public static void addUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES(?,?,?)");
            statement.setString(1, username);
            statement.setString(2, "password");
            statement.setString(3, "user@email.com");
            statement.executeUpdate();
        } catch(SQLException e){
            throw new DataAccessException("Failed to add user");
        }
    }
}

