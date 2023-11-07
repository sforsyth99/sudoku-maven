package com.example.sudoku;

public class SampleSudokus {

    // Sample Sudoku solution
    public static final short[][] simpleSudokuSolution = new short[][] { //
            { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, //
            { 7, 8, 9, 1, 2, 3, 4, 5, 6 }, //
            { 4, 5, 6, 7, 8, 9, 1, 2, 3 }, //
            { 2, 3, 1, 5, 6, 4, 8, 9, 7 }, //
            { 8, 9, 7, 2, 3, 1, 5, 6, 4 }, //
            { 5, 6, 4, 8, 9, 7, 2, 3, 1 }, //
            { 3, 1, 2, 6, 4, 5, 9, 7, 8 }, //
            { 9, 7, 8, 3, 1, 2, 6, 4, 5 }, //
            { 6, 4, 5, 9, 7, 8, 3, 1, 2 } };

    // Sample Sudoku clue set
    public static final short[][] simpleSudokuClues = new short[][] { //
            { 0, 2, 0, 4, 0, 6, 0, 8, 9 }, //
            { 0, 0, 9, 1, 2, 3, 4, 5, 6 }, //
            { 4, 0, 6, 7, 8, 0, 0, 2, 3 }, //
            { 0, 3, 1, 5, 6, 4, 0, 9, 7 }, //
            { 8, 0, 0, 2, 0, 1, 0, 6, 4 }, //
            { 0, 6, 0, 8, 0, 0, 0, 0, 1 }, //
            { 0, 0, 0, 0, 4, 5, 0, 7, 0 }, //
            { 9, 7, 0, 3, 0, 2, 6, 0, 5 }, //
            { 0, 4, 0, 9, 7, 8, 3, 1, 2 } };

    public static final short[][] sampleSolution2 = new short[][] { //
            { 4, 7, 1, 2, 6, 3, 9, 5, 8 }, //
            { 9, 6, 2, 7, 5, 8, 3, 4, 1 }, //
            { 5, 8, 3, 4, 1, 9, 2, 6, 7 }, //
            { 6, 4, 9, 5, 8, 1, 7, 3, 2 }, //
            { 3, 2, 8, 9, 4, 7, 5, 1, 6 }, //
            { 1, 5, 7, 3, 2, 6, 4, 8, 9 }, //
            { 2, 1, 5, 8, 7, 4, 6, 9, 3 }, //
            { 8, 9, 4, 6, 3, 2, 1, 7, 5 }, //
            { 7, 3, 6, 1, 9, 5, 8, 2, 4 } };

    public static final short[][] sampleClues2 = new short[][] { //
            { 0, 7, 0, 0, 6, 0, 9, 5, 8 }, //
            { 9, 6, 2, 0, 0, 8, 0, 0, 1 }, //
            { 0, 8, 0, 4, 1, 9, 0, 6, 0 }, //
            { 6, 4, 0, 0, 8, 1, 7, 3, 0 }, //
            { 3, 0, 0, 9, 4, 0, 0, 0, 6 }, //
            { 1, 5, 0, 0, 0, 0, 0, 0, 0 }, //
            { 0, 0, 5, 8, 0, 0, 6, 9, 3 }, //
            { 0, 0, 4, 6, 0, 2, 0, 0, 0 }, //
            { 7, 0, 0, 0, 9, 0, 8, 0, 0 } };

    // Sample Sudoku solution 3 - hard
    public static final short[][] hardSolution1 = new short[][] { //
            { 9, 7, 6, 8, 1, 2, 4, 5, 3 }, //
            { 2, 5, 3, 9, 6, 4, 8, 1, 7 }, //
            { 1, 4, 8, 3, 5, 7, 9, 2, 6 }, //
            { 7, 9, 5, 6, 2, 8, 3, 4, 1 }, //
            { 4, 6, 1, 5, 7, 3, 2, 8, 9 }, //
            { 3, 8, 2, 1, 4, 9, 7, 6, 5 }, //
            { 5, 3, 9, 2, 8, 6, 1, 7, 4 }, //
            { 6, 2, 4, 7, 9, 1, 5, 3, 8 }, //
            { 8, 1, 7, 4, 3, 5, 6, 9, 2 } };

    public static final short[][] hardSolution1Clues = new short[][] { //
            { 0, 7, 0, 8, 0, 2, 0, 0, 0 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 7 }, //
            { 1, 4, 8, 3, 5, 0, 0, 0, 0 }, //
            { 0, 0, 5, 0, 0, 8, 3, 0, 0 }, //
            { 0, 6, 0, 0, 0, 0, 0, 0, 9 }, //
            { 3, 0, 0, 0, 0, 0, 7, 0, 0 }, //
            { 5, 0, 0, 0, 0, 0, 1, 7, 0 }, //
            { 0, 0, 4, 0, 9, 0, 0, 0, 0 }, //
            { 0, 0, 7, 4, 0, 0, 0, 0, 2 } };

    public static final short[][] hardSolution2 = new short[][] { //
            { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, //
            { 7, 8, 9, 1, 2, 3, 4, 5, 6 }, //
            { 4, 5, 6, 7, 8, 9, 1, 2, 3 }, //
            { 2, 3, 1, 5, 6, 4, 8, 9, 7 }, //
            { 8, 9, 7, 2, 3, 1, 5, 6, 4 }, //
            { 5, 6, 4, 8, 9, 7, 2, 3, 1 }, //
            { 3, 1, 2, 6, 4, 5, 9, 7, 8 }, //
            { 9, 7, 8, 3, 1, 2, 6, 4, 5 }, //
            { 6, 4, 5, 9, 7, 8, 3, 1, 2 } };

    // Sample Sudoku clue set
    public static final short[][] hardSolution2Clues = new short[][] { //
            { 0, 0, 0, 0, 0, 0, 0, 0, 9 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 4, 0, 6, 7, 8, 0, 0, 2, 3 }, //
            { 0, 3, 1, 5, 6, 4, 0, 9, 7 }, //
            { 8, 0, 0, 2, 0, 1, 0, 6, 4 }, //
            { 0, 6, 0, 8, 0, 0, 0, 0, 1 }, //
            { 0, 0, 0, 0, 4, 5, 0, 7, 0 }, //
            { 9, 7, 0, 3, 0, 2, 6, 0, 5 }, //
            { 0, 4, 0, 9, 7, 8, 3, 1, 2 } };

    public static final short[][] hardSolution3 = new short[][] { //
            { 1, 5, 3, 8, 7, 6, 9, 4, 2 }, //
            { 2, 8, 7, 3, 4, 9, 1, 6, 5 }, //
            { 6, 4, 9, 5, 1, 2, 3, 7, 8 }, //
            { 3, 9, 6, 2, 5, 4, 8, 1, 7 }, //
            { 5, 2, 8, 7, 6, 1, 4, 3, 9 }, //
            { 4, 7, 1, 9, 8, 3, 2, 5, 6 }, //
            { 8, 6, 2, 4, 3, 5, 7, 9, 1 }, //
            { 9, 3, 5, 1, 2, 7, 6, 8, 4 }, //
            { 7, 1, 4, 6, 9, 8, 5, 2, 3 } };

    // Sample Sudoku clue set
    public static final short[][] hardSolution3Clues = new short[][] { //
            { 0, 0, 0, 0, 7, 0, 0, 0, 0 }, //
            { 0, 0, 0, 3, 0, 0, 0, 0, 5 }, //
            { 6, 0, 0, 0, 0, 2, 3, 0, 0 }, //
            { 3, 9, 0, 2, 0, 0, 8, 0, 7 }, //
            { 0, 2, 0, 0, 0, 1, 0, 3, 0 }, //
            { 0, 0, 1, 0, 8, 0, 2, 0, 0 }, //
            { 0, 0, 0, 0, 0, 0, 7, 0, 0 }, //
            { 9, 0, 5, 0, 0, 0, 0, 0, 4 }, //
            { 0, 1, 0, 6, 9, 0, 0, 0, 0 } };

    public static final short[][] hardSolution4 = new short[][] { //
            { 6, 2, 9, 3, 8, 7, 4, 5, 1 }, //
            { 4, 7, 3, 6, 1, 5, 9, 2, 8 }, //
            { 8, 5, 1, 9, 2, 4, 3, 6, 7 }, //
            { 3, 4, 7, 1, 5, 9, 2, 8, 6 }, //
            { 5, 9, 2, 8, 7, 6, 1, 4, 3 }, //
            { 1, 8, 6, 2, 4, 3, 5, 7, 9 }, //
            { 7, 3, 4, 5, 9, 8, 6, 1, 2 }, //
            { 2, 6, 5, 7, 3, 1, 8, 9, 4 }, //
            { 9, 1, 8, 4, 6, 2, 7, 3, 5 } };

    public static final short[][] hardSolution4Clues = new short[][] { //
            { 0, 0, 9, 0, 0, 0, 4, 5, 0 }, //
            { 0, 0, 3, 6, 0, 0, 0, 0, 8 }, //
            { 0, 0, 0, 0, 2, 0, 0, 6, 7 }, //
            { 0, 4, 0, 0, 0, 9, 0, 0, 0 }, //
            { 0, 0, 2, 0, 0, 0, 0, 0, 3 }, //
            { 0, 8, 6, 0, 0, 0, 5, 7, 0 }, //
            { 0, 0, 4, 0, 9, 0, 0, 0, 2 }, //
            { 2, 0, 0, 7, 0, 0, 8, 0, 0 }, //
            { 0, 1, 0, 0, 0, 0, 0, 0, 0 } };

    // cannot be solved by just looking up to naked clues
    // has hidden pair 5,7 in row 9. 9,3 and 9, 6
    public static final short[][] evil1Clues = new short[][] { //
            { 0, 0, 9, 0, 7, 0, 3, 0, 0 }, //
            { 0, 5, 0, 0, 0, 0, 0, 0, 6 }, //
            { 0, 0, 0, 2, 0, 6, 8, 0, 1 }, //
            { 4, 3, 0, 0, 0, 0, 0, 0, 2 }, //
            { 0, 0, 6, 0, 0, 0, 4, 0, 9 }, //
            { 0, 0, 0, 7, 0, 0, 0, 0, 0 }, //
            { 0, 9, 0, 0, 0, 3, 0, 5, 0 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 8, 6, 0, 4, 0, 0, 0, 0, 3 } };

    public static final short[][] needsForcingChains1Solution = new short[][] { //
            { 8, 6, 2, 7, 9, 4, 3, 5, 1 }, //
            { 1, 4, 5, 8, 3, 2, 6, 7, 9 }, //
            { 7, 9, 3, 6, 1, 5, 4, 8, 2 }, //
            { 2, 7, 8, 4, 6, 9, 5, 1, 3 }, //
            { 5, 3, 6, 1, 2, 8, 9, 4, 7 }, //
            { 4, 1, 9, 3, 5, 7, 8, 2, 6 }, //
            { 9, 2, 1, 5, 4, 3, 7, 6, 8 }, //
            { 3, 8, 4, 2, 7, 6, 1, 9, 5 }, //
            { 6, 5, 7, 9, 8, 1, 2, 3, 4 } };

    public static final short[][] needsForcingChains1Clues = new short[][] { //
            { 8, 0, 2, 0, 0, 0, 0, 0, 0 }, //
            { 0, 0, 0, 0, 3, 0, 6, 0, 9 }, //
            { 7, 9, 0, 6, 1, 0, 0, 0, 0 }, //
            { 0, 7, 0, 4, 0, 0, 5, 1, 0 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 0, 1, 9, 0, 0, 7, 0, 2, 0 }, //
            { 0, 0, 0, 0, 4, 3, 0, 6, 8 }, //
            { 3, 0, 4, 0, 7, 0, 0, 0, 0 }, //
            { 0, 0, 0, 0, 0, 0, 2, 0, 4 } };

    public static final short[][] needsXWingForcingChains1Solution = new short[][] { //

            { 8, 5, 6, 3, 2, 4, 9, 7, 1 }, //
            { 7, 3, 9, 5, 8, 1, 2, 4, 6 }, //
            { 1, 4, 2, 7, 9, 6, 3, 5, 8 }, //
            { 3, 6, 8, 9, 7, 2, 4, 1, 5 }, //
            { 2, 1, 7, 4, 3, 5, 8, 6, 9 }, //
            { 4, 9, 5, 6, 1, 8, 7, 3, 2 }, //
            { 6, 7, 3, 8, 5, 9, 1, 2, 4 }, //
            { 9, 2, 4, 1, 6, 7, 5, 8, 3 }, //
            { 5, 8, 1, 2, 4, 3, 6, 9, 7 } };

    public static final short[][] needsXWingForcingChains1Clues = new short[][] { //

            { 0, 5, 0, 0, 2, 0, 0, 7, 0 }, //
            { 7, 0, 0, 0, 8, 0, 0, 0, 0 }, //
            { 1, 0, 2, 7, 9, 0, 3, 0, 0 }, //
            { 0, 0, 8, 0, 0, 0, 0, 1, 0 }, //
            { 0, 0, 0, 4, 0, 5, 0, 0, 0 }, //
            { 0, 9, 0, 0, 0, 0, 7, 0, 0 }, //
            { 0, 0, 3, 0, 5, 9, 1, 0, 4 }, //
            { 0, 0, 0, 0, 6, 0, 0, 0, 3 }, //
            { 0, 8, 0, 0, 4, 0, 0, 9, 0 } };

    // The hardest sudoku known
    public static final short[][] alEscargotClues = new short[][] { //
            { 1, 0, 0, 0, 0, 7, 0, 9, 0 }, //
            { 0, 3, 0, 0, 2, 0, 0, 0, 8 }, //
            { 0, 0, 9, 6, 0, 0, 5, 0, 0 }, //
            { 0, 0, 5, 3, 0, 0, 9, 0, 0 }, //
            { 0, 1, 0, 0, 8, 0, 0, 0, 2 }, //
            { 6, 0, 0, 0, 0, 4, 0, 0, 0 }, //
            { 3, 0, 0, 0, 0, 0, 0, 1, 0 }, //
            { 0, 4, 0, 0, 0, 0, 0, 0, 7 }, //
            { 0, 0, 7, 0, 0, 0, 3, 0, 0 } };

    // This is to test changing the size of the grid
    public static final short[][] twoByTwoSolution = new short[][] { { 1, 2, 3, 4 }, //
            { 3, 4, 1, 2 }, //
            { 2, 1, 4, 3 }, //
            { 4, 3, 2, 1 }//
    };
    public static final short[][] twoByTwoClues = new short[][] { { 1, 2, 3, 4 }, //
            { 0, 4, 1, 2 }, //
            { 2, 1, 4, 0 }, //
            { 4, 0, 2, 1 }//
    };

    public static short[][][] samplePuzzles = { hardSolution1Clues, hardSolution2Clues, hardSolution4Clues,
            needsForcingChains1Clues, sampleClues2, simpleSudokuClues };
    private static int currentPuzzleNum = 0;

    public static int[][] getNextPuzzle() {
        if (currentPuzzleNum == samplePuzzles.length) {
            currentPuzzleNum = 0;
        }
        short[][] puzzle = samplePuzzles[currentPuzzleNum];
        currentPuzzleNum++;

        int[][] intArray = short2dArrayToInt2dArray(puzzle);

        return intArray;
    }

    public static final int[][] short2dArrayToInt2dArray(short[][] shortArray) {
        int[][] intArray = new int[shortArray.length][shortArray[0].length];
        for (int i = 0; i < shortArray.length; i++) {
            for (int j = 0; j < shortArray[i].length; j++) {
                intArray[i][j] = shortArray[i][j];
            }
        }
        return intArray;
    }
}
