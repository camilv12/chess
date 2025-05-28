package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDao implements GameDao {

    public SqlGameDao() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
        // Reset game id increment to 1
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("ALTER TABLE games AUTO_INCREMENT = 1");
            statement.executeUpdate();
        } catch(SQLException e){
            throw new DataAccessException("Game initialization failed:", e);
        }
    }

    // TODO: Move serialization/deserialization to Service class
    private String serialize(ChessGame game){
        if(game == null){
            return null;
        }
        return new Gson().toJson(game);
    }
    private ChessGame deserialize(String json){
        return new Gson().fromJson(json, ChessGame.class);
    }

    @Override
    public int createGame(GameData data) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("""
                    INSERT INTO games (
                    game_name,
                    white_username,
                    black_username,
                    game_state)
                    VALUES(?,?,?,?)""",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, data.gameName());
            statement.setString(2, data.whiteUsername());
            statement.setString(3, data.blackUsername());
            statement.setString(4, serialize(data.game()));
            statement.executeUpdate();

            var rs = statement.getGeneratedKeys();
            var id = 0;
            if(rs.next()){
                id  =rs.getInt(1);
            }
            return id;
        } catch(SQLException e){
            throw new DataAccessException("Game creation failed:", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("""
                    SELECT
                    game_id,
                    game_name,
                    white_username,
                    black_username,
                    game_state
                    FROM games WHERE game_id=?
                    """);
            statement.setInt(1, gameID);
            try (var rs = statement.executeQuery()){
                if(rs.next()){
                    var id = rs.getInt("game_id");
                    var name = rs.getString("game_name");
                    var white = rs.getString("white_username");
                    var black = rs.getString("black_username");
                    ChessGame state = deserialize(rs.getString("game_state"));
                    return new GameData(id, white, black, name, state);
                }
                else {
                    throw new DataAccessException("Game ID not found");
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Game request failed:", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("""
                    SELECT
                    game_id,
                    game_name,
                    white_username,
                    black_username,
                    game_state
                    FROM games
                    """);
            try (var rs = statement.executeQuery()){
                Collection<GameData> list = new ArrayList<>();
                while(rs.next()){
                    var id = rs.getInt("game_id");
                    var name = rs.getString("game_name");
                    var white = rs.getString("white_username");
                    var black = rs.getString("black_username");
                    ChessGame state = deserialize(rs.getString("game_state"));
                    list.add(new GameData(id, white, black, name, state));
                }
                return list;
            }
        } catch(SQLException e){
            throw new DataAccessException("List games failed:", e);
        }
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("""
                    UPDATE games SET
                     white_username=?,
                     black_username=?,
                     game_state=?
                     WHERE game_id = ?
                    """);
            statement.setString(1, data.whiteUsername());
            statement.setString(2, data.blackUsername());
            statement.setString(3, serialize(data.game()));
            statement.setInt(4, data.gameID());
            statement.executeUpdate();
        } catch(SQLException e){
            throw new DataAccessException("Game update failed:", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("DELETE FROM games");
            statement.executeUpdate();
        } catch(SQLException e){
            throw new DataAccessException("Game clear failed:", e);
        }
    }
}
