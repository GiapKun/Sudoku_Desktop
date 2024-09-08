package Database;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author Tran Giap
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String[][] board;
    private final boolean[][] mutable;
    private final int secondsPassed;
    private final int mistake;
    private final int hint;
    private final String[][] noteValues;
    private final Stack<int[]> moveHistory;
    private final String[][] solution;

    public GameState(String[][] board, boolean[][] mutable, int secondsPassed, int mistake, int hint, String[][] noteValues, Stack<int[]> moveHistory, String[][] solution) {
        this.board = board;
        this.mutable = mutable;
        this.secondsPassed = secondsPassed;
        this.mistake = mistake;
        this.hint = hint;
        this.noteValues = noteValues;
        this.moveHistory = moveHistory;
        this.solution = solution;
    }

    public String[][] getBoard() {
        return board;
    }

    public boolean[][] getMutable() {
        return mutable;
    }

    public int getSecondsPassed() {
        return secondsPassed;
    }

    public int getMistake() {
        return mistake;
    }

    public int getHint() {
        return hint;
    }

    public String[][] getNoteValues() {
        return noteValues;
    }

    public Stack<int[]> getMoveHistory() {
        return moveHistory;
    }

    public String[][] getSolution() {
        return solution;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + Arrays.deepToString(board) +
                ", mutable=" + Arrays.deepToString(mutable) +
                ", secondsPassed=" + secondsPassed +
                ", mistake=" + mistake +
                ", hint=" + hint +
                ", noteValues=" + Arrays.deepToString(noteValues) +
                ", moveHistory=" + moveHistory +
                ", solution=" + Arrays.deepToString(solution) +
                '}';
    }
}
