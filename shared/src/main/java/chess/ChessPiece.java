package chess;

import java.util.Collection;
import java.util.ArrayList;

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
    public ChessGame.TeamColor getTeamColor() {return this.pieceColor;}

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
        switch (this.type) {
            case BISHOP -> {
                return bishopMoves(board, myPosition);
            }
            case ROOK -> {
                return rookMoves(board, myPosition);
            }
            case QUEEN -> {
                return queenMoves(board, myPosition);
            }
            case KING -> {
                return kingMoves(board, myPosition);
            }
        }
        throw new RuntimeException("Not implemented");
    }

    private void directionalMoves(ChessBoard board, ChessPosition currentPosition, int rowDir, int colDir, Collection<ChessMove> validMoves){
        int[] positions = ChessPosition.positionToIndex(currentPosition);

        int row = positions[0];
        int col = positions[1];

        while(true){
            // Iteratively continues to add the direction
            row += rowDir;
            col += colDir;

            // Base Case: Stop recursion if out of bounds
            if (row < 0 || row >= board.getBoard().length || col < 0 || col >= board.getBoard()[0].length){
                break;
            }

            // Check the status of the piece
            ChessPiece piece = board.getBoard()[row][col];
            ChessPosition newPosition = ChessPosition.indexToPosition(row, col);

            // If the space is empty, continue recursion
            if (piece == null){
                validMoves.add(new ChessMove(currentPosition, newPosition, null));
            }
            else{
                // If the space belongs to an enemy piece, allow capture then end recursion
                if (!piece.getTeamColor().equals(this.pieceColor)){
                    validMoves.add(new ChessMove(currentPosition, newPosition, null));
                }
                break;
            }

        }
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        int [][] directions = {{-1,1},{1,1},{1,-1},{-1,-1}};
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1], validMoves);
        }
        return validMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        int [][] directions = {{0,1},{0,-1},{1,0},{-1,0}};
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1], validMoves);
        }
        return validMoves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        int [][] directions = {{-1,1},{1,1},{1,-1},{-1,-1},{0,1},{0,-1},{1,0},{-1,0}};
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1], validMoves);
        }
        return validMoves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        int [][] directions = {{-1,1},{1,1},{1,-1},{-1,-1},{0,1},{0,-1},{1,0},{-1,0}};
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[] positions = ChessPosition.positionToIndex(myPosition);

        for(int[] dir: directions){
            int row = positions[0] + dir[0];
            int col = positions[1] + dir[1];
            // Skip if out of bounds
            if (row < 0 || row >= board.getBoard().length || col < 0 || col >= board.getBoard()[0].length){
                continue;
            }
            ChessPiece piece = board.getBoard()[row][col];
            ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
            if (piece == null){
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
            else{
                // If the space belongs to an enemy piece, allow capture then end recursion
                if (!piece.getTeamColor().equals(this.pieceColor)){
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return validMoves;
    }

    //private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){throw new RuntimeException("Not implemented");}
}
