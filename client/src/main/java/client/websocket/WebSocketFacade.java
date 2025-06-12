package client.websocket;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;


public class WebSocketFacade extends Endpoint {
    Session session;
    MessageHandler handler;

    public WebSocketFacade(String url, MessageHandler handler) throws Exception{
        try{
            url = url.replace("http","ws");
            URI socketURI = new URI(url+"/ws");
            this.handler = handler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage serverMessage = new Gson().fromJson(s, ServerMessage.class);
                    handler.message(serverMessage);
                }
            });
        } catch(Exception e){
            throw new Exception("Connection Failed");
        }
    }

    // Endpoint requires this method but intentionally left blank
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendConnect(String token, int id) throws Exception{
        throw new RuntimeException("Not Implemented");
    }

    public void sendMove() throws Exception{
        throw new RuntimeException("Not Implemented");
    }

    public void close() throws Exception{
        try{
            this.session.close();
        } catch (IOException e){
            throw new Exception("Connection Failed");
        }
    }


}
