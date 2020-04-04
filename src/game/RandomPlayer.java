package game;

import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class RandomPlayer implements Player {
    private Board board;
    private long remainingTime;
    private java.util.Random rndGenerator;
    private int myOrder;
    private int opponentOrder;


    @Override
    public void init(int order, long t, Random rnd) {
        board = new Board(order);
        remainingTime = t;
        rndGenerator = rnd;
        myOrder = order;
        if (order == 0) {
            opponentOrder = 1;
        }
        else {
            opponentOrder = 0;
        }
    }



    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        // add prevMove to data model
        // Board needs method to take in new move
        // in case of pass, return null
        // add tOpponent and t to data model??



        if (!board.enterMove(prevMove, opponentOrder)) {
            System.out.println("Invalid Move entered!");
            return null;
        }



        while (true) {
            // Uncomment this line to use the random move generator:
            int[] myMoveCoordinates = generateRandomMove();

            Move myMove;
            if (myMoveCoordinates != null) {
                myMove = new Move(myMoveCoordinates[1], myMoveCoordinates[0]);
            }
            else {
                myMove = null;
            }
            boolean res = board.enterMove(myMove, myOrder);
            if (res == true) {
                return myMove;
            }
        }


    }

    @Override
    public boolean gameRunning() {
        return board.isGameRunning();
    }

    private int[] generateRandomMove() {
        byte[][] validMoves = board.getValidMoves(myOrder);

        for (int i = 0; i < 100000; i++) {
            int a = rndGenerator.nextInt(board.SIZE);
            int b = rndGenerator.nextInt(board.SIZE);
            if (validMoves[a][b] != 0) {
                return new int[]{a, b};
            }
        }
        // if the end of the previous for loop is reached, most likely there
        // is no valid move possible for the current player, so lets return null
        // and skip this move
        return null;
    }

    public boolean isGameRunning() {
        return board.isGameRunning();
    }


    public Board getBoard() {
        return this.board;
    }

}
