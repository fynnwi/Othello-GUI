package game;

import szte.mi.Move;
import szte.mi.Player;

import java.util.ArrayList;
import java.util.Random;

public class DiskSquarePlayer implements Player {
    private Board board;
    private long remainingTime;
    private Random rndGenerator;
    private int myOrder;
    private int opponentOrder;
    private int[][] diskSquareTable;
    ArrayList<FieldProperties> possibleMoves;


    /* TO DO
    - dynamische Bewertung der einzelnen Felder

     */




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
        // initialize disk square table
        // mehrmals während des Spiels updaten?
        int[][] subsquare = getDiskSquareValues();
        diskSquareTable = achieveSymmetry(subsquare);
        possibleMoves = new ArrayList<FieldProperties>();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        if (!board.enterMove(prevMove, opponentOrder)) {
            System.out.println("Invalid Move entered!");
            return null;
        }


        while (true) {
            int[] myMoveCoordinates = calculateNextMove();
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
            else {
                System.out.println("new move calculation failed. Return null as move");
                return null;
            }
        }
    }

    @Override
    public boolean gameRunning() {
        return board.isGameRunning();
    }

    private int[] calculateNextMove() {
        putValidMovesInList();
        if (possibleMoves == null) {
            return null;
        }
        diskSquareDistribution();
        greedDistribution();

        // weight the different evaluations
        for (FieldProperties i : possibleMoves) {
            float weightGreedy = 1;
            float weightDiskSquare = 5;
            i.finalScore = weightDiskSquare* i.diskSquareValue + weightGreedy * i.greedyValue;
        }

        FieldProperties max = possibleMoves.get(0);

        for (FieldProperties i : possibleMoves) {
            if (i.finalScore > max.finalScore) {
                max = i;
            }
        }
        int[] myMoveCoordinates = new int[]{max.column, max.row};
        return myMoveCoordinates;
    }


    private int[][] getDiskSquareValues() {
        return new int[][]{{50,-20,10,5},{0,-30,-1,-1},{0,0,1,1},{0,0,0,0}};
    }


    // iterates over possibleMoves list and weight moves
    // according to the number of coins that can be flipped
    private void greedDistribution() {
        int listSum = 0;

        for (FieldProperties i : possibleMoves) {
            listSum += i.nrFlips;
        }

        for (FieldProperties i : possibleMoves) {
            i.greedyValue = i.nrFlips / listSum;
        }


    }

    private void diskSquareDistribution() {
        int sum = 0;
        for (FieldProperties i : possibleMoves) {
            i.diskSquareValue = diskSquareTable[i.row][i.column];
            sum += i.diskSquareValue;
        }
        for (FieldProperties i : possibleMoves) {
            i.diskSquareValue = i.diskSquareValue / sum;
        }
    }


    private void putValidMovesInList() {
        System.out.println("im in putValidMoves inList");
        possibleMoves.clear();
        System.out.println("im in putValidMoves inList");
        byte[][] validMoves = board.getValidMoves(myOrder);
        System.out.println("lel");
        for (int i = 0; i < diskSquareTable.length; i++) {
            for (int j = 0; j < diskSquareTable.length; j++) {
                if (validMoves[i][j] != 0) {
                    FieldProperties listEntry = new FieldProperties(i,j);
                    listEntry.nrFlips = validMoves[i][j];
                    possibleMoves.add(listEntry);
                }
            }
        }
    }



    public void printDiskSquareTable() {
        for (int i = 0; i < diskSquareTable.length; i++) {
            for (int j = 0; j < diskSquareTable.length; j++) {
                System.out.print(diskSquareTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    private int[][] achieveSymmetry(int[][] subsquare) {
        int length = subsquare.length;

        // first: symmetrize subsquare
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (j >= i) {
                    break;
                }
                subsquare[i][j] = subsquare[j][i];
            }
        }

        int[][] matrix = new int[8][8];
        int d = matrix.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                matrix[i][j] = subsquare[i][j];
                // y axis symmetry
                matrix[i][d - j - 1] = subsquare[i][j];
                // x axis symmetry
                matrix[d - i - 1][j] = subsquare[i][j];
                // diagonal symmetry
                matrix[d - i - 1][d - j - 1] = subsquare[i][j];
            }
        }
        return matrix;
    }
}


