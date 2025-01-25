package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    public static int[] positionToIndex(ChessPosition position){
        int row = 8 - position.getRow();
        int col = position.getColumn() - 1;
        return new int[]{row, col};
    }

    public static ChessPosition indexToPosition(int row, int col){
        int posRow = 8 - row;
        int posCol = col + 1;
        return new ChessPosition(posRow,posCol);
    }

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
    this.row = row;
    this.col = col;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row +
                ", " + col +
                ")";
    }
}
