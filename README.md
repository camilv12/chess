# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Server Sequence Diagram
[![Server Sequence Diagram](server-diagram.png)](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=C4S2BsFMAIGEAtIGcnQMqQE4DcvQCLIgDmAdgFDkCGAxsAPaZzgiSnDkAOVmoNI3duiy5MXHnwFUhACWkATKGO68Q-QcGE41kcavXTN+KsCoBBGjWRJy8k1QBGVJDHkPKbYJgCeSbv1JiaAAGADoATkpiTHoAV05oAGJoqm9mSB5oKk5OFhoTEHpSJIB3eDBdWBZPaABaAD4tUQAuaABtfABRABlOgBVOgF1oAHo3cgwcPAboOVJFLFbYKnBwaAZ0njmFsW2lOsbJ7B0llbWNqozMI50JkR0G41MLKxRmmigeAAoASnIn8yWawNNzNQhQYAwM7QADiVAAtpAAf97C9gfVQeDIJCsqtoABVFyYZEAtEoEEOMGQCFQvFmWLAeAk1FA8kNG5WVqkWKrO7aKx1GZ7RabTAAJWQPI4wqYtRmR0WAG8AL580SCxpVVjsVoAJmCwQAOqQVeQ2PIojF4klSFRsGkJcQQEgvAUiqVypDyFqavKRCK2gAFADyaD6wxGsSJxsVACIo1hbYjY81Y9BYwAadPcFAlRjyFNpzPpyDwqggcCF9OqhWyxoy1pxhOYJOQKvF2M5pB5zAF1PprOx0vlyv92OqmUHJonaCYSBOl1YL6O52Q8WQACOsWQwD+DjnVAA1tAJVud9ASs5oGX5DAQKQaIw53RwN41ToNU0RQAhKjyU-bi6nQAB5WJwoBFGq0wzD6OoACwGjGsaIigVDEG2qadJgMSYK0TjyLOm6AcA45mvM74CjMpKskgrTocAhJLs2rZ-NRryoDMoIAGL3gRjHEvY0AOGkzEIro+4ZMe-EXmA8DQKJiK4ge8hpJAwGrjYbHWJ+HKQK0-HIrpOn+rh0BmOAyneH0R5sCBYEQRQtafrBwCtAhADMSEoUgaEYemWE4a0KyWesNmkKR5oos8NHGfyenQNyvJGVRLLse8B6QvxXzNgCrGpdpnGUmY8h8UShn3JRjRaW8NAZZA9KMl8VAMky9h5dF7FTqCxUEQ1rWmBRMByvWChKK0K6LuuSBSuQk7DV+plNkSrZpq0RaDs1jJ9PQh5sKt6bVlBsowdUOrQPqRomvGy1ift63ppt8Dbbt4XQGth2ReQ0RxAkiSMNI6HQN09BOsUiRlBU3qnZofpTKZQahuGowuCghSkDGCmQFmXY9vINYmQ0DbQIqmPY843b5hOo1YOyFV6eAIP3l8wOgwBO57gex5sy6F5Xjed4Pk+kAvm+yWwy00C-v+RE7nZkDgWjR3OdDzQIZdcbeb5hYBYweF-oRZ4uhF5G6Y8+VvPRWWY+1gLsRSzQ8fMBJlYJwnyTdiLkBJR7QAAkoL2HC5omNRbbwLizOiXgINsUS-itotYwIAAF6QPIcsK5BTnzS5bnBAAjF51ha2tOumbECeMknqcFqqkXVRxEecgZ9he5zfsB8+mg4-mMfzbW+mV-A1dpxnDlKznKvQAhhdXZr6Epv52G6-JQ8j7XZEWn3VXm7R0C1RkkJ9U1LW5aHZIcY0oJmSVZmn63puE9Tpks-eErTeA0rP7HIpLYmt1vQOh2R6z09qAKLPjOGyttSuXOohK6mM7oDgei1UBr13rGy3t9a0yQ5xsCBiDOImhwaekqNDKcA92hdF6AMCMKMkBo2NCAnabAjpP3mGNLIqCWEUBlLTOKzQGbECIczQhDJua7kGmbDq1hmj0WPswl6NsL722gI7Xq99TBCTSIo1h3tjz+0fIHOgd8to8PPjRYaulmhRz7uLRY8dHrrzHorbOJ0YF51nhrYuC9S7L3LmvTAKc06YIsXbfhxxOR9XKnFaRYc3i3hpMfaJbUwkFSvpSAg1JsQwBSQNR+9QGwsyIe-GafCIlKkgaIYaucLoxjruRL6VpfpzgIt0VcsIxKoBIZDFyFCTKtDaDCfoEZiBiSYdwl6bDCnPyCpM1hfD6jWOgCwF0cIULM1XOs5AEi-im3qA3OR2IFHzNIMomKhVWjqNMf1Kg2iuFmKmfojuRiu43LQWktkSy6Y2J5NHMWhwBmOMTkEmuLis4mWgZ4TxRdUK+KXoFVeTjQUhIaRaBuscTh5KoHYkaHCRTtLWV00pn9Zrf37gM4msYxkoUXm0OMNLIC+3wIvfOup3JwUHBDTKHsMLrVjA4cAtBDz8RWmOQcjKAByt0xzKmGFU6Cmop51KuoypAdKGViWZay9lnL0zcsgKKmV-LBXCqNcmcV6YpXGurPKzelofpJE4LETAuQYCwDqp0xSPSvR9PsfDEMYZRnjKHmgmM1rEQKswOwnYczHlsHDWJaVkayX4ujd8gR+86rbK+B6w+kBtm7LbpJE8MseaXlQPzaA95XlB1fLiharQpYSPBY5SFk8PFq1hT5XxsYy56wInOQ2JE0VSIObvI5wATnxrOZ8y+0Br7XOxfc3RFBnmGKFiYvqHyG5WJ+bYgFjbgVVxRenUC8tx5uKVR46eBdu0lwRSvCuyLgkb3rrvTFUTNE4uSjvGRbws35pzdss+GLLk3wIiBh+dNPwNjzSYAtYkSVfzTRU3CmrETataGyjlUaGi1PgRhplLLsO6tVPappjrEjeGpAzEo0AABS9B7xepgIkU1NBDxQxgf0uGgzAz4iRiMRlEyZ0xlyKkLAsB6AM0wFmRlzK8MzLTXGp6PDxNCpo5gaTsn5NavwFTVDGbIl6QAFbMdIDmpj95C1lskc8iRvNK1-gFrWkWDbKHNrs62ie7joVdrnj4vl-ahL6yHcRUJ+zDnyJaifGd5y7YYkyUu79K7TnFp9hu4xmht3mN3REk4B6YMUr48e4ep6fNXuYDeme974V9v8a0Z9ILX2hPy8ZrF36x3RexDmhT+AEvom4rxVjAJ7n9bnXugRUGBoObs2nfeMnGDVtQKYKZh7KHmUstZF6lX21+bOh5OrfkGuIuChkFSoUXqRbpnEi+zQXDAGA2JUDH7wP4k4HYHEM2f23ZjWNazlmkOSlJeU4zlS2H4ZViq00FGgA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
