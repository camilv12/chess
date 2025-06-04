package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardRenderer {
    // Color configurations
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK;
    private static final String LIGHT_SQUARE = SET_BG_COLOR_GREEN;
    private static final String DARK_SQUARE = SET_BG_COLOR_DARK_GREEN;
    private static final String WHITE_PIECE_COLOR = SET_TEXT_COLOR_WHITE;
    private static final String BLACK_PIECE_COLOR = SET_TEXT_COLOR_BLACK;
    private static final String RESET_ALL = RESET_BG_COLOR + RESET_TEXT_COLOR;

    public static void render(ChessGame game, boolean isWhitePerspective){
        StringBuilder display = new StringBuilder();

        // Top Border
        display.append(BORDER_COLOR)
                .append("  a  b  c  d  e  f  g  h  ")
                .append(RESET_ALL)
                .append("\n");

        // Invert rows
        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 0 : 9;
        int rowStep = isWhitePerspective ? -1 : 1;

        for (int row = startRow; row != endRow; row += rowStep) {
            // Left border
            display.append(BORDER_COLOR)
                    .append(row)
                    .append(RESET_ALL);

            // Invert cols
            int startCol = isWhitePerspective ? 1 : 8;
            int endCol = isWhitePerspective ? 9 : 0;
            int colStep = isWhitePerspective ? 1 : -1;

            for (int col = startCol; col != endCol; col += colStep){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(position);

                // Determine square color
                boolean isLightSquare = (row + col) % 2 == 0;
                String squareColor = isLightSquare ? LIGHT_SQUARE : DARK_SQUARE;

                display.append(squareColor);

                // Add piece or empty space
                if (piece != null) {
                    String pieceColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                            WHITE_PIECE_COLOR : BLACK_PIECE_COLOR;
                    display.append(pieceColor)
                            .append(getUnicodePiece(piece))
                            .append(RESET_ALL);
                } else {
                    display.append(EMPTY).append(RESET_ALL);
                }
            }

            // Right border
            display.append(BORDER_COLOR)
                    .append(row)
                    .append(RESET_ALL)
                    .append("\n");
        }


        // Bottom Border
        display.append(BORDER_COLOR)
                .append("  a  b  c  d  e  f  g  h  ")
                .append(RESET_ALL)
                .append("\n");

        System.out.print(display);
    }

    private static String getUnicodePiece(ChessPiece piece){
        return switch(piece.getPieceType()){
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }

}

