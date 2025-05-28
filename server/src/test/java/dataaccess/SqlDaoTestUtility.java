package dataaccess;

import java.sql.SQLException;

public class SqlDaoTestUtility {

    public static void clearTables() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            String[] clearStatements = {
                    "DELETE FROM users",
                    "DELETE FROM auth",
                    "DELETE FROM games"
            };
            for(var statement : clearStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Failed to clear tables");
        }
    }
}
