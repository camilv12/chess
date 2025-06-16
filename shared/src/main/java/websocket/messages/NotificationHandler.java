package websocket.messages;

public class NotificationHandler {
    public static String playerJoined(String username, String playerColor) {
        return String.format("%s joined the game as %s", username, playerColor);
    }

    public static String observerJoined(String username) {
        return String.format("%s is now observing the game", username);
    }

    public static String playerMoved(String username, String moveDescription) {
        return String.format("%s moved %s", username, moveDescription);
    }

    public static String playerLeft(String username) {
        return String.format("%s left the game", username);
    }

    public static String playerResigned(String username) {
        return String.format("%s resigned. Game over!", username);
    }

    public static String checkAlert(String username) {
        return String.format("%s is in check!", username);
    }

    public static String checkmateAlert(String username) {
        return String.format("Checkmate! %s wins!", username);
    }

    public static String stalemateAlert() {
        return "Stalemate! The game is a draw.";
    }
}