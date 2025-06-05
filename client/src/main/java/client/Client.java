package client;

public interface Client {
    String prompt();

    ClientState eval(String input) throws Exception;

    void help();
}
