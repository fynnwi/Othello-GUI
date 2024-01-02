# Othello GUI – Coal vs. Solar edition

A Java implementation of the board game Othello (also known as Reversi) I made for a university project.

It includes the game logic, a pretty JavaFX GUI, as well as a bot opponent player.

## How to run:
You will need to have Java 11 installed to be able to run this.
If the application is not starting properly, try downloading the appropriate JavaFX SDK for your system from https://openjfx.io/ and replacing the `lib` folder of this repository with the `lib` folder from the new SDK.

1. navigate to `out/artifacts/OthelloAI_jar` in terminal

2. run `java -jar --module-path lib --add-modules=javafx.controls,javafx.fxml OthelloAI.jar`

This is what the board looks like at the start of each game:

![This is what the game board looks like](https://github.com/fynnwi/Othello-GUI/assets/57621903/2d7249e8-349f-4d61-9dc9-3b5851abaaff)

At the moment, Coal has the upper hand...

![Coal has the upper hand](https://github.com/fynnwi/Othello-GUI/assets/57621903/42ce7de4-b117-4555-bd79-9338ee4c162d)

But in the end, Solar wins, even though Coal secured all the corners:

![Solar wins](https://github.com/fynnwi/Othello-GUI/assets/57621903/21ab2449-7707-4a18-9fb0-59aa7157c8bc)
