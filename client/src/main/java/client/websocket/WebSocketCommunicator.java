package client.websocket;

import com.google.gson.Gson;
import exception.CommunicationException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageObserver;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class WebSocketCommunicator extends Endpoint {
    Session session;
    ServerMessageObserver observer;
    private final URI uri;
    private final Gson gson = new Gson();

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer){
        this.observer = observer;
        this.uri = URI.create(serverUrl.replace("http","ws")+"/ws");
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect() {
        if(isConnected()){
            throw new CommunicationException("Already connected");
        }
        try{
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                    .configurator(new ClientEndpointConfig.Configurator() {
                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {
                            headers.put("Accept", Collections.singletonList("application/json"));
                        }
                    })
                    .build();
            this.session = container.connectToServer(this, config, uri);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    observer.notify(notification);
                }
            });
        } catch (Exception e) {
            throw new CommunicationException("Connection Failed: " + e.getMessage());
        }
    }

    public void send(UserGameCommand command){
        if(!isConnected()){
            throw new CommunicationException("Not connected to server");
        }
        try{
            session.getBasicRemote().sendText(gson.toJson(command));
        }catch (IOException e) {
            throw new CommunicationException("Send Failed: " + e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID){
        if(!isConnected()){
            throw new CommunicationException("Not connected to server");
        }
        try{
            send(new UserGameCommand(
                    UserGameCommand.CommandType.LEAVE,
                    authToken,
                    gameID
            ));
        } catch (Exception e){
            throw new CommunicationException("Leave Failed: " + e.getMessage());
        }
    }

    public void disconnect(){
        try{
            if(isConnected()){
                session.close();
            }
        }catch (Exception e){
            throw new CommunicationException("Disconnect failed: " + e.getMessage());
        } finally {
            session = null;
        }
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }



}
