package game;

import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class GreedyPlayer implements Player {
    private Board board;
    private long remainingTime;
    private Random rndGenerator;
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
        if (!board.enterMove(prevMove, opponentOrder)) {
            return null;
        }

        while (true) {

            int[] myMoveCoordinates = findTheGreedyMove();

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


    private int[] findTheGreedyMove() {
        byte[][] validMoves = board.getValidMoves(myOrder);
        int row = 0;
        int column = 0;
        boolean skip = true;
        for (int i = 0; i < validMoves.length; i++) {
            for (int j = 0; j < validMoves.length; j++) {
                if (validMoves[i][j] != 0) {
                    skip = false;
                    if (validMoves[i][j] > validMoves[row][column]) {
                        row = i;
                        column = j;
                    }
                }
            }
        }
        if (skip == true) {
            return null;
        }
        return new int[]{row, column};
    }
}
