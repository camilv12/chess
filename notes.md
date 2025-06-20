# My notes

## Project To-Do
- [X] Chess GitHub Repository
- [X] Phase 0: Chess Moves
- [X] Phase 1: Chess Game
- [X] Phase 2: Chess Design
- [X] Phase 3: Chess Web-API
- [X] Phase 4: Chess Database
- [X] Phase 5: Chess Pregame
- [ ] Phase 6: Chess Gameplay
  -[X] Implement `WebSocketFacade`
    - [X] Set up WebSocket client infrastructure
    - [X] Create `WebSocketFacade` class skeleton
    - [X] Add WebSocket dependency to build file
    - [X] Establish basic connection to server endpoint
  - [X] WebSocket Foundation
    - [X] Implement message serialization/deserialization
      - [X] Extend `UserGameCommand` for all required commands
      - [X] Extend `ServerMessage` for all response types
    - [X] Add basic error handling for WebSocket connection
  - [ ] Game Connection Flow
    - [X] Implement `connect` command
      - [X] Player connection with color assignment
      - [X] Observer connection
    - [X] Handle initial `loadGame` response
    - [X] Send connection notifications to other clients
  - [X] Move Implementation
    - [X] Implement `makeMove` command
      - [X] Validate moves locally before sending
      - [X] Handle move notation for notifications
    - [X] Process `loadGame` updates after moves
    - [X] Implement check/checkmate notifications
  - [X] Leave/Resign Functionality
      - [X] Implement `leave` command
        - [X] For both players and observers
        - [X] Proper cleanup of WebSocket connection
      - [X] Implement `resign` command
        - [X] Game state termination
  - [X] UI Commands
    - [X] Implement `help` command with gameplay options
    - [X] Implement `redraw` command
    - [X] Add command parsing to GameClient
  - [X] Highlight Legal Moves
    - [X] Implement move highlighting
      - [X] Calculate legal moves for selected piece
      - [X] Visual distinction for legal squares
      - [X] Support for all piece types
    - [X] Add to help menu
  - [ ] Notification System
    - [ ] Display connection/disconnection notices
    - [ ] Show move notifications with proper formatting
    - [ ] Handle resignation/check/checkmate alerts
    - [ ] Implement clean message formatting
  - [ ] Error Handling
    - [ ] Process WebSocket error messages
    - [ ] Handle invalid move attempts
    - [ ] Manage connection issues
    - [ ] Prevent actions after game end
  - [ ] Final Testing & Polish
    - [ ] Verify all notification scenarios
    - [ ] Test edge cases (multiple observers, etc.)
    - [ ] Improve board redrawing performance
  - [ ] Final Checks (Commit 11)
    - [ ] Run all WebSocket tests
    - [ ] Verify code quality standards
    - [ ] Update documentation
    - [ ] Final manual testing of all features

## Class Notes