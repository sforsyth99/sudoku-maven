package com.example.sudoku;

import javax.swing.event.ChangeEvent;

import com.example.sudoku.Sudoku.OverrideType;

@SuppressWarnings("serial")

public class SudokuChangeEvent extends ChangeEvent {

    public enum SudokuChangeEventType {
        NO_CHANGE, SELECTED_CELL_CHANGED, GUESS_CHANGED, CANDIDATES_CHANGED, AUTO_CANDIDATE_MODE_CHANGED,
        INPUT_CANDIDATE_MODE_CHANGED, WIN_STATE_CHANGED, NEW_GAME
    };

    private SudokuChangeEventType changeType = SudokuChangeEventType.NO_CHANGE;
    private int row = -1;
    private int col = -1;
    private int guess = -1;
    private OverrideType[][][] userCandidates = null;
    private CandidateSet[][] autoCandidates = null;
    private boolean autoCandidateMode = true;
    private boolean inputCandidateMode = true;
    private boolean userWon = false;

    private int[][] clues = null;
    private int[][] guesses = null;

    public SudokuChangeEventType getChangeType() {
        return changeType;
    }

    private SudokuChangeEvent(Object source, SudokuChangeEventType changeType) {
        super(source);
        this.changeType = changeType;
    }

    public static SudokuChangeEvent makeGuessChangedEvent(Object source, int row, int col, int guess, int[][] clues,
            int[][] guesses) {
        SudokuChangeEvent event = new SudokuChangeEvent(source, SudokuChangeEventType.GUESS_CHANGED);
        event.row = row;
        event.col = col;
        event.guess = guess;
        event.clues = clues;
        event.guesses = guesses;
        return event;
    }

    public static SudokuChangeEvent makeSelectionChangedEvent(Object source, int row, int col, int[][] clues) {
        SudokuChangeEvent event = new SudokuChangeEvent(source, SudokuChangeEventType.SELECTED_CELL_CHANGED);
        event.row = row;
        event.col = col;
        event.clues = clues;
        return event;
    }

    public static SudokuChangeEvent makeCandidatesChangedEvent(Object source, OverrideType[][][] userCandidates,
            CandidateSet[][] autoCandidates) {
        SudokuChangeEvent event = new SudokuChangeEvent(source, SudokuChangeEventType.CANDIDATES_CHANGED);
        event.userCandidates = userCandidates;
        event.autoCandidates = autoCandidates;
        return event;
    }

    public static SudokuChangeEvent makeAutoCandidateModeEvent(Object source, boolean autoCandidateMode,
            OverrideType[][][] userCandidates, CandidateSet[][] autoCandidates) {
        SudokuChangeEvent event = new SudokuChangeEvent(source, SudokuChangeEventType.AUTO_CANDIDATE_MODE_CHANGED);
        event.autoCandidateMode = autoCandidateMode;
        event.userCandidates = userCandidates;
        event.autoCandidates = autoCandidates;
        return event;
    }

    public static SudokuChangeEvent makeNewGameEvent(Object source, int[][] clues, int[][] guesses,
            CandidateSet[][] autoCandidates, OverrideType[][][] userCandidates) {
        SudokuChangeEvent event = new SudokuChangeEvent(source, SudokuChangeEventType.NEW_GAME);
        event.clues = clues;
        event.guesses = guesses;
        event.autoCandidates = autoCandidates;
        event.userCandidates = userCandidates;
        event.autoCandidateMode = true;
        return event;
    }

    public static SudokuChangeEvent makeWinStateChangeEvent(Object source, boolean userWon) {
        SudokuChangeEvent event = new SudokuChangeEvent(source, SudokuChangeEventType.WIN_STATE_CHANGED);
        event.userWon = userWon;
        return event;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getGuess() {
        return guess;
    }

    public OverrideType[][][] getUserCandidates() {
        return userCandidates;
    }

    public boolean isAutoCandidateMode() {
        return autoCandidateMode;
    }

    public boolean isUserWon() {
        return userWon;
    }

    public CandidateSet[][] getAutoCandidates() {
        return autoCandidates;
    }

    public int[][] getClues() {
        return clues;
    }

    public int[][] getGuesses() {
        return guesses;
    }

    public boolean isInputCandidateMode() {
        return inputCandidateMode;
    }
}
