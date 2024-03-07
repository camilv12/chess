package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
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
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board,startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null){
            throw new InvalidMoveException("There is no piece here.");
        }
        Collection<ChessMove> movesList = validMoves(move.getStartPosition());
        if(!movesList.contains(move)){
            throw new InvalidMoveException("This move is not allowed for the piece.");
        }
        if(!isTeamTurn(piece)){
            throw new InvalidMoveException("It is not " + piece.getTeamColor() + "'s turn.");
        }

        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);

        if (isInCheck(piece.getTeamColor())) {
            board.addPiece(move.getStartPosition(), piece);
            board.removePiece(move.getEndPosition());
            throw new InvalidMoveException("This move puts the king in check.");
        }

        if(move.getPromotionPiece() != null){
            board.removePiece(move.getEndPosition());
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), promotionType);
            board.addPiece(move.getEndPosition(),promotedPiece);
        }

        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private boolean isTeamTurn(ChessPiece piece) {
        return piece.getTeamColor() == turn;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        return isKingAttacked(kingPosition, opponentColor);
    }

    private ChessPosition findKingPosition(TeamColor teamColor){
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    return position;
                }
            }
        }
        return null;
    }
    private boolean isKingAttacked(ChessPosition kingPosition, TeamColor opponentColor){
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition opponentPosition = new ChessPosition(row,col);
                ChessPiece opponentPiece = board.getPiece(opponentPosition);
                if(opponentPiece != null && opponentPiece.getTeamColor() == opponentColor){
                    if(canAttackKing(opponentPosition,kingPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean canAttackKing(ChessPosition opponentPosition, ChessPosition kingPosition){
        Collection<ChessMove> attackMoves = validMoves(opponentPosition);
        for(ChessMove attack : attackMoves){
            if(attack.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        Collection<ChessPosition> teamPositions =  teamPositions(teamColor);
        boolean check = true;
        for(ChessPosition position : teamPositions){
            Collection<ChessMove> movesList = validMoves(position);
            for(ChessMove move : movesList){
                ChessPiece startPiece = board.getPiece(move.getStartPosition());
                ChessPiece endPiece = board.getPiece(move.getEndPosition());

                board.removePiece(move.getStartPosition());
                board.addPiece(move.getEndPosition(), startPiece);

                if(!isInCheck(teamColor)){
                    board.removePiece(move.getEndPosition());
                    board.addPiece(move.getStartPosition(), startPiece);
                    if(endPiece != null){
                        board.addPiece(move.getEndPosition(), endPiece);
                    }
                    return false;
                }
                board.removePiece(move.getEndPosition());
                board.addPiece(move.getStartPosition(), startPiece);
                if(endPiece != null){
                    board.addPiece(move.getEndPosition(), endPiece);
                }
            }
        }
        return check;
    }

    private Collection<ChessPosition> teamPositions(TeamColor teamColor){
        Collection<ChessPosition> teamPieces = new ArrayList<>();
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor){
                    teamPieces.add(position);
                }
            }
        }
        return teamPieces;
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
        return board;
    }
}
