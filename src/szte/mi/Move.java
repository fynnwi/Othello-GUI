package szte.mi;

/**
 * Represents a move in the game. It is a constant class.
 */
public class Move {

    /** X coordinate on the board */
    public final int x;

    /** Y coordinate on the board */
    public final int y;

    /** Constructs the move. It does not perform any checks on the values.
     * @param x X coordinate on the board. It is indexed from 0, that is,
     * its possible vales are 0, 1, etc.
     * @param y Y coordinate on the board. It is indexed from 0, that is,
     * its possible vales are 0, 1, etc.
     */
    public Move( int x, int y ) {

        this.x = x;
        this.y = y;
    }

}
