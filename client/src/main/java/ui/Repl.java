package ui;

import client.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private ClientState currentState = ClientState.LOGIN;
    private final LoginClient loginClient;
    private final LobbyClient lobbyClient;
    private final GameClient gameClient;

    private Client client;

    public Repl(int port){
        final Session session = new Session();
        this.loginClient = new LoginClient(port, session);
        this.lobbyClient = new LobbyClient(port, session);
        this.gameClient = new GameClient(session);
        client = loginClient;
    }

    public void run(){
        try(Scanner scanner = new Scanner(System.in)) {
            System.out.print(SET_BG_COLOR_MAGENTA + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD);
            System.out.println("♕ Welcome to Chess! Type 'help' to get started. ♕");
            System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);

            while(currentState != ClientState.EXIT){
                System.out.print(client.prompt());
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
            System.out.println("Goodbye!");
        }
    }

    private void setState(ClientState state){
        if(state == ClientState.GAME && currentState != ClientState.GAME){
            gameClient.draw();
        }

        this.currentState = state;
        switch (state){
            case LOGIN -> client = loginClient;
            case LOBBY -> client = lobbyClient;
            case GAME -> client = gameClient;
            case EXIT -> {}
        }
    }


}
