package gui;

import game.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import szte.mi.Move;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Random;



public class View extends Application {
    static final String TITLE = "Othello - Kohle vs. Solar Edition";
    static final int GAME_SIZE = 8;
    static final int BOARD_SIZE = 600; // in pixels
    static final int FIELD_SIZE = 75; // width of one field in pixels
    static final String PLAYER1_NAME = "Solar";
    static final String PLAYER2_NAME = "Kohle";
    static final int DELAY = 1000; // bot move delay in ms


    static Image BACKGROUND = null;
    static Image PLAYER1 = null;
    static Image PLAYER2 = null;
    static Image VALIDMOVE = null;


    static Move lastMove = null;

    public static void main(String[] args) throws FileNotFoundException {
        // load all images
        URL backgroundUrl = View.class.getResource("/img/Board_Grafik.png");
        URL player1Url = View.class.getResource("/img/Solar.png");
        URL player2Url = View.class.getResource("/img/Kohle.png");
        URL validmoveUrl = View.class.getResource("/img/Solar_frei.png");


        BACKGROUND = new Image(String.valueOf(backgroundUrl));

        PLAYER1 = new Image(String.valueOf(player1Url));
        PLAYER2 = new Image(String.valueOf(player2Url));
        VALIDMOVE = new Image(String.valueOf(validmoveUrl));



        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        int myOrder = 0;
        int opponentOrder = 1;
        Board myBoard = new Board(myOrder);

        Podrick opponentPlayer = new Podrick();
        opponentPlayer.init(opponentOrder, 8000, new Random());


        // board component
        Group boardGroup = new Group();
        initializeBoard(boardGroup, myBoard);

        Group topBar = new Group();
        refreshTopBar(topBar, myBoard);

        EventHandler<MouseEvent> onClick = e -> {
            if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {

                // get field coordinates from click coordinates
                int[] coordinates = getFieldFromClickCoordinates((int) e.getX(), (int) e.getY());
                // request move in data model
                Move myMove = new Move(coordinates[1],coordinates[0]);
                boolean response = myBoard.enterMove(myMove,myOrder);
                if (response == true) {
                    lastMove = opponentPlayer.nextMove(myMove,8000,8000);
                    if (lastMove == null) {
                        //System.out.println("View.start(): opponentPlayer returned null move!");
                    }

                    // delay
                    Task<Void> sleeper = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                Thread.sleep(DELAY);
                            } catch (Exception e){}
                            //System.out.println("just slept");
                            return null;
                        }
                    };
                    sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {
                            //System.out.println("im in the handle");
                            // enter opponent move to data model
                            myBoard.enterMove(lastMove, opponentOrder);

                            // check if I have to skip this move
                            if (myBoard.onlySkipPossible(myOrder)) {
                                if (myBoard.enterMove(null, myOrder)) {
                                    //System.out.println("View.start(): Successfully skipped my move!");
                                }
                                lastMove = opponentPlayer.nextMove(null,8000,8000);
                                myBoard.enterMove(lastMove, opponentOrder);
                            }
                            // refresh gui
                            try {
                                refreshBoard(boardGroup, myBoard, myOrder,true);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            refreshTopBar(topBar, myBoard);
                        }
                    });
                    new Thread(sleeper).start();


                    //System.out.println("after");

