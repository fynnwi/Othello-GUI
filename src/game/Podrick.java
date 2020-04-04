package game;

import szte.mi.Move;
import szte.mi.Player;

import java.util.ArrayList;
import java.util.Random;

public class Podrick implements Player {
    private Board board;
    private long remainingTime;
    private int myOrder;
    private int opponentOrder;
    private Random rndGenerator;
    private int[][] diskSquareTable;




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
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {

        if (!board.enterMove(prevMove, opponentOrder)) {
            //System.out.println("Podrick.nextMove(): prevMove illegal (according to Board)");
            return null;
        }

        Move myMove = calculateNextMove();

        // debugging prints:
        //System.out.println("Podrick.nextMove(): Successfully calculated new Move");
        if (myMove != null) {
            //System.out.println("Podrick.nextMove(): myMove: x=" + myMove.x + " y=" + myMove.y);
        }
        else {
            //System.out.println("Podrick.nextMove(): myMove: null");
        }
        board.enterMove(myMove,myOrder);
        //System.out.println("Podrick.nextMove(): Successfully written new Move into data model");
        //System.out.println();


        if (!board.isGameRunning()) {
            //board.printInfo();
        }
        //board.printBoard();
        return myMove;
    }

    @Override
    public boolean gameRunning() {
        return board.isGameRunning();
    }

    private int[][] getDiskSquareValues() {
        return new int[][]{{50,-20,10,5},{0,-30,-1,-1},{0,0,1,1},{0,0,0,0}};
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

    public void printDiskSquareTable() {
        System.out.println("DISK-SQUARE TABLE");
        for (int i = 0; i < diskSquareTable.length; i++) {
            for (int j = 0; j < diskSquareTable.length; j++) {
                System.out.print(diskSquareTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    private Move calculateNextMove() {
        ArrayList<FieldProperties> possibleMoves;
        possibleMoves = putValidMovesInList();
        if (possibleMoves.isEmpty()) {
            return null;
        }
        diskSquareEvaluation(possibleMoves);
        greedyEvaluation(possibleMoves);
        //mobilityEvaluation(possibleMoves);

        // weight the different evaluations and calculate a final score
        calculateFinalScore(possibleMoves);

        int[] move = pickMoveWithHighestScore(possibleMoves);

        // transform coordinates into move object
        Move nextMove = new Move(move[1], move[0]);
        //System.out.println("Podrick.calculateNextMove(): Returning move object with x=" + move[1] + " and y=" + move[0]);
        return nextMove;
    }

    private ArrayList<FieldProperties> putValidMovesInList() {
        ArrayList<FieldProperties> list = new ArrayList<FieldProperties>();
        byte[][] validMoves = board.getValidMoves(myOrder);

        for (int i = 0; i < validMoves.length; i++) {
            for (int j = 0; j < validMoves.length; j++) {
                if (validMoves[i][j] != 0) {
                    FieldProperties listEntry = new FieldProperties(i,j);
                    listEntry.nrFlips = validMoves[i][j];
                    list.add(listEntry);
//                    System.out.println("Adding to list: i=" + i + " j=" + j + "nrFlips=" + validMoves[i][j]);
                }
            }
        }
        return list;
    }

    private void diskSquareEvaluation(ArrayList<FieldProperties> list) {
        int sum = 0;
        for (FieldProperties i : list) {
            i.diskSquareValue = diskSquareTable[i.row][i.column];
            sum += Math.abs(i.diskSquareValue);
            // System.out.println("Podrick.diskSquareEvaluation(): Writing diskSquareValue " + i.diskSquareValue);
            // System.out.println("Podick.diskSquareEvaluation(): Sum = " + sum);
        }
        for (FieldProperties i : list) {
            i.diskSquareValue = i.diskSquareValue / sum;
            //System.out.println("Podrick.diskSquareEvaluation(): DiskSquareValue = " + i.diskSquareValue);
        }
    }

    private void greedyEvaluation(ArrayList<FieldProperties> list) {
        int sum = 0;
        for (FieldProperties i:list) {
            sum += i.nrFlips;
//            System.out.println("nrFlips = " + i.nrFlips);
//            System.out.println("Sum = " + sum);
        }
        for (FieldProperties i:list) {
            i.greedyValue = i.nrFlips / (float) sum;
            //System.out.println("Podrick.greedyEvaluation(): greedyValue = " + i.greedyValue);
        }
    }

    private void mobilityEvaluation(ArrayList<FieldProperties> list) {
        // What happens if list == null?!

        int sum = 0;
        // iterate over all possible moves and
        for (FieldProperties l:list) {
            Board simBoard = new Board(board);
            simBoard.printBoard();
            Move simMove = new Move(l.column,l.row);
            //System.out.println("Podrick.mobilityEvaluation(): simMove: x=" + simMove.x + " y=" + simMove.y);
            if (!simBoard.enterMove(simMove, myOrder)) {
                //System.out.println("Podrick.mobilityEvaluation(): Entering simMove to simBoard failed");
            }
            simBoard.printBoard();
            byte[][] validMoves = simBoard.getValidMoves(opponentOrder);
            int nrPossibleMoves = 0;
            for (int i = 0; i < validMoves.length; i++) {
                for (int j = 0; j < validMoves.length; j++) {
                    // DECIDE: consider NUMBER of possible moves or also the number of flips??

                    // for now: only consider number of possible opponent moves
                    if (validMoves[i][j] != 0) {
                        nrPossibleMoves++;
                    }
                }
            }
            l.opponentValidMoves = nrPossibleMoves;
            sum += nrPossibleMoves;
            //System.out.println("Podrick.mobilityEvaluation(): opponentValidMoves = " + nrPossibleMoves);
            simBoard = null;
        }

        // normalize
        for (FieldProperties l:list) {
            l.opponentMobilityScore = l.opponentValidMoves / (float) sum;
            //System.out.println("Podrick.mobilityEvaluation(): opponentMobilityScore = " + l.opponentMobilityScore);
        }
    }









    private void calculateFinalScore(ArrayList<FieldProperties> list) {
        // weights:
        float wGreedy = 1;
        float wDiskSquare = 5;
        for (FieldProperties i:list) {
            i.finalScore = wGreedy * i.greedyValue + wDiskSquare * i.diskSquareValue;
            //.println("Podrick.calculateFinalScore(): Final Score = " + i.finalScore);
        }
    }

    private int[] pickMoveWithHighestScore(ArrayList<FieldProperties> list) {
        FieldProperties max = list.get(0);
        // System.out.println("Podrick.pickMoveWithHighestScore(): Picking move with highest score");
        // System.out.println("Podrick.pickMoveWithHighestScore(): Current max: " + max.finalScore );
        for (FieldProperties i:list) {
            if (i.finalScore > max.finalScore) {
                max = i;
                // System.out.println("Podrick.pickMoveWithHighestScore(): New max with final score = " + i.finalScore);
            }
            // add: if there are equal final scores, pick a random one of
            // them, instead of the first one in the list
        }
        //System.out.println("Podrick.pickMoveWithHighestScore(): max final score = " + max.finalScore);
        int[] move = new int[]{max.row, max.column};
        //System.out.println("Podrick.pickMoveWithHighestScore(): Returning int[] {" + max.row + ", " + max.column + "}");
        return move;
    }




    public void printBoard() {
        board.printBoard();
    }

    public void printInfo() {
        board.printInfo();
    }

    public int getWinner() {
        return board.getWinner();
    }

}
