package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

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
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movesList = new ArrayList<>();
        switch (type) {
            case KING:
                // Implement King's moves
                break;
            case QUEEN:
                movesList = queenMoves(board,myPosition);
                // Implement Queen's moves
                break;
            case BISHOP:
                // Implement Bishop's moves
                movesList = bishopMoves(board,myPosition);
                break;
            case KNIGHT:
                // Implement Knight's moves
                break;
            case ROOK:
                movesList = rookMoves(board,myPosition);
                break;
            case PAWN:
                // Implement Pawn's moves
                break;
        }
        return movesList;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        addMovesInDirection(moves,board,myPosition,1,1); //Up-Right
        addMovesInDirection(moves,board,myPosition,1,-1); //Up-Left
        addMovesInDirection(moves,board,myPosition,-1,1); //Down-Right
        addMovesInDirection(moves,board,myPosition,-1,-1); //Down-Left
        return moves;
    }
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        addMovesInDirection(moves,board,myPosition,1,0); //Up
        addMovesInDirection(moves,board,myPosition,-1,0); //Down
        addMovesInDirection(moves,board,myPosition,0,-1); //Left
        addMovesInDirection(moves,board,myPosition,0,1); //Right
        return moves;
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        addMovesInDirection(moves,board,myPosition,1,0); //Up
        addMovesInDirection(moves,board,myPosition,-1,0); //Down
        addMovesInDirection(moves,board,myPosition,0,-1); //Left
        addMovesInDirection(moves,board,myPosition,0,1); //Right
        addMovesInDirection(moves,board,myPosition,1,1); //Up-Right
        addMovesInDirection(moves,board,myPosition,1,-1); //Up-Left
        addMovesInDirection(moves,board,myPosition,-1,1); //Down-Right
        addMovesInDirection(moves,board,myPosition,-1,-1); //Down-Left
        return moves;
    }



    private void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition position, int rowIncrement, int colIncrement) {
        //Increments the rows and columns by a certain number: 1, 0, or -1
        int row = position.getRow() + rowIncrement;
        int col = position.getColumn() + colIncrement;
        while (row >= 1 && row <= 8 && col >= 1 && col <= 8) { //Iterates through the whole board,
            ChessPosition nextPosition = new ChessPosition(row, col);
            if (board.isOccupied(nextPosition)) { //Checks piece in the next position to check if it is an opponent. Allows opponent capture.
                ChessPiece pieceAtNextPosition = board.getPiece(nextPosition);
                if (pieceAtNextPosition.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(position, nextPosition, null));
                }
                break; // Stop if a piece is captured or blocked
            }
            else {
                moves.add(new ChessMove(position, nextPosition, null));
            }
            row += rowIncrement;
            col += colIncrement;
        }
    }








}
