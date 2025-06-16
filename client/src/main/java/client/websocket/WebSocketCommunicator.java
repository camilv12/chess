package client.websocket;

import com.google.gson.Gson;
import exception.CommunicationException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
        if (this.session != null){
            this.session.setMaxIdleTimeout(30000);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect() {
        if(isConnected()){
            throw new CommunicationException("Already connected", false);
        }
        try{
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxSessionIdleTimeout(30000);

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
                    try{
                        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                        observer.notify(serverMessage);
                    } catch (Exception e){
                        observer.notify(new ErrorMessage("Invalid message format: " + e.getMessage()));
                    }
                }
            });
        } catch (DeploymentException e) {
            throw new CommunicationException("Server rejected connection: " + e.getMessage(), true);
        } catch (IOException e){
            throw new CommunicationException("Network error: " + e.getMessage(), true);
        } catch (Exception e){
            throw new CommunicationException("Connection failed: " + e.getMessage(), false);
        }
    }

    public void send(UserGameCommand command) throws CommunicationException {
        if(!isConnected()) {
            throw new CommunicationException("Not connected to server", false);
        }

        try {
            if(command == null) {
                throw new IllegalArgumentException("Command cannot be null");
            }
            String json = gson.toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IllegalArgumentException e) {
            throw new CommunicationException("Invalid command: " + e.getMessage(), false);
        } catch (IOException e) {
            disconnect();
            throw new CommunicationException("Network error while sending", true);
        }
    }

    public void disconnect(){
        try{
            if(isConnected()){
                session.close(new CloseReason(
                        CloseReason.CloseCodes.NORMAL_CLOSURE,
                        "Client initiated disconnect"
                ));
            }
        }catch (Exception e){
            throw new CommunicationException("Disconnect failed: " + e.getMessage(), false);
        } finally {
            session = null;
        }
    }

    public void reconnect() throws CommunicationException{
        disconnect();
        connect();
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }



}
