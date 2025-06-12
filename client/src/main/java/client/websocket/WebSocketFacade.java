package client.websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageObserver;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;


public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws Exception{
        try{
            url = url.replace("http","ws");
            URI socketURI = new URI(url+"/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage serverMessage = new Gson().fromJson(s, ServerMessage.class);
                    observer.notify(serverMessage);
                }
            });
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // Endpoint requires this method but intentionally left blank
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void send(UserGameCommand command) throws Exception{
        try{
            String json = new Gson().toJson(command);
            session.getBasicRemote().sendText(json);
        }catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void close() throws Exception{
        try{
            this.session.close();
        } catch (IOException e){
            throw new Exception(e.getMessage());
        }
    }

    public boolean isOpen() {
        return session != null && session.isOpen();
    }



}
