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
                return kingMoves(board,myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board,myPosition);
        }
        return movesList;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        addMovesInDirection(moves, board, myPosition, 1, 1); //Up-Right
        addMovesInDirection(moves, board, myPosition, 1, -1); //Up-Left
        addMovesInDirection(moves, board, myPosition, -1, 1); //Down-Right
        addMovesInDirection(moves, board, myPosition, -1, -1); //Down-Left
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        addMovesInDirection(moves, board, myPosition, 1, 0); //Up
        addMovesInDirection(moves, board, myPosition, -1, 0); //Down
        addMovesInDirection(moves, board, myPosition, 0, -1); //Left
        addMovesInDirection(moves, board, myPosition, 0, 1); //Right
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        addMovesInDirection(moves, board, myPosition, 1, 0); //Up
        addMovesInDirection(moves, board, myPosition, -1, 0); //Down
        addMovesInDirection(moves, board, myPosition, 0, -1); //Left
        addMovesInDirection(moves, board, myPosition, 0, 1); //Right
        addMovesInDirection(moves, board, myPosition, 1, 1); //Up-Right
        addMovesInDirection(moves, board, myPosition, 1, -1); //Up-Left
        addMovesInDirection(moves, board, myPosition, -1, 1); //Down-Right
        addMovesInDirection(moves, board, myPosition, -1, -1); //Down-Left
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
            } else {
                moves.add(new ChessMove(position, nextPosition, null));
            }
            row += rowIncrement;
            col += colIncrement;
        }
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        //2-D array that specifies movement direction, 2 in any direction + 1 perpendicular
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-2, 1}, {-2, -1}, {2, 1}, {2, -1}, {-1, -2}, {1, -2}, {-1, 2}, {1, 2}};

        for (int[] direction : directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition nextPosition = new ChessPosition(row, col);
                if (!board.isOccupied(nextPosition) || board.getPiece(nextPosition).getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int col = myPosition.getColumn();
        //Changes based on team color
        int direction = 0;
        int startingRow = 0;
        int promotionRow = 0;
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startingRow = 2;
            promotionRow = 8;
        }
        if (getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
            startingRow = 7;
            promotionRow = 1;
        }
        int row = myPosition.getRow() + direction;
        if (row >= 1 && row <= 8) { //Excluding rows 1 and 8 to allow for promotion
            ChessPosition nextPosition = new ChessPosition(row, col);
            if (!board.isOccupied(nextPosition)) {
                if(row == promotionRow){
                    addPromotionMoves(moves,myPosition,nextPosition);
                }
                else {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                    // Check 2 squares ahead from the starting position
                    if (myPosition.getRow() == startingRow) {
                        nextPosition = new ChessPosition(row + direction, col);
                        if (!board.isOccupied(nextPosition)) {
                            moves.add(new ChessMove(myPosition, nextPosition, null));
                        }
                    }
                }
            }
            // Check for pawn capture
            int[] cols = {-1, 1};
            for (int i : cols) {
                if (col + i <= 8 && col + i >= 1) {
                    nextPosition = new ChessPosition(row, col + i);
                    if (board.isOccupied(nextPosition) && board.getPiece(nextPosition).getTeamColor() != this.getTeamColor()) {
                        if(row == promotionRow){
                            addPromotionMoves(moves,myPosition,nextPosition);
                        }
                        else{
                            moves.add(new ChessMove(myPosition, nextPosition, null));
                        }
                    }
                }
            }
        }
        return moves;
    }
    private void addPromotionMoves(Collection<ChessMove> moves,ChessPosition myPosition,ChessPosition nextPosition){
        moves.add(new ChessMove(myPosition,nextPosition,ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition,nextPosition,ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition,nextPosition,ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition,nextPosition,ChessPiece.PieceType.KNIGHT));
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
        for(int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition nextPosition = new ChessPosition(row, col);
                if (!board.isOccupied(nextPosition) || board.getPiece(nextPosition).getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
        return moves;
    }
}
