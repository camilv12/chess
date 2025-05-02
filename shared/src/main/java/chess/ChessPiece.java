package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() { return this.pieceColor; }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(this.type) {
            case BISHOP:
                return bishopMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case KING:
                return kingMoves(board, myPosition);
            case PAWN:
        }
        throw new RuntimeException("Not implemented");
    }

    private void directionalMoves(ChessBoard board, ChessPosition myPosition, int rowDir, int colDir,
                                  Collection<ChessMove> moveList, boolean canSlide){
        int[] indices = ChessPosition.positionToIndex(myPosition);
        int row = indices[0];
        int col = indices[1];

        do{
            row += rowDir;
            col += colDir;

            // Check if out of bounds
            if (row < 0 || row > 7 || col < 0 || col > 7) {
                break;
            }

            // Check if the space is empty
            ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
            ChessPiece piece = board.getPiece(newPosition);

            if (piece == null){
                moveList.add(new ChessMove(myPosition, newPosition, null));
            }

            else {
                if (piece.pieceColor != this.pieceColor){
                    moveList.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }while(canSlide);
    }

    /* TODO:
        Refactor pieceMoves and helper functions to take the array of directions and canSlide as the arguments
        Ex: Without calling bishopMoves
        int[][] bishopDirections = {{1,1},{1,-1},{-1,1},{-1,-1}};
        for(int[] dir : directions) ...
     */

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};
        for(int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1],validMoves, true);
        }
        return validMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
        for(int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1],validMoves, true);
        }
        return validMoves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for(int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1],validMoves, true);
        }
        return validMoves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for(int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1],validMoves, false);
        }
        return validMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};
        for(int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1],validMoves, false);
        }
        return validMoves;
    }



    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
