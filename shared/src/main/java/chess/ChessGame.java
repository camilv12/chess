package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor team;
    ChessBoard board;

    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        // If there is no piece at that location, this method returns null
        if (piece == null){
            return null;
        }
        // Collection<ChessMove> pieceMoves = piece.pieceMoves(this.board, startPosition);
        // Logic to check if a move would put the king in danger of check

        return piece.pieceMoves(this.board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        // Check if the move is valid
        Collection<ChessMove> legalMoves = validMoves(startPosition);
        ChessPiece piece = this.board.getPiece(startPosition);

        if(legalMoves.contains(move) && piece.getTeamColor() == this.team){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece.PieceType promotionPieceType = move.getPromotionPiece();
            if(promotionPieceType != null){
                ChessGame.TeamColor color = piece.getTeamColor();
                ChessPiece promotionPiece = new ChessPiece(color, promotionPieceType);
                this.board.addPiece(endPosition, promotionPiece);
                this.board.addPiece(startPosition, null);
            }
            else{
                this.board.addPiece(endPosition, piece);
                this.board.addPiece(startPosition, null);
            }
            ChessGame.TeamColor turn = (this.team == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            setTeamTurn(turn);
        }

        else{
            throw new InvalidMoveException();
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }
}
