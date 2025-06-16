package exception;

public class CommunicationException extends RuntimeException{
    private boolean shouldReconnect;
    public CommunicationException(String message, boolean shouldReconnect) {

        super(message);
        this.shouldReconnect = shouldReconnect;
    }

    public boolean shouldReconnect() {
        return shouldReconnect;
    }
}
