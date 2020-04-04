package game;

import szte.mi.Move;


/*

TO DO:
- add functionality to recognize strategy of the opponent

 */





public class Board {


    public static final int SIZE = 8;

    private byte winner;
    private byte[] score;
    private boolean gameRunning;
    private int plyCount;
    private boolean skipAllowed;

    private int myOrder;
    private int opponentOrder;
    private byte firstPlayer;
    private byte secondPlayer;
    private int playerTurn;

    private byte[][] board;
    private byte[][] validMoves;


    // Constructor
    public Board(int myOrder) {

        winner = -1;
        score = new byte[]{2,2};
        gameRunning = true;
        skipAllowed = false;

        this.myOrder = myOrder;
        plyCount = 0;

        if (myOrder == 0) {
            opponentOrder = 1;
            playerTurn = myOrder;
            // playerTurn = opponentOrder;
            firstPlayer = (byte) myOrder;
            secondPlayer = (byte) opponentOrder;
        }
        else {
            opponentOrder = 0;
            playerTurn = opponentOrder;
            // playerTurn = myOrder;
            firstPlayer = (byte) opponentOrder;
            secondPlayer = (byte) myOrder;
        }


        validMoves = new byte[SIZE][SIZE];

        // initialize the board
        board = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // field not occupied -> -1
                board[i][j] = -1;
            }
        }

        board[3][4] = firstPlayer;
        board[4][3] = firstPlayer;
        board[3][3] = secondPlayer;
        board[4][4] = secondPlayer;

        calculateValidMoves();
    }

    // Copy-Constructor
    public Board(Board another) {
        this.winner = another.winner;
        this.score = another.score;
        this.gameRunning = another.gameRunning;
        this.plyCount = another.plyCount;
        this.skipAllowed = another.skipAllowed;
        this.myOrder = another.myOrder;
        this.opponentOrder = another.opponentOrder;
        this.firstPlayer = another.firstPlayer;
        this.secondPlayer = another.secondPlayer;
        this.playerTurn = another.playerTurn;
        this.board = another.board;
        this.validMoves = another.validMoves;
    }




    // FUNCTIONALITY:

    public boolean enterMove(Move move, int order) {
        // The first move is always null!
        // check if move is the first move ever
        if (move == null && plyCount == 0 && order == secondPlayer) {
            //System.out.println("Board.enterMove(): 0th move successfully skipped");
            return true;
        }


        // check if order is right
        if (order != playerTurn && (plyCount != 0)) {
            //System.out.println("Board.enterMove(): Wrong player order or plyCount == 0!");
            return false;
        }

        // check if inputting a null move is legal
        if (move == null) {
            //System.out.println("Board.enterMove(): Input move is null...");

            // check if skipping this move is allowed, otherwise return false
            if (skipAllowed == true) {
                //System.out.println("Board.enterMove(): Skip move was allowed...");
                finalizeNewMove();
                return true;
            }
            //System.out.println("Board.enterMove(): Input move is null but skipping is illegal!");
            return false;
        }
        if (!validIndicesAndEmpty(move.y, move.x)) {
            // illegal input
            //System.out.println("Board.enterMove(): Illegal input, no lastMove executed!");
            return false;
        }

        if (validMoves[move.y][move.x] == 0) {
            //System.out.println("Board.enterMove(): validMoves[" + move.y + "][" + move.x + "] == 0, no lastMove executed");
            return false;
        }

        // new move is valid -> execute
        placeCoin(move.y,move.x);
        finalizeNewMove();
        return true;
    }

    private void finalizeNewMove() {
        // check for winner
        plyCount++;
        nextPlayerTurn();
        calculateValidMoves();

        // check if opponent has any valid moves
        skipAllowed = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (validMoves[i][j] != 0) {
                    skipAllowed = false;
                }
            }
        }

        // opponent has no valid moves, check if I will have to
        // skip a move, too (= end of game)
        if (skipAllowed == true) {
            nextPlayerTurn();
            calculateValidMoves();
            boolean bothPlayersSkip = true;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (validMoves[i][j] != 0) {
                        bothPlayersSkip = false;
                    }
                }
            }
            // game won't end, set playerTurn back to correct value
            if (bothPlayersSkip == false) {
                nextPlayerTurn();
                calculateValidMoves();
            }
            // both players will have to skip:
            else {
                gameRunning = false;
                setWinner();
            }
        }
    }


    /*
    This method calculates the possible legal moves for the respective player and writes them into
    validMoves. The value written in field [i][j] represents the amount of coins that can be flipped,
    in case that the next move is placed there.
     */
    private void calculateValidMoves() {
        // 1.) Iterate over board and find possible candidates
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // reset from previous state
                validMoves[i][j] = 0;
                // if the field is not occupied
                if (board[i][j] == -1) {
                    // investigate the neighborhood
                    // north west
                    if (validIndicesAndOccupiedByEnemy(i-1,j-1)) {
                        // There is an enemy-occupied neighboring field, investigate further
                        // into this direction
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"NW",0);
                    }
                    // north
                    if (validIndicesAndOccupiedByEnemy(i-1,j)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"N",0);
                    }
                    // north east
                    if (validIndicesAndOccupiedByEnemy(i-1,j+1)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"NE",0);
                    }
                    // east
                    if (validIndicesAndOccupiedByEnemy(i,j+1)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"E",0);
                    }
                    // south east
                    if (validIndicesAndOccupiedByEnemy(i+1,j+1)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"SE",0);
                    }
                    // south
                    if (validIndicesAndOccupiedByEnemy(i+1,j)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"S",0);
                    }
                    // south west
                    if (validIndicesAndOccupiedByEnemy(i+1,j-1)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"SW",0);
                    }
                    // west
                    if (validIndicesAndOccupiedByEnemy(i,j-1)) {
                        validMoves[i][j] += checkLineConstructionInDirection(i,j,"W",0);
                    }


                }



            }
        }
    }

    /*
    Returns:    number of coins that can be flipped if a coin is placed on [i][j]
                0 if [i][j] is not a valid move

    This method gets invoked by calculateValidMoves()
     */

    private int checkLineConstructionInDirection(int i, int j, String dir, int count) {
        if (validIndices(i,j)) {
            // base case: there exists a straight line between two coins of the same color
            // and there are enemy coins inbetween them
            if (board[i][j] == playerTurn) {
                // this count represents how many enemy coins can be flipped in case that the
                // next move is placed on the respective field
                return count-1;
            }
            if (dir == "NW" && validIndicesAndNotEmpty(i-1,j-1)) {
                return checkLineConstructionInDirection(i-1,j-1,"NW",count+1);
            }
            if (dir == "N" && validIndicesAndNotEmpty(i-1,j)) {
                return checkLineConstructionInDirection(i-1,j,"N", count+1);
            }
            if (dir == "NE" && validIndicesAndNotEmpty(i-1,j+1)) {
                return checkLineConstructionInDirection(i-1,j+1,"NE",count+1);
            }
            if (dir == "E" && validIndicesAndNotEmpty(i,j+1)) {
                return checkLineConstructionInDirection(i,j+1,"E",count+1);
            }
            if (dir == "SE" && validIndicesAndNotEmpty(i+1,j+1)) {
                return checkLineConstructionInDirection(i+1,j+1,"SE",count+1);
            }
            if (dir == "S" && validIndicesAndNotEmpty(i+1,j)) {
                return checkLineConstructionInDirection(i+1,j,"S",count+1);
            }
            if (dir == "SW" && validIndicesAndNotEmpty(i+1,j-1)) {
                return checkLineConstructionInDirection(i+1,j-1,"SW",count+1);
            }
            if (dir == "W" && validIndicesAndNotEmpty(i,j-1)) {
                return checkLineConstructionInDirection(i,j-1,"W",count+1);
            }
            return 0;
        }
        return 0;
    }

    private void placeCoin(int i, int j) {
        board[i][j] = (byte) playerTurn;
        if (playerTurn == firstPlayer) {
            score[0] += 1;
        }
        else if (playerTurn == secondPlayer) {
            score[1] += 1;
        }
        else {
            //System.out.println("Board.placeCoin(): playerTurn ≠ firstPlayer || playerTurn ≠ secondPlayer");
        }
        flipAllCoins(i,j);
    }


    private void flipAllCoins(int i, int j) {
        // call flipColor in all 8 directions
        flipCoin(i-1,j-1,"NW");
        flipCoin(i-1,j,"N");
        flipCoin(i-1,j+1,"NE");
        flipCoin(i,j+1,"E");
        flipCoin(i+1,j+1,"SE");
        flipCoin(i+1,j,"S");
        flipCoin(i+1,j-1,"SW");
        flipCoin(i,j-1,"W");

    }

    /*
    Returns     true if in the specified direction coins can be flipped

    This method is invoked by flipAllCoins()
     */
    private boolean flipCoin(int i, int j, String dir) {
        // FIRST: check for valid indexes
        if (validIndicesAndNotEmpty(i,j)) {

            // base case: if a field with playerTurn is reached AGAIN, flip the coins
            // of the previous fields
            if (board[i][j] == playerTurn) {
                return true;
            }
            if (dir == "NW") {
                if (flipCoin(i-1,j-1,"NW")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "N") {
                if (flipCoin(i-1,j,"N")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "NE") {
                if (flipCoin(i-1,j+1,"NE")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "E") {
                if (flipCoin(i,j+1,"E")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "SE") {
                if (flipCoin(i+1,j+1,"SE")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "S") {
                if (flipCoin(i+1,j,"S")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "SW") {
                if (flipCoin(i+1,j-1,"SW")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            if (dir == "W") {
                if (flipCoin(i,j-1,"W")) {
                    board[i][j] = (byte) playerTurn;
                    flipScore();
                    return true;
                }
            }
            return false;
        }
        return false;
    }



    // HELPER FUNCTIONS

    private void flipScore() {
        if (playerTurn == firstPlayer) {
            score[0] += 1;
            score[1] -= 1;
        }
        else if (playerTurn == secondPlayer) {
            score[1] += 1;
            score[0] -= 1;
        }
        else {
            //System.out.println("Board.flipScore(): playerTurn ≠ firstPlayer || playerTurn ≠ secondPlayer");
        }
    }
    // Returns true if the indices i and j are allowed for the board
    private boolean validIndices(int i, int j) {
        if (i >= 0 && i < SIZE && j >= 0 && j < SIZE) {
            return true;
        }
        else {
            return false;
        }
    }
    // checks if the indices are valid and the field is empty
    private boolean validIndicesAndEmpty(int i, int j) {
        if (validIndices(i,j)) {
            if (board[i][j] == -1) {
                return true;
            }
            return false;
        }
        return false;
    }
    // checks if the indices are valid and the field is empty
    private boolean validIndicesAndNotEmpty(int i, int j) {
        if (validIndices(i,j)) {
            if (board[i][j] != -1) {
                return true;
            }
            return false;
        }
        return false;
    }
    // checks if the indices are valid and the field is occupied by the enemy player
    private boolean validIndicesAndOccupiedByEnemy(int i, int j) {
        if (validIndicesAndNotEmpty(i,j)) {
            if (board[i][j] != playerTurn) {
                return true;
            }
            return false;
        }
        return false;
    }

    // kann später optimiert werden durch direktes Addieren während flipAllCoins()
//    private void refreshScore() {
//        score[0] = 0;
//        score[1] = 0;
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                if (board[i][j] == firstPlayer) {
//                    score[0] += 1;
//                }
//                else if (board[i][j] == secondPlayer) {
//                    score[1] += 1;
//                }
//            }
//        }
//    }

    private void nextPlayerTurn() {
        if (playerTurn == firstPlayer) {
            playerTurn = secondPlayer;
        }
        else if (playerTurn == secondPlayer) {
            playerTurn = firstPlayer;
        }
        else {
            //System.out.println("Board.nextPlayerTurn(): playerTurn ≠ firstPlayer || playerTurn ≠ secondPlayer");
        }
    }



    // Player-Interface-Order (0,1) muss in Board-Order (1,2) übersetzt werden!
//    private boolean setPlayerTurn(int order) {
//        if (order == 0) {
//            playerTurn = 1;
//            return true;
//        }
//        else if (order == 1) {
//            playerTurn = 2;
//            return true;
//        }
//        else
//            return false;
//    }


//
//    private boolean skipMoveAllowed() {
//        // is this refresh of validMoves really necessary?
//        // -> as long as it is only called in enterMove() it's not necessary
//        // calculateValidMoves();
//
//        // Exception if this is the first move ever (so prevMove MUST be null)
//        if (plyCount == 0) {
//            return true;
//        }
//        // it is not the first move ever and there are literally no valid moves possible:
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                if (validMoves[i][j] != 0) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }






    private void setWinner() {
        if (score[0] > score[1]) {
            // Player 1 wins
            winner = firstPlayer;
        }
        else if (score[0] < score[1]) {
            // Player 2 wins
            winner = secondPlayer;
        }
        else {
            // Tie
            winner = -2;
        }
    }



    // PUBLIC FUNCTIONS:

    public boolean onlySkipPossible(int order) {
        if (order != playerTurn) {
            //System.out.println("Board.onlySkipPossible(): order ≠ playerTurn!");
        }
        return skipAllowed;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }
    public byte[][] getBoard() {
        return board;
    }
    public byte[][] getValidMoves(int order) {
        if (order != playerTurn) {
            //System.out.println("Board.getValidMoves(): order ≠ playerTurn");
        }
        return validMoves;
    }

    public byte[] getScore() {
        return score;
    }
    public int getWinner() {
        return winner;
    }
    public int getPlayerTurn() {
        return playerTurn;
    }

    public void printInfo() {
        System.out.println("INFO:");
        System.out.println("playerTurn = " + playerTurn);
        System.out.println("winner = " + winner);
        System.out.println("gameRunning = " + gameRunning);
        System.out.println("score = " + score[0] + " : " + score[1]);
        System.out.println("plyCount = " + plyCount);
        System.out.println("skipAllowed = " + skipAllowed);
    }
    public void printBoard() {
        System.out.println("BOARD:");
        System.out.println("   0 1 2 3 4 5 6 7");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print("|");
                if (board[i][j] == firstPlayer) {
                    System.out.print("O");
                }
                else if (board[i][j] == secondPlayer) {
                    System.out.print("X");
                }
                else {
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }
    public void printValidMoves() {
        System.out.println("VALID MOVES for player " + playerTurn + ":");
        System.out.println("   0 1 2 3 4 5 6 7");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print("|");
                if (validMoves[i][j] != 0) {
                    System.out.print(validMoves[i][j]);

                }
                else {
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }
}
