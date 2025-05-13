package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    // Deep copy constructor
    public ChessBoard(ChessBoard original){
        this.board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPiece originalPiece = original.board[i][j];
                if(originalPiece != null){
                    this.board[i][j] = new ChessPiece(
                            originalPiece.getTeamColor(),
                            originalPiece.getPieceType()
                    );
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int[] index = ChessPosition.positionToIndex(position);
        board[index[0]][index[1]] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int[] index = ChessPosition.positionToIndex(position);
        return board[index[0]][index[1]];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Set all tiles to null
        for (int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = null;
            }
        }

        // Fill out white side pawns
        for(int i = 0; i < 8; i++){
            addPiece(ChessPosition.indexToPosition(6,i),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        // Fill out black side pawns
        for(int i = 0; i < 8; i++){
            addPiece(ChessPosition.indexToPosition(1,i),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        // Set up rooks
        addPiece(ChessPosition.indexToPosition(7,0),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(ChessPosition.indexToPosition(7,7),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(ChessPosition.indexToPosition(0,0),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(ChessPosition.indexToPosition(0,7),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        // Set up knights
        addPiece(ChessPosition.indexToPosition(7,1),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(ChessPosition.indexToPosition(7,6),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(ChessPosition.indexToPosition(0,1),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(ChessPosition.indexToPosition(0,6),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        // Set up bishops
        addPiece(ChessPosition.indexToPosition(7,2),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(ChessPosition.indexToPosition(7,5),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(ChessPosition.indexToPosition(0,2),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(ChessPosition.indexToPosition(0,5),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        // Set up queens
        addPiece(ChessPosition.indexToPosition(7,3),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(ChessPosition.indexToPosition(0,3),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        // Set up kings
        addPiece(ChessPosition.indexToPosition(7,4),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(ChessPosition.indexToPosition(0,4),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }

    public ChessBoard copy(){
        return new ChessBoard(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   a b c d e f g h").append("\n");
        for (int row = 8; row >= 1; row--) {
            sb.append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                ChessPiece p = getPiece(new ChessPosition(row, col));
                sb.append("|")
                  .append(p != null ? p.toString() : " ");
            }
            sb.append("| ").append(row).append('\n');
        }
        sb.append("   a b c d e f g h").append("\n");
        return sb.toString();
    }
}
