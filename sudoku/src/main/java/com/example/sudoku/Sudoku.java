package com.example.sudoku;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.ChangeListener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 


public class Sudoku {

    // Listens to the view to detect user actions
    public class ViewChangeListener {

        public void keyPressed(int i) {
            keyPressedP(i);
        }

        public void selectedCellChanged(int i, int j) {
            selectedCellChangedP(i, j);
        }

        public void inputCandidateModeChanged(boolean b) {
            setInputCandidateModeP(b);
        }

        public void autoCandidateModeChanged(boolean b) {
            setAutoCandidateModeP(b);
        }

        public void newGame() {
            newGameP();
        }

        public void solvePuzzle() {
            solveP();
        }

        public void addSudokuChangeListener(ChangeListener changeListener) {
            addChangeListener(changeListener);
        }
    };

    // Sudoku structure definition
    private static final int SUDOKU_ROOT = 3;
    public static final int NUM_ROWS = SUDOKU_ROOT * SUDOKU_ROOT;
    public static final int NUM_COLS = SUDOKU_ROOT * SUDOKU_ROOT;
    public static final int NUM_CANDIDATES = SUDOKU_ROOT * SUDOKU_ROOT;
    public static final int NUM_H_BLOCKS = SUDOKU_ROOT;
    public static final int NUM_V_BLOCKS = SUDOKU_ROOT;
    public static final int NUM_BLOCKS_TOTAL = NUM_H_BLOCKS * NUM_V_BLOCKS;
    public static final int NUM_CELLS_PER_BLOCK = SUDOKU_ROOT * SUDOKU_ROOT;
    public static final int ROWS_PER_BLOCK = SUDOKU_ROOT;
    public static final int COLS_PER_BLOCK = SUDOKU_ROOT;

    // State of an individual Sudoku puzzle
    // private int[][] clues = SampleSudokus.hardSolution1Clues;
    private int[][] clues = SampleSudokus.getNextPuzzle();
    private int[][] guesses; // The user's guesses and the sample clues
    private CandidateSet[][] autoCandidates; // automatically-generated candidates

    private Logger logger = LoggerFactory.getLogger("Sudoku");

    public enum OverrideType {
        NOT_OVERRIDDEN, OVERRIDE_FALSE, OVERRIDE_TRUE
    }

    private OverrideType[][][] userCandidates;// candidates that the user suggests

    // Game play state
    private int selectedCellRow = 0;
    private int selectedCellCol = 0;
    private boolean autoCandidateMode = true;
    private boolean inputCandidateMode = false;

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    private ViewChangeListener viewChangeListener = new ViewChangeListener(); // TODO: Have multiple view listeners?

    public Sudoku() {
        // Initiate the guesses array to be populated with the sample clues
        initSudokuPuzzle(clues);
        initSudokuGamePlay();

    }

