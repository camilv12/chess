package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = 8-row;
        this.col = col+1;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    public int[] positionToIndex(ChessPosition position){
        int row = position.getRow() - 8;
        int col = position.getColumn() - 1;
        return new int[]{row, col};
    }

    public ChessPosition indexToPosition(int row, int col) {
        return new ChessPosition(row, col);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPosition that)) {
            return false;
        }
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString(){
        char file = (char) ('a' + (this.col - 1));
        return "" + file + this.row;
    }
}