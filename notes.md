# My notes

## Project To-Do
- [X] Chess GitHub Repository
- [X] Phase 0: Chess Moves
- [X] Phase 1: Chess Game
- [X] Phase 2: Chess Design
- [X] Phase 3: Chess Web-API
- [X] Phase 4: Chess Database
- [ ] Phase 5: Chess Pregame
  - [X] Set up project and ServerFacade class
    - [X] Add `ServerFacade` constructor
    - [X] Make sure tests start/stop server and pass port to facade
    - [X] Set up `ServerFacadeTest` skeleton
    - [X] Implement `ServerFacade` class
  - [X] Set up unit tests
    - [X] Each public method in `ServerFacade` has one positive and one negative test
    - [X] Each test has an assert statement
  - [X] Implement Login Client
    - [X] `help` - Displays text informing what the user can do
    - [X] `login` - Prompts login info, calls server login API, transitions client to post-login UI
    - [X] `register` - Prompts user to put in registration info, calls server register API and logs in user
  - [X] Write the Post-Login UI commands
    - [X] `help` - Displays text informing what the user can do
    - [X] `logout` - Logs out the user, calls logout API
    - [X] `create game` - Allows user to input a name for a game, calls create game API
    - [X] `list games` - Lists available games, calls list games API
    - [X] `play game` - Allows the user to join a game, calls join game API
    - [X] `observe game` - Allows the user to specify a game to observe
  - [ ] Implement stubs for Gameplay UI (Functionality added in Phase 6) 
    - [ ]
  - [ ] Implement REPL class
    - [ ] Cycle between `LoginClient`, `LobbyClient`, and `GameClient`
    - [ ] `quit` - Exits UI neatly
  - [ ] Draw initial chess board in terminal
    - [ ] Render chessboard with chess pieces, must be alternating colors and show coordinates
    - [ ] Support white and black perspectives
    - [ ] Make sure `h1` and `a8` are the light squares
  - [ ] Handle errors and messages
    - [ ] Map HTTP errors to simple messages
    - [ ] Follow UI requirements: Avoid raw JSON, AuthTokens, Status Codes, etc.
    - [ ] Catch errors and handle bad input without crashing
  - [ ] Final Checks:
    - [ ] Local unit tests pass and make sense
    - [ ] Program supports functionality
    - [ ] Check for Code Quality
- [ ] Phase 6: Chess Gameplay

## Class Notes