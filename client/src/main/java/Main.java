import client.Repl;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        var port = server.run(0);
        new Repl(port).run();
        server.stop();
    }
}