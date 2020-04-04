import game.Board;
import game.Podrick;
import game.RandomPlayer;
import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class Runner {


    public static void main(String[] args) {

        int player1Order = 0;
        int player2Order = 1;

        Board board1 = new Board(player1Order);
        Board board2 = new Board(player2Order);

        RandomPlayer player1 = new RandomPlayer();
        player1.init(player1Order,8000,new Random());

        Podrick player2 = new Podrick();
        player2.init(player2Order,8000,new Random());

        playGame(player2, player1);
    }


    static void playGame(Player firstPlayer,Player secondPlayer) {
        Move lastMove = null;
        boolean gameRunning = true;

        while (gameRunning) {
            lastMove = firstPlayer.nextMove(lastMove,8000,8000);
            gameRunning = firstPlayer.gameRunning();
            if (!gameRunning) {
                System.out.println("firstPlayer returned !gameRunning");
                break;
            }

            lastMove = secondPlayer.nextMove(lastMove,8000,8000);
            gameRunning = secondPlayer.gameRunning();
            if (!gameRunning) {
                System.out.println("secondPlayer returned !gameRunning");
                break;
            }
        }

    }



}
