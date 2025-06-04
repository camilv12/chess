import client.Repl;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        new Repl(port).run();
    }
}