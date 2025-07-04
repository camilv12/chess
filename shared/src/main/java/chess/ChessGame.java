package chess;

import exception.InvalidMoveException;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private final ChessBoard board;

    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null){ return null; }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : potentialMoves){
            ChessBoard boardCopy = this.board.copy();
            applyMove(boardCopy, move);
            if(!isInCheckHelper(boardCopy, piece.getTeamColor())){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private void applyMove(ChessBoard board, ChessMove move){
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(),null);
        board.addPiece(move.getEndPosition(), piece);
        // Handle pawn promotion
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), piece);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        // Check if the move is valid
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null) { throw new InvalidMoveException("Invalid Move");}

        Collection<ChessMove> legalMoves = validMoves(startPosition);
        if(legalMoves.contains(move) && piece.getTeamColor() == this.team){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece.PieceType promotionPieceType = move.getPromotionPiece();
            if(promotionPieceType != null){
                ChessGame.TeamColor color = piece.getTeamColor();
                ChessPiece promotionPiece = new ChessPiece(color, promotionPieceType);
                this.board.addPiece(endPosition, promotionPiece);
                this.board.addPiece(startPosition, null);
            }
            else{
                this.board.addPiece(endPosition, piece);
                this.board.addPiece(startPosition, null);
            }
            ChessGame.TeamColor turn = (this.team == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            setTeamTurn(turn);
        }

        else{
            throw new InvalidMoveException("Invalid Move");
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckHelper(this.board, teamColor);
    }

    private ChessPosition findKingPosition(ChessBoard board, ChessGame.TeamColor teamColor){
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null &&
                        piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == teamColor) {
                    return position;
                }

            }
        }
        return null; // King not found
    }

    private boolean isInCheckHelper(ChessBoard board, TeamColor teamColor){
        ChessPosition kingPosition = findKingPosition(board, teamColor);
        if (kingPosition == null){ return false; }

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (shouldSkipPiece(piece, teamColor)){
                    continue;
                }
                if(canThreatenKing(piece, board, position, kingPosition)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldSkipPiece(ChessPiece piece, TeamColor teamColor){
        return (piece == null || piece.getTeamColor() == teamColor);
    }
    private boolean canThreatenKing(ChessPiece piece, ChessBoard board, ChessPosition from, ChessPosition kingPosition) {
        for (ChessMove move : piece.pieceMoves(board, from)) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            return false;
        }

        // Return false if the king can escape check
        ChessPosition kingPosition = findKingPosition(this.board, teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingPosition);
        if(!kingMoves.isEmpty()) {
            return false;
        }

        // Verify Capture Possibilities
        Collection<ChessPosition> attackers = checkingPositions(this.board, teamColor, kingPosition);
        return !canCaptureOrBlock(this.board, teamColor, kingPosition, attackers);
    }

    private Collection<ChessPosition> checkingPositions(ChessBoard board, TeamColor teamColor,
                                                        ChessPosition kingPosition){
        Collection<ChessPosition> enemyPieces = new ArrayList<>();
        // Iterate through the chess board
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }
                // If there is an enemy piece, check if its moves end with the king's position
                Collection<ChessMove> enemyMoves = piece.pieceMoves(this.board, position);
                for (ChessMove move : enemyMoves){
                    if(move.getEndPosition().equals(kingPosition)){
                        enemyPieces.add(position); // If so, add the starting position
                        break;
                    }
                }
            }
        }
        return enemyPieces;
    }

    private boolean canCaptureOrBlock(ChessBoard board, TeamColor teamColor,
                                      ChessPosition kingPosition, Collection<ChessPosition> attackers){
        // Double Check returns false, already checked king's moves
        if (attackers.size() > 1){ return false; }

        ChessPosition attackerPosition = attackers.iterator().next();
        ChessPiece attacker = board.getPiece(attackerPosition);
        Set<ChessPosition> enemyPath = getEnemyPath(kingPosition, attackerPosition, attacker.getPieceType());

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece == null || piece.getTeamColor() != teamColor){ continue; }
                Collection<ChessMove> moves = validMoves(position);
                for(ChessMove move : moves) {
                    if(enemyPath.contains(move.getEndPosition()) ||
                    move.getEndPosition().equals(attackerPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Set<ChessPosition> getEnemyPath(ChessPosition kingPosition, ChessPosition attackerPosition,
                                            ChessPiece.PieceType pieceType){
        Set<ChessPosition> path = new HashSet<>();
        // Only update for Rooks, Queens, and Bishops
        if (pieceType == ChessPiece.PieceType.ROOK ||
            pieceType == ChessPiece.PieceType.QUEEN||
            pieceType == ChessPiece.PieceType.BISHOP){
            int rowStep = Integer.signum(attackerPosition.getRow() - kingPosition.getRow());
            int colStep = Integer.signum(attackerPosition.getColumn() - kingPosition.getColumn());
            int row = kingPosition.getRow() + rowStep;
            int col = kingPosition.getColumn() + colStep;
            while(row != attackerPosition.getRow() || col != attackerPosition.getColumn()){
                path.add(new ChessPosition(row, col));
                row += rowStep;
                col += colStep;
            }
        }
        return path;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = this.board.getPiece(position);

                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }

                Collection<ChessMove> moves = validMoves(position);
                if(moves != null && !moves.isEmpty()) {
                    return false; // Found at least one valid move
                }
            }
        }

        return true;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                this.board.addPiece(position, piece);
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }

    @Override
    public String toString() {
        return "Current turn: " +
                (team == TeamColor.WHITE ? "WHITE" : "BLACK") +
                '\n' +
                board.toString();
    }
}
