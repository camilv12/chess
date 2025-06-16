package server.handler;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

@WebSocket
public class ChessWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) {
        // Just acknowledge connection for now
        try {
            session.getRemote().sendString("{\"serverMessageType\":\"CONNECTED\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // Minimal message handling
        try {
            session.getRemote().sendString("{\"serverMessageType\":\"ACK\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // Clean up if needed
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}