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
  -[ ] Implement `WebSocketFacade`
    - [ ] Set up WebSocket client infrastructure
    - [ ] Create `WebSocketFacade` class skeleton
    - [ ] Add WebSocket dependency to build file
    - [ ] Establish basic connection to server endpoint
  - [ ] WebSocket Foundation
    - [ ] Implement message serialization/deserialization
      - [ ] Extend `UserGameCommand` for all required commands
      - [ ] Extend `ServerMessage` for all response types
    - [ ] Add basic error handling for WebSocket connection
  - [ ] Game Connection Flow
    - [ ] Implement `connect` command
      - [ ] Player connection with color assignment
      - [ ] Observer connection
    - [ ] Handle initial `loadGame` response
    - [ ] Send connection notifications to other clients
  - [ ] Move Implementation
    - [ ] Implement `makeMove` command
      - [ ] Validate moves locally before sending
      - [ ] Handle move notation for notifications
    - [ ] Process `loadGame` updates after moves
    - [ ] Implement check/checkmate notifications
  - [ ] Leave/Resign Functionality
      - [ ] Implement `leave` command
        - [ ] For both players and observers
        - [ ] Proper cleanup of WebSocket connection
      - [ ] Implement `resign` command
        - [ ] Confirmation prompt
        - [ ] Game state termination
  - [ ] UI Commands
    - [ ] Implement `help` command with gameplay options
    - [ ] Implement `redraw` command
    - [ ] Add command parsing to GameClient
  - [ ] Highlight Legal Moves
    - [ ] Implement move highlighting
      - [ ] Calculate legal moves for selected piece
      - [ ] Visual distinction for legal squares
      - [ ] Support for all piece types
    - [ ] Add to help menu
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