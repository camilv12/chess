package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private ClientState currentState = ClientState.LOGIN;
    private final LoginClient loginClient;
    private final LobbyClient lobbyClient;

    private Client client;

    public Repl(int port){
        this.loginClient = new LoginClient(port);
        this.lobbyClient = new LobbyClient(port);
        client = loginClient;
    }

    public void run(){
        try(Scanner scanner = new Scanner(System.in)) {
            System.out.print(SET_BG_COLOR_MAGENTA + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD);
            System.out.println("♕ Welcome to Chess! Type 'help' to get started. ♕");
            System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
            System.out.println(client.prompt());

            while(currentState != ClientState.EXIT){
                String input = scanner.nextLine().trim();
                if(input.equalsIgnoreCase("help")){
                    client.help();
                    continue;
                }

                try{
                    ClientState newState = client.eval(input);
                    setState(newState);
                }catch (Throwable e){
                    System.out.print(e.getMessage());
                }
                System.out.println();
            }
        }
    }

    private void setState(ClientState state){
        this.currentState = state;

        switch (state){
            case LOGIN -> client = loginClient;
            case LOBBY -> client = lobbyClient;
            // case GAME -> client = gameClient;
            case EXIT -> {}
        }
    }


}
