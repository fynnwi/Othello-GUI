package game;

public class FieldProperties {
    public int row;
    public int column;
    public int nrFlips;
    public float greedyValue;
    public float diskSquareValue;
    public float finalScore;
    public int opponentValidMoves;
    public float opponentMobilityScore;

    public FieldProperties(int row, int column) {
        this.row = row;
        this.column = column;
        nrFlips = 0;
        greedyValue = 0;
        diskSquareValue = 0;
        finalScore = 0;
        opponentValidMoves = 0;
        opponentMobilityScore = 0;
    }
}
