package client.websocket;

import websocket.messages.ServerMessage;

public interface MessageHandler {
    void message(ServerMessage serverMessage);
}
