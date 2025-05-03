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
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

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
        return switch (this.type) {
            case BISHOP:
                int[][] bishopDirs = {{1,1},{1,-1},{-1,1},{-1,-1}};
                yield calculateMoves(board, myPosition, bishopDirs, true);
            case ROOK:
                int[][] rookDirs = {{1,0},{0,1},{-1,0},{0,-1}};
                yield calculateMoves(board, myPosition, rookDirs, true);
            case QUEEN:
                int [][] queenDirs = {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
                yield calculateMoves(board, myPosition, queenDirs, true);
            case KING:
                int[][] kingDirs = {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
                yield calculateMoves(board, myPosition, kingDirs, false);
            case KNIGHT:
                int [][] knightDirs = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};
                yield calculateMoves(board, myPosition, knightDirs, false);
            case PAWN:
                yield pawnMoves(board, myPosition);
        };
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
            if(!canSlide){
                break;
            }
        } while(true);
    }

    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition,
                                                 int[][] directions, boolean canSlide){
        Collection<ChessMove> validMoves = new ArrayList<>();
        for(int[] dir : directions){
            directionalMoves(board, myPosition, dir[0], dir[1],validMoves, canSlide);
        }
        return validMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        pawnMove(board, myPosition, validMoves);
        pawnCapture(board, myPosition, validMoves);
        return validMoves;
    }

    private void pawnMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        // Convert to indices
        int[] indices = ChessPosition.positionToIndex(myPosition);
        int row = indices[0];
        int col = indices[1];

        // Set first row and direction
        int firstRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 6 : 1;
        int promotionRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 0 : 7;
        int rowDir = (this.pieceColor == ChessGame.TeamColor.WHITE) ? -1 : 1;

        // Move
        boolean isFirstRow = (row == firstRow);
        row += rowDir;

        // Check if out of bounds
        if (row < 0 || row > 7){
            throw new ArrayIndexOutOfBoundsException("Out of Bounds");
        }

        // Check if empty
        ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
        ChessPiece piece = board.getPiece(newPosition);

        if (piece == null){
            if (isFirstRow){
                row += rowDir;
                ChessPosition twoSquares = ChessPosition.indexToPosition(row, col);
                ChessPiece newPiece = board.getPiece(twoSquares);
                if(newPiece == null){
                    validMoves.add(new ChessMove(myPosition, twoSquares, null));
                }
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
            else if (row == promotionRow){
                promotePawn(myPosition, newPosition, validMoves);
            }
            else{
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    private void pawnCapture(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        // Convert to indices
        int[] indices = ChessPosition.positionToIndex(myPosition);
        int row = indices[0];

        // Set promotion row and directions
        int promotionRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 0 : 7;
        int rowDir = (this.pieceColor == ChessGame.TeamColor.WHITE) ? -1 : 1;
        int[] colDirs = {-1, 1};

        row += rowDir;

        if (row < 0 || row > 7){
            throw new ArrayIndexOutOfBoundsException("Out of Bounds");
        }

        for(int dir : colDirs){
            int col = indices[1] + dir;

            if(col < 0 || col > 7){
                continue;
            }

            ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
            ChessPiece piece = board.getPiece(newPosition);

            if(piece != null && piece.pieceColor != this.pieceColor){
                if(row == promotionRow){
                    promotePawn(myPosition, newPosition, validMoves);
                }
                else{
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }



    }

    private void promotePawn(ChessPosition startPosition, ChessPosition endPosition, Collection<ChessMove> validMoves){
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
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
