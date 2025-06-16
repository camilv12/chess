package client.websocket;

import com.google.gson.Gson;
import exception.CommunicationException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageObserver;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;



public class WebSocketCommunicator extends Endpoint {
    Session session;
    ServerMessageObserver observer;
    private final URI uri;
    private final Gson gson = new Gson();

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer) throws Exception{
        this.observer = observer;
        this.uri = URI.create(serverUrl.replace("http","ws")+"/ws");
    }

    public void connect(){
        if(isConnected()){
            throw new CommunicationException("Already connected");
        }
        try{
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch(Exception e){
            throw new CommunicationException("Failed to connect: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.session.addMessageHandler(String.class, message -> {
            try {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                observer.notify(serverMessage);
            } catch (Exception e) {
                observer.notify(new ServerMessage(ServerMessage.ServerMessageType.ERROR));
            }
        });

    }

    public void send(UserGameCommand command) throws Exception{
        if(!isConnected()){
            throw new CommunicationException("Not connected to server");
        }
        try{
            session.getBasicRemote().sendText(gson.toJson(command));
        }catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void disconnect(){
        try{
            if(session != null && session.isOpen()){
                this.session.close();
            }
        } catch (IOException e){
            throw new CommunicationException("Disconnect Failed: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }



}
