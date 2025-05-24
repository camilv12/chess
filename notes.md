# My notes

## Project To-Do
- [X] Chess GitHub Repository
- [X] Phase 0: Chess Moves
- [X] Phase 1: Chess Game
- [X] Phase 2: Chess Design
- [X] Phase 3: Chess Web-API
- [ ] Phase 4: Chess Database
    - [ ] Implement MySQL Database Setup
        - [ ] Install MySQL DBMS
        - [ ] Configure `db.properties` with credentials
        - [ ] Design database schema (users, games, auths tables)
        - [ ] Add DB initialization on server startup
    - [ ] MySQL DAO Implementations
        - [ ] `MySqlUserDao`
            - [ ] Implement CRUD methods with password hashing
            - [ ] Handle unique username constraints
        - [ ] `MySqlAuthDao`
            - [ ] Manage authTokens
        - [ ] `MySqlGameDao`
            - [ ] Store serialized ChessGame objects
            - [ ] Handle player assignment
    - [ ] Password Security
        - [ ] Integrate BCrypt hashing in `UserService`
        - [ ] Update registration/login to use hashed passwords
    - [ ] ChessGame Serialization
        - [ ] Add JSON serialization/deserialization for `ChessGame`
        - [ ] Implement Gson TypeAdapter if needed
    - [ ] Testing
        - [ ] Write DAO unit tests in `server/src/test/java/dataaccess`
            - [ ] 2 tests per DAO method (1 positive, 1 negative)
            - [ ] Test board state persistence
        - [ ] Make sure all tests pass locally

- [ ] Phase 5: Chess Pregame
- [ ] Phase 6: Chess Gameplay

## Class Notes