                    // refresh gui WITHOUT validmove symbols
                    try {
                        // refreshBoard(boardGroup, myBoard, myOrder);
                        refreshBoard(boardGroup, myBoard, myOrder,false);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    refreshTopBar(topBar, myBoard);
                }
                else {
                    // refresh gui WITH valid move symboles
                    try {
                        // refreshBoard(boardGroup, myBoard, myOrder);
                        refreshBoard(boardGroup, myBoard, myOrder,true);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    refreshTopBar(topBar, myBoard);
                }

            }
        };
        boardGroup.addEventHandler(MouseEvent.ANY, onClick);





        // put top bar and board in a VBox
        VBox layout = new VBox();
        layout.setSpacing(0);
        layout.getChildren().addAll(topBar, boardGroup);




        stage.setScene(new Scene(layout, BOARD_SIZE, BOARD_SIZE+14));
        stage.setTitle(TITLE);
        stage.show();


    }


    private int[] getFieldFromClickCoordinates(int x, int y) {
        int row = y / FIELD_SIZE; // int division
        int column = x / FIELD_SIZE;
        //System.out.println("View.getFieldFromClickCoordinates(): row = " + row + "column = " + column);
        return new int[]{row, column};
    }

    private ImageView initBackground(Image img) {
        ImageView v = new ImageView(img);
        v.setPreserveRatio(true);
        v.setFitHeight(BOARD_SIZE);
        v.setFitWidth(BOARD_SIZE);
        return v;
    }

    private ImageView initField(Image img, int i, int j) {
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(FIELD_SIZE);
        imgView.setFitHeight(FIELD_SIZE);
        imgView.setX(j * FIELD_SIZE);
        imgView.setY(i * FIELD_SIZE);
        return imgView;
    }

    private void initializeBoard(Group board, Board data) {
        // set backgroung image
        ImageView backGroundView = initBackground(BACKGROUND);
        // add background to node
        board.getChildren().addAll(backGroundView);


        // nachchecken: welche order bekommt diese funktion?
        byte[][] validMoves = data.getValidMoves(0);
        byte[][] coins = data.getBoard();

        for (int i = 0; i < GAME_SIZE; i++) {
            for (int j = 0; j < GAME_SIZE; j++) {
                if (coins[i][j] == 0) {
                    board.getChildren().add(initField(PLAYER1, i, j));
                }
                else if (coins[i][j] == 1) {
                    board.getChildren().add(initField(PLAYER2, i, j));
                }
                else if (validMoves[i][j] != 0) {
                    board.getChildren().add(initField(VALIDMOVE, i, j));
                }
            }
        }
    }



    private void refreshTopBar(Group topBar, Board data) {

        topBar.getChildren().clear();

        // set score
        byte[] score = data.getScore();
        Label scoreLabel = new Label(PLAYER1_NAME + " " + score[0] + " : " + score[1] + " " + PLAYER2_NAME);
        scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        topBar.getChildren().add(scoreLabel);

        // print winner or playerTurn
        int w = data.getWinner();
        // if there is a winner
        if (w != -1) {
            Label winnerLabel = new Label();
            winnerLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            winnerLabel.setTranslateX(200);
            if (w == 0) {
                winnerLabel.setText(PLAYER1_NAME + " gewinnt! :)");
            }
            else if (w == 1) {
                winnerLabel.setText(PLAYER2_NAME + " gewinnt!");
            }
            else if (w == -2) {
                winnerLabel.setText("Unentschieden!");
            }
            topBar.getChildren().add(winnerLabel);
        }
        else {
            Label turnLabel = new Label();
            turnLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            turnLabel.setTranslateX(200);
            int playerTurn = data.getPlayerTurn();
            if (playerTurn == 0) {
                turnLabel.setText(PLAYER1_NAME+" ist dran!");
            }
            else {
                turnLabel.setText(PLAYER2_NAME+" ist dran!");
            }
            topBar.getChildren().add(turnLabel);
        }
    }



    // Method for refreshing gui according to some data model
    private void refreshBoard(Group board, Board data, int order, boolean showValidMoves) throws FileNotFoundException {
        // temporarily store background image
        Node backgroundView = board.getChildren().get(0);
        Node infoboardView = board.getChildren().get(1);
        // remove all nodes from the board component
        board.getChildren().clear();
        board.getChildren().addAll(backgroundView, infoboardView);

        // nachchecken: welche order bekommt diese funktion?
        byte[][] validMoves = data.getValidMoves(order);
        byte[][] coins = data.getBoard();

        for (int i = 0; i < GAME_SIZE; i++) {
            for (int j = 0; j < GAME_SIZE; j++) {

                if (coins[i][j] == 0) {
                    board.getChildren().add(initField(PLAYER1, i, j));
                }
                else if (coins[i][j] == 1) {
                    board.getChildren().add(initField(PLAYER2, i, j));
                }
                else if (validMoves[i][j] != 0 && showValidMoves) {
                    board.getChildren().add(initField(VALIDMOVE, i, j));
                }
            }
        }
    }
}




















