# My notes

## Project To-Do
- [X] Chess GitHub Repository
- [X] Phase 0: Chess Moves
- [X] Phase 1: Chess Game
- [X] Phase 2: Chess Design
- [X] Phase 3: Chess Web-API
- [ ] Phase 4: Chess Database
    - [X] Implement MySQL Database Setup
        - [X] Install MySQL DBMS
        - [X] Configure `db.properties` with credentials
        - [X] Design database schema (users, games, auths tables)
        - [X] Add DB initialization on server startup
    - [ ] MySQL DAO Implementations
        - [X] Write DAO unit tests in `server/src/test/java/dataaccess`
        - [X] `SqlUserDao`
            - [X] Implement CRUD methods
            - [X] 2 tests per DAO method (1 positive, 1 negative)
        - [X] `SqlAuthDao`
            - [X] Manage authTokens
            - [X] 2 tests per DAO method (1 positive, 1 negative)
        - [ ] `SqlGameDao`
            - [ ] Handle player assignment
            - [X] 2 tests per DAO method (1 positive, 1 negative)
            - [ ] Store serialized ChessGame objects
                - [ ] Add JSON serialization/deserialization for `ChessGame`
                - [ ] Implement Gson TypeAdapter if needed
    - [ ] Implement Password hashing
      - [ ] Integrate BCrypt hashing in `UserService`
      - [ ] Update registration/login to use hashed passwords
    - [ ] Testing
        - [ ] Test board state persistence
        - [ ] Make sure all tests pass locally

- [ ] Phase 5: Chess Pregame
- [ ] Phase 6: Chess Gameplay

## Class Notes