    private void initSudokuPuzzle(int[][] newClues) {
        clues = newClues;
        shufflePuzzle(clues);
        guesses = new int[NUM_ROWS][NUM_COLS];

        autoCandidates = new CandidateSet[NUM_ROWS][NUM_COLS];
        for (int i = 0; i < NUM_ROWS; i++)
            for (int j = 0; j < NUM_COLS; j++)
                autoCandidates[i][j] = new CandidateSet();

        userCandidates = new OverrideType[NUM_ROWS][NUM_COLS][NUM_CANDIDATES];

        // Initialize user candidates to be not overridden.
        for (OverrideType[][] row : userCandidates) {
            for (OverrideType[] cell : row) {
                for (int k = 0; k < NUM_CANDIDATES; k++) {
                    cell[k] = OverrideType.NOT_OVERRIDDEN;
                }
            }
        }

        // Initialize each guess with the clue. Then remove the clue
        // from the auto candidates in the corresponding row, column, and blocks.
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                int guess = clues[i][j];
                guesses[i][j] = guess;
                for (int k = 0; k < NUM_CANDIDATES; k++) {
                    int value = k + 1;
                    if (rowHasValue(clues, i, value) || colHasValue(clues, j, value)
                            || blockHasValue(clues, i, j, value))
                        autoCandidates[i][j].remove(k);
                    else
                        autoCandidates[i][j].add(k);
                }
            }
        }
    }

    private void initSudokuGamePlay() {

    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    private void fireStateChanged(SudokuChangeEvent event) {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }

    private void selectedCellChangedP(int row, int col) {
        if (row < 0 || row > (NUM_ROWS - 1) || col < 0 || col > (NUM_COLS - 1))
            throw new IllegalArgumentException(
                    "Row and col indexes: " + row + "," + col + " must be between " + 0 + " and " + NUM_ROWS);

        selectedCellRow = row;
        selectedCellCol = col;

        fireStateChanged(SudokuChangeEvent.makeSelectionChangedEvent(this, row, col, clues));
    }

    // A key was pressed, either set a guess or toggle a candidate of selected cell
    private void keyPressedP(int numericValue) {

        // Do not allow users to change the value of clue cells
        if (clues[selectedCellRow][selectedCellCol] != 0)
            return;

        // In input candidate mode - update the candidates
        if (inputCandidateMode) {
            if (numericValue == 0)
                return;

            OverrideType origUserOverride = userCandidates[selectedCellRow][selectedCellCol][numericValue - 1];
            OverrideType newUserOverride = origUserOverride;

            if (autoCandidateMode) {
                // We are in auto candidate mode, so override the candidate
                switch (origUserOverride) {
                case OVERRIDE_TRUE:
                    newUserOverride = OverrideType.OVERRIDE_FALSE;
                    break;

                case OVERRIDE_FALSE:
                    newUserOverride = OverrideType.OVERRIDE_TRUE;
                    break;

                case NOT_OVERRIDDEN:
                    // This has not been overridden yet so we need to check the auto candidate
                    // to see if toggling should start as true or false
                    if (autoCandidates[selectedCellRow][selectedCellCol].contains(numericValue - 1))
                        // this is an auto candidate, so toggling should override FALSE
                        newUserOverride = OverrideType.OVERRIDE_FALSE;
                    else
                        newUserOverride = OverrideType.OVERRIDE_TRUE;
                    break;
                }

            } else {
                // no auto candidates -- just show user candidates
                if (origUserOverride == OverrideType.OVERRIDE_TRUE)
                    newUserOverride = OverrideType.OVERRIDE_FALSE;
                else
                    newUserOverride = OverrideType.OVERRIDE_TRUE;
            }
            userCandidates[selectedCellRow][selectedCellCol][numericValue - 1] = newUserOverride;

            SudokuChangeEvent event = SudokuChangeEvent.makeCandidatesChangedEvent(this, userCandidates,
                    autoCandidates);
            fireStateChanged(event);

        } else {
            makeGuess(selectedCellRow, selectedCellCol, numericValue);
        }
    }

    private void makeGuess(int row, int col, int value) {
        guesses[row][col] = value;
        SudokuChangeEvent event = SudokuChangeEvent.makeGuessChangedEvent(this, row, col, value, clues, guesses);
        fireStateChanged(event);

        updateAutoCandidates();
    }

    /*
     * Returns true if the Sudoku is solved! No empty cells, no duplicates in rows,
     * no duplicates in columns and no duplicates in blocks.
     */
    public static boolean isSolved(int[][] grid) {

        if (hasEmptyGuessCells(grid) || checkRows(grid) == false || checkColumns(grid) == false
                || checkBlocks(grid) == false)
            return false;
        return true;

    }

    public static boolean isMistake(int row, int col, int[][] guesses) {

        int guess = guesses[row][col];
        // check to see if it is in any other cells in the row.
        for (int j = 0; j < NUM_ROWS; j++) {
            if (j != col && guesses[row][j] == guess) {
                return true;
            }
        }

        // check to see if it is in any other cells in the column.
        for (int i = 0; i < NUM_COLS; i++) {
            if (i != row && guesses[i][col] == guess) {
                return true;
            }
        }

        // check to see if it is in any other rows in the block.
        int blockRow = row / ROWS_PER_BLOCK;
        int blockCol = col / COLS_PER_BLOCK;

        for (int i = 0; i < ROWS_PER_BLOCK; i++) {
            for (int j = 0; j < COLS_PER_BLOCK; j++) {
                int nextVal = guesses[i + (blockRow * ROWS_PER_BLOCK)][j + (blockCol * COLS_PER_BLOCK)];
                if ((guess == nextVal)
                        && !((row == i + (blockRow * ROWS_PER_BLOCK)) && (col == j + (blockCol * COLS_PER_BLOCK)))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean rowHasValue(int[][] array, int rowNum, int value) {

        int[] row = array[rowNum];
        for (int cellVal : row) {
            if (cellVal == value)
                return true;
        }
        return false;
    }

    public static boolean colHasValue(int[][] array, int colNum, int value) {

        for (int j = 0; j < NUM_ROWS; j++) {
            int num = array[j][colNum];
            if (num == value)
                return true;
        }
        return false;

    }

    public static boolean blockHasValue(int[][] array, int rowNum, int colNum, int value) {
        int rowBlockNum = rowNum / ROWS_PER_BLOCK;
        int colBlockNum = colNum / COLS_PER_BLOCK;
        for (int i = 0; i < ROWS_PER_BLOCK; i++) {
            for (int j = 0; j < COLS_PER_BLOCK; j++) {
                int num = array[i + (rowBlockNum * ROWS_PER_BLOCK)][j + (colBlockNum * COLS_PER_BLOCK)];
                if (num == value)
                    return true;
            }
        }
        return false;
    }

    /*
     * Returns true if all of the rows are mistake-free
     */
    private static boolean checkRows(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            int[] row = grid[i];
            boolean[] seen = new boolean[row.length];

            for (int j = 0; j < row.length; j++) {
                int num = row[j];
                if (num != 0) {
                    if (seen[num - 1]) {
                        return false;
                    } else {
                        seen[num - 1] = true;
                    }
                }
            }
        }
        return true;
    }

    /*
     * Checks each of the 9 3x3 blocks in the puzzle
     */
    private static boolean checkBlocks(int[][] grid) {
        for (int i = 0; i < (NUM_ROWS / ROWS_PER_BLOCK); i++) {
            for (int j = 0; j < (NUM_COLS / COLS_PER_BLOCK); j++) {
                if (!checkBlock(grid, i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    /*
     * Checks a single 3-3 block to see if there are any duplicate numbers.
     */
    private static boolean checkBlock(int[][] grid, int blockRow, int blockCol) {
        int numRowBlocks = NUM_ROWS / ROWS_PER_BLOCK;
        int numColBlocks = NUM_COLS / COLS_PER_BLOCK;
        boolean[] seen = new boolean[numRowBlocks * numColBlocks];

        for (int i = 0; i < numRowBlocks; i++) {
            for (int j = 0; j < numColBlocks; j++) {
                int num = grid[i + (blockRow * numRowBlocks)][j + (blockCol * numColBlocks)];
                if (num != 0) {
                    if (seen[num - 1]) {
                        return false;
                    } else {
                        seen[num - 1] = true;
                    }
                }
            }
        }
        return true;
    }

    private static boolean checkColumns(int[][] grid) {

        for (int i = 0; i < NUM_COLS; i++) {
            if (!checkColumn(grid, i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkColumn(int[][] grid, int i) {
        boolean[] seen = new boolean[NUM_ROWS];
        for (int j = 0; j < NUM_ROWS; j++) {
            int num = grid[j][i];
            if (num != 0) {
                if (seen[num - 1]) {
                    return false;
                } else {
                    seen[num - 1] = true;
                }
            }
        }
        return true;
    }

    /*
     * returns true if any 0s left in puzzle
     */

    private static boolean hasEmptyGuessCells(int[][] grid) {
        return Arrays.stream(grid).flatMapToInt(Arrays::stream).anyMatch(cell -> cell == 0);
    }

    // rebuild ALL the auto candidates.
    // later add user candidates and have them be overrides
    private void updateAutoCandidates() {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                for (int k = 0; k < NUM_CANDIDATES; k++) {

                    int value = k + 1;
                    if (rowHasValue(guesses, i, value) || colHasValue(guesses, j, value)
                            || blockHasValue(guesses, i, j, value)) {
                        autoCandidates[i][j].remove(k);
                    } else {
                        autoCandidates[i][j].add(k);
                    }

                }
            }
        }

        fireStateChanged(SudokuChangeEvent.makeCandidatesChangedEvent(this, userCandidates, autoCandidates));
    }

    public static void main(String[] argc) {
        Sudoku sudoku = new Sudoku();

        Solver solver = new Solver(sudoku.clues);
        boolean checkForcingChains = true;
        solver.solve(checkForcingChains);

        boolean inputCandidateMode = false;
        boolean autoCandidateMode = true;
        SudokuGridUI grid = new SudokuGridUI(sudoku.viewChangeListener, sudoku.clues, sudoku.autoCandidates,
                inputCandidateMode, autoCandidateMode);
        grid.setVisible(true);

    }

    private void setInputCandidateModeP(boolean inputCandidateMode) {
        this.inputCandidateMode = inputCandidateMode;
    }

    private void setAutoCandidateModeP(boolean autoCandidateMode) {
        this.autoCandidateMode = autoCandidateMode;
        SudokuChangeEvent event = SudokuChangeEvent.makeAutoCandidateModeEvent(this, autoCandidateMode, userCandidates,
                autoCandidates);
        fireStateChanged(event);
    }

    // TODO: This works, but is really slow.
    // Generate the solution ahead of time.
    // Tidy up all the events that are going on here -- selecting all the cells
    // and making guesses like a user does it in the UI.
    private void solveP() {
        Solver solver = new Solver(clues);
        solver.solve(true);
        int[][] values = solver.getValues();
       // logger.info("Here is the solution:");
       // logger.info(SolverHelper.formatSudoku(values));

        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[0].length; j++) {
                int value = values[i][j];
                selectedCellChangedP(i, j); // TODO: probably a neater way to do this.
                makeGuess(i, j, value);
            }
        }
    }

    private void newGameP() {
        logger.info("The sudoku is going to make a new game now...");
        int[][] nextPuzzle = SampleSudokus.getNextPuzzle();
        initSudokuPuzzle(nextPuzzle);
        initSudokuGamePlay();
        logger.info(SolverHelper.formatSudoku(nextPuzzle));
        SudokuChangeEvent event = SudokuChangeEvent.makeNewGameEvent(this, clues, guesses, autoCandidates,
                userCandidates);
        fireStateChanged(event);
    }

    private int[][] shufflePuzzle(int[][] clues) {
        int[][] shuffled = new int[NUM_ROWS][NUM_COLS];

        // rotate randomly 0 - 3 times
        // randomly assign number symbols
        // swap rows and cols
        // - for each row block there are 6 choices. Pick one of 6
        // - for each col block there are 6 choices. Pick one of 6

//      logger.info("Original:");
//      logger.info(SolverHelper.formatSudoku(clues));
//      logger.info("\n\n90:");
//      logger.info(SolverHelper.formatSudoku(rotateArray90(clues)));
//      logger.info("\n\n180:");
//      logger.info(SolverHelper.formatSudoku(rotateArray180(clues)));
//      logger.info("\n\n270:");
//      logger.info(SolverHelper.formatSudoku(rotateArray270(clues)));

        clues = rotateArray180(clues);

        // logger.info("\n\nNumbers swapped: 1 and 9");
        swapNumbers(clues, 1, 9);
        swapNumbers(clues, 2, 6);
        swapNumbers(clues, 3, 2);
        swapNumbers(clues, 4, 7);
        swapNumbers(clues, 5, 1);
        swapNumbers(clues, 6, 2);
        swapNumbers(clues, 7, 8);
        swapNumbers(clues, 8, 3);
        swapNumbers(clues, 9, 7);
        // logger.info(SolverHelper.formatSudoku(clues));

        // logger.info("\n\nRows swapped 2 and 3");
        swapRows(clues, 0, 2);
        swapRows(clues, 3, 4);
        swapRows(clues, 7, 8);
        // logger.info(SolverHelper.formatSudoku(clues));

        // logger.info("\n\nCols swapped 2 and 3");
        swapColumns(clues, 1, 2);
        swapColumns(clues, 4, 5);
        swapColumns(clues, 6, 8);
        // logger.info(SolverHelper.formatSudoku(clues));

//      logger.info("\n\nAfter shuffling:");
//      logger.info(SolverHelper.formatSudoku(clues));

        return shuffled;
    }

    public static int[][] rotateArray90(int[][] arr) {
        int[][] newArray = new int[arr[0].length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                newArray[j][arr.length - 1 - i] = arr[i][j];
            }
        }
        return newArray;
    }

    public static int[][] rotateArray180(int[][] arr) {
        int[][] newArray = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                newArray[arr.length - 1 - i][arr[0].length - 1 - j] = arr[i][j];
            }
        }
        return newArray;
    }

    public static int[][] rotateArray270(int[][] arr) {
        int[][] newArray = new int[arr[0].length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                newArray[arr[0].length - 1 - j][i] = arr[i][j];
            }
        }
        return newArray;
    }

    public static void swapNumbers(int[][] arr, int num1, int num2) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] == num1) {
                    arr[i][j] = num2;
                } else if (arr[i][j] == num2) {
                    arr[i][j] = num1;
                }
            }
        }
    }

    public static void swapRows(int[][] arr, int row1, int row2) {
        int[] temp = arr[row1];
        arr[row1] = arr[row2];
        arr[row2] = temp;
    }

    public static void swapColumns(int[][] arr, int col1, int col2) {
        for (int i = 0; i < arr.length; i++) {
            int temp = arr[i][col1];
            arr[i][col1] = arr[i][col2];
            arr[i][col2] = temp;
        }
    }
}
