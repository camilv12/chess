package client;


import ui.ChessBoardRenderer;

public class GameClient implements Client {
    private final Session session;

    public GameClient(Session session){
        this.session = session;
    }

    @Override
    public String prompt() {
        return "[GAME] >>> ";
    }

    @Override
    public ClientState eval(String input) throws Exception{
        try{
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            return switch(cmd){
                case "redraw" -> draw();
                case "leave" -> leave();
                case "quit" -> ClientState.EXIT;
                default -> ClientState.GAME;
            };
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void help(){
        System.out.print( """
               Game Menu:
               help - display menu
               redraw - redraw board
               leave - leave game
               quit - exit program
               """);
    }

    public ClientState leave(){
        return ClientState.LOBBY; // No real functionality yet
    }

    public ClientState draw(){
        boolean isWhitePerspective = (session.getColor() == null) || (session.getColor().equals("WHITE"));
        ChessBoardRenderer.render(session.getGame(), isWhitePerspective);
        return ClientState.GAME;
    }


}