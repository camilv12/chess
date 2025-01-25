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
            case KNIGHT -> {
                return knightMoves(board, myPosition);
            }
            case PAWN ->{
                return pawnMoves(board, myPosition);
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

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        // Moves in an L shape, which is any combination of -2/2 and -1/1
        int [][] directions = {{-2,1},{-2,-1},{-1,2},{-1,-2},{2,1},{2,-1},{1,2},{1,-2}};
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[] positions = ChessPosition.positionToIndex(myPosition);

        for(int[] dir : directions){
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

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        pawnForward(board, myPosition, validMoves);
        pawnCapture(board,myPosition,validMoves);
        return validMoves;
    }

    private void pawnForward(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        int[] positions = ChessPosition.positionToIndex(myPosition);

        int forward = (this.pieceColor == ChessGame.TeamColor.WHITE) ? -1 : 1;
        int firstRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 6 : 1;

        int row = positions[0] + forward;
        int col = positions[1];

        //Check if out of bounds
        if(row < 0 || row >= board.getBoard().length){
            return;
        }

        //Check if any piece blocks (cannot capture forward)
        ChessPiece piece = board.getBoard()[row][col];
        if(piece == null){
            ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
            if(row == 0 || row == board.getBoard().length - 1){
                validMoves.add(new ChessMove(myPosition,newPosition,PieceType.QUEEN));
                validMoves.add(new ChessMove(myPosition,newPosition,PieceType.ROOK));
                validMoves.add(new ChessMove(myPosition,newPosition,PieceType.BISHOP));
                validMoves.add(new ChessMove(myPosition,newPosition,PieceType.KNIGHT));
            }
            else {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
                if(positions[0] == firstRow){
                    pawnFirstMove(board, myPosition, validMoves);
                }
            }
        }


    }
    private void pawnFirstMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        int[] positions = ChessPosition.positionToIndex(myPosition);

        int forward = (this.pieceColor == ChessGame.TeamColor.WHITE) ? -2 : 2;
        int row = positions[0] + forward;
        int col = positions[1];

        // Trivial check if out of bounds
        if(row < 0 || row >= board.getBoard().length){
            return;
        }

        // Check if any piece blocks
        ChessPiece piece = board.getBoard()[row][col];
        if(piece == null) {
            ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
            validMoves.add(new ChessMove(myPosition, newPosition, null));
        }
    }
    private void pawnCapture(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        int[] positions = ChessPosition.positionToIndex(myPosition);
        int forward = (this.pieceColor == ChessGame.TeamColor.WHITE) ? -1 : 1;
        int[][] directions = {{forward, -1},{forward, 1}};
        for(int[] dir : directions){
            int row = positions[0] + dir[0];
            int col = positions[1] + dir[1];
            if (row < 0 || row >= board.getBoard().length || col < 0 || col >= board.getBoard()[0].length){
                continue;
            }
            ChessPiece piece = board.getBoard()[row][col];
            if (piece != null){
                if (!piece.getTeamColor().equals(this.pieceColor)){
                    ChessPosition newPosition = ChessPosition.indexToPosition(row, col);
                    if(row == 0 || row == board.getBoard().length - 1){
                        validMoves.add(new ChessMove(myPosition,newPosition,PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition,newPosition,PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition,newPosition,PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition,newPosition,PieceType.KNIGHT));
                    }
                    else{
                        validMoves.add(new ChessMove(myPosition,newPosition,null));
                    }
                }
            }
        }
    }
}
