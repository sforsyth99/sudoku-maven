package com.example.sudoku;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")

public class SudokuGridUI extends JFrame {

    Sudoku.ViewChangeListener sudokuViewListener = null;

    private static final Color BORDER_COLOR = Color.GRAY;
    private static final int THIN_BORDER_WIDTH = 1;
    private static final int THICK_BORDER_WIDTH = 4;

    private static final Color FRAME_BACKGROUND_COLOR = Color.WHITE;

    private static final Dimension SCREEN_SIZE = new Dimension(500, 700);

    // UI components
    CellPanel[][] cells = new CellPanel[Sudoku.NUM_ROWS][Sudoku.NUM_COLS];
    JTextField youWonLabel = null;
    JCheckBox candidateInputCheckBox = null; // TODO: replace with a keyboard
    JCheckBox autoCandidateCheckBox = null;
    JButton newGameButton = null;
    JButton solveGameButton = null;

    boolean displayAutoCandidates = true;

    // Listeners
    KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                // set the cell value
                sudokuViewListener.keyPressed(Character.getNumericValue(e.getKeyChar()));
                break;
            case '0':
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_ESCAPE:
                // clear the cell
                sudokuViewListener.keyPressed(0);
                break;

            }
        }
    };

    MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            int row = 0;
            int col = 0;

            Object source = e.getSource();
            if (source instanceof CellField) {
                row = ((CellField) source).getRowIndex();
                col = ((CellField) source).getColIndex();
                sudokuViewListener.selectedCellChanged(row, col);
            } else if (source instanceof CellPanel) {
                row = ((CellPanel) source).getRowIndex();
                col = ((CellPanel) source).getColIndex();
                sudokuViewListener.selectedCellChanged(row, col);
            }
        }
    };

    public SudokuGridUI(Sudoku.ViewChangeListener sudokuViewListener, int[][] clues, CandidateSet[][] autoCandidates,
            boolean inputCandidateMode, boolean autoCandidateMode) {

        this.sudokuViewListener = sudokuViewListener;

        initUI(clues, autoCandidates, inputCandidateMode, autoCandidateMode);

        sudokuViewListener.addSudokuChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateGridChanges(e);
            }
        });
        candidateInputCheckBox.addKeyListener(keyListener);
        candidateInputCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    sudokuViewListener.inputCandidateModeChanged(true);
                else
                    sudokuViewListener.inputCandidateModeChanged(false);
            }
        });

        autoCandidateCheckBox.addKeyListener(keyListener);
        autoCandidateCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // set auto candidate mode in the Sudoku
                if (e.getStateChange() == ItemEvent.SELECTED)
                    sudokuViewListener.autoCandidateModeChanged(true);
                else
                    sudokuViewListener.autoCandidateModeChanged(false);
            }
        });

        // In case this label has the focus, add a key listener
        youWonLabel.addKeyListener(keyListener);
    }

    private void initUI(int[][] clues, CandidateSet[][] candidates, boolean inputCandidateMode,
            boolean autoCandidateMode) {

        JPanel mainFrame = new JPanel(new GridBagLayout());

        // Set up the main screen
        mainFrame.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, THIN_BORDER_WIDTH));
        mainFrame.setBackground(FRAME_BACKGROUND_COLOR);

        JPanel sudokuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Add the Sudoku grid
        for (int row = 0; row < Sudoku.NUM_ROWS; row++) {
            boolean thickBottomBorder = false;
            boolean thickTopBorder = false;

            if (row % Sudoku.ROWS_PER_BLOCK == (Sudoku.ROWS_PER_BLOCK - 1))
                thickBottomBorder = true; // Thick bottom border after 3rd, 6th, 8th row
            if (row == 0)
                thickTopBorder = true; // Thick top border before first row

            for (int col = 0; col < Sudoku.NUM_COLS; col++) {
                boolean thickRightBorder = false;
                boolean thickLeftBorder = false;
                if (col % Sudoku.COLS_PER_BLOCK == (Sudoku.COLS_PER_BLOCK - 1)) // Thick right border after 3rd, 6th
                                                                                // 8th, column
                    thickRightBorder = true;
                if (col == 0)
                    thickLeftBorder = true; // Thick left border before first column

                CellPanel cell = new CellPanel(row, col, mouseListener, keyListener);

                // If the cell is a clue, display the clue
                if (clues[row][col] != 0)
                    cell.showClue(Integer.toString(clues[row][col]));
                // Otherwise, display the candidates
                else
                    cell.showCandidates(candidates[row][col]);

                // Set the border of the cell
                cell.setBorder(BorderFactory.createMatteBorder(thickTopBorder ? THICK_BORDER_WIDTH : THIN_BORDER_WIDTH,
                        thickLeftBorder ? THICK_BORDER_WIDTH : THIN_BORDER_WIDTH,
                        thickBottomBorder ? THICK_BORDER_WIDTH : THIN_BORDER_WIDTH,
                        thickRightBorder ? THICK_BORDER_WIDTH : THIN_BORDER_WIDTH, BORDER_COLOR));

                gbc = new GridBagConstraints();
                gbc.gridx = col;
                gbc.gridy = row;

                sudokuPanel.add(cell, gbc);
                cells[row][col] = cell;

            }
        }

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainFrame.add(sudokuPanel, gbc);

        youWonLabel = new JTextField("");
        youWonLabel.setPreferredSize(new Dimension(200, 30));
        youWonLabel.setEditable(false);
        youWonLabel.setBorder(BorderFactory.createEmptyBorder());
        youWonLabel.setBackground(FRAME_BACKGROUND_COLOR);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        mainFrame.add(youWonLabel, gbc);

        candidateInputCheckBox = new JCheckBox("Pencil Mark Input Mode");
        candidateInputCheckBox.setSelected(inputCandidateMode);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        mainFrame.add(candidateInputCheckBox, gbc);

        autoCandidateCheckBox = new JCheckBox("Auto Pencil Marks");
        autoCandidateCheckBox.setSelected(autoCandidateMode);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        mainFrame.add(autoCandidateCheckBox, gbc);

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudokuViewListener.newGame();
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        mainFrame.add(newGameButton, gbc);

        solveGameButton = new JButton("Solve Game");
        solveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudokuViewListener.solvePuzzle();
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        mainFrame.add(solveGameButton, gbc);

        add(mainFrame);
        setTitle("Sudoku");

        setSize(SCREEN_SIZE);
        setMinimumSize(SCREEN_SIZE);
        setMaximumSize(SCREEN_SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        revalidate();
        repaint();
    }

    private void selectionChanged(SudokuChangeEvent myChangeEvent) {

        int row = myChangeEvent.getRow();
        int col = myChangeEvent.getCol();
        int[][] clues = myChangeEvent.getClues();

        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                CellPanel cell = findCell(i, j);
                // if the cell is a clue, skip over it.
                if (clues[i][j] == 0) {
                    if (i == row && j == col) {
                        // this is the selected sell
                        cell.setHighlighted(true);
                        cell.requestFocus();
                    } else
                        // all other cells
                        cell.setHighlighted(false);
                }
            }
        }

        revalidate();
        repaint();
    }

    private void updateGridChanges(ChangeEvent e) {

        SudokuChangeEvent.SudokuChangeEventType changeType = SudokuChangeEvent.SudokuChangeEventType.NO_CHANGE;

        SudokuChangeEvent myChangeEvent = null;
        if (e instanceof SudokuChangeEvent) {
            myChangeEvent = (SudokuChangeEvent) e;
            changeType = myChangeEvent.getChangeType();
        } else
            throw new IllegalArgumentException();

        switch (changeType) {
        case SELECTED_CELL_CHANGED:
            selectionChanged(myChangeEvent);
            break;
        case GUESS_CHANGED:
            guessChanged(myChangeEvent);
            break;
        case CANDIDATES_CHANGED:
            candidatesChanged(myChangeEvent);
            break;
        case WIN_STATE_CHANGED:
            winStateChanged(myChangeEvent);
            break;
        case AUTO_CANDIDATE_MODE_CHANGED:
            autoCandidateModeChanged(myChangeEvent);
            break;
        case INPUT_CANDIDATE_MODE_CHANGED:
            inputCandidateModeChanged(myChangeEvent);
            break;
        case NO_CHANGE:
            break;
        case NEW_GAME:
            newGame(myChangeEvent);
        default:
            break;
        }
    }

    private void guessChanged(SudokuChangeEvent changeEvent) {

        int[][] clues = changeEvent.getClues();
        int[][] guesses = changeEvent.getGuesses();
        int selectedRow = changeEvent.getRow();
        int selectedCol = changeEvent.getCol();

        // Update the selected cell with the current guess,check other cells
        // for errors, and update candidates if in auto candidate mode
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                CellPanel cell = findCell(i, j);
                int value = guesses[i][j];

                // The cell is not a clue, decide whether to
                // show a guess or candidates.
                if (clues[i][j] == 0) {

                    // This is the cell whose guess has changed
                    if (i == selectedRow && j == selectedCol) {
                        cell.setGuessValue(value);
                        if (value == 0) {
                            // if the value has been deleted, show candidates
                            cell.showCandidates();

                        } else {
                            // if it's a new guess, check for errors and display value
                            cell.showGuess();
                        }
                    } else {
                        // The cell displays candidates, update them if in auto candidate mode

                        // if (value == 0 && sudoku.isAutoCandidateMode())
                        if (value == 0 && displayAutoCandidates)
                            cell.showCandidates();

                    }
                }

                // if the cell is an error, display an error
                cell.setIsError(Sudoku.isMistake(i, j, guesses)); // TODO: OK b/c guesses contain clues?

                cell.revalidate();
                cell.repaint();
            }
        }
        revalidate();
        repaint();
    }

    // if a user candidates has changed, set the auto candidates
    private void candidatesChanged(SudokuChangeEvent event) {

        CandidateSet[][] autoCandidatesAll = event.getAutoCandidates();
        Sudoku.OverrideType[][][] userCandidatesAll = event.getUserCandidates();

        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {

                CandidateSet autoCandidates = autoCandidatesAll[i][j];

                Sudoku.OverrideType[] userCandidates = userCandidatesAll[i][j];

                CandidateSet cellCandidates = new CandidateSet();

                if (displayAutoCandidates)
                    cellCandidates.addAll(autoCandidates);
                else {
                    cellCandidates = new CandidateSet();
                }
                for (int k = 0; k < Sudoku.NUM_CANDIDATES; k++) {
                    Sudoku.OverrideType userCandidateValue = userCandidates[k];
                    if (userCandidateValue == Sudoku.OverrideType.OVERRIDE_FALSE)
                        cellCandidates.remove(k);
                    if (userCandidateValue == Sudoku.OverrideType.OVERRIDE_TRUE)
                        cellCandidates.add(k);
                }
                CellPanel cell = cells[i][j];
                cell.setCandidates(cellCandidates);
            }
        }

        revalidate();
        repaint();
    }

    private void newGame(SudokuChangeEvent event) {
        // initUI(event.getClues(), event.getAutoCandidates(), false, false);

        int[][] clues = event.getClues();
        CandidateSet autoCandidates[][] = event.getAutoCandidates();
        // Add the Sudoku grid
        for (int row = 0; row < Sudoku.NUM_ROWS; row++) {
            for (int col = 0; col < Sudoku.NUM_COLS; col++) {
                CellPanel cell = cells[row][col];
                // If the cell is a clue, display the clue
                if (clues[row][col] != 0) {
                    cell.showClue(Integer.toString(clues[row][col]));
                }
                // Otherwise, display the candidates
                else
                    cell.showCandidates(autoCandidates[row][col]);
                // if the cell is an error, display an error
                // cell.setIsError(Sudoku.isMistake(row, col, event.getGuesses()));
                cell.setIsError(false);// assumes new puzzles have no errors.
            }
        }

        revalidate();
        repaint();

    }

    // This is not necessary when there is just one view
    private void inputCandidateModeChanged(SudokuChangeEvent event) {
        boolean isSelected = event.isInputCandidateMode();
        if (isSelected != candidateInputCheckBox.isSelected())
            candidateInputCheckBox.setSelected(event.isInputCandidateMode()); // in case there is more than one view
    }

    private void autoCandidateModeChanged(SudokuChangeEvent event) {

        displayAutoCandidates = event.isAutoCandidateMode();
        if (displayAutoCandidates != autoCandidateCheckBox.isSelected())
            autoCandidateCheckBox.setSelected(displayAutoCandidates); // in case there is more than one view

        candidatesChanged(event); // TODO: Use a different method to swap display to/from autoCandidateMode
    }

    private void winStateChanged(SudokuChangeEvent event) {
        boolean solved = event.isUserWon();
        if (solved) {
            youWonLabel.setText("You won!");
            youWonLabel.setHorizontalAlignment(JTextField.CENTER);
        }

    }

    private CellPanel findCell(int i, int j) {
        return cells[i][j];
    }
}