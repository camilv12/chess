package websocket.messages;

public interface ServerMessageObserver {

    default void onNotification(NotificationMessage message) {}
    default void onError(ErrorMessage message) {}
    default void onGameUpdate(LoadGameMessage message) {}

    default void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> onNotification((NotificationMessage) serverMessage);
            case ERROR -> onError((ErrorMessage) serverMessage);
            case LOAD_GAME -> onGameUpdate((LoadGameMessage) serverMessage);
        }
    }
}

