package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promotionPiece;


    public ChessMove(ChessPosition start, ChessPosition end,
                     ChessPiece.PieceType promotionPiece) {
        this.start = start;
        this.end = end;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {  return this.start; }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessMove chessMove)) {
            return false;
        }
        return Objects.equals(start, chessMove.start)
                && Objects.equals(end, chessMove.end)
                && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotionPiece);
    }

    @Override
    public String toString() {
        return start +
                "->" + end +
                ", promotion:" + promotionPiece;
    }
}
