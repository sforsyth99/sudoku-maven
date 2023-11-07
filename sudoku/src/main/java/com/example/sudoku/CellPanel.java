package com.example.sudoku;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CellPanel extends JPanel {
    private int rowIndex;
    private int colIndex;
    private CellField guessField; // stores 1 text field - the guess or clue
    private CellField[] candidateFields; // stores 9 text fields - the candidates

    enum DisplayType {
        CELL_NOT_INIT, CELL_CANDIDATES, CELL_GUESS, CELL_CLUE
    };

    private DisplayType displayType = DisplayType.CELL_NOT_INIT;

    public static final Color CLUE_BACKGROUND_COLOR = new Color(230, 230, 230); // light gray
    public static final Color HIGHLIGHT_COLOR = Color.YELLOW;
    public static final Color FONT_COLOR = Color.BLACK;
    public static final Color ERROR_COLOR = Color.RED;
    public static final Color BACKGROUND_COLOR = Color.WHITE;
    public static final Color CANDIDATE_FONT_COLOR = Color.GRAY;

    public static final Font CANDIDATE_FONT = new Font("Helvetica Neue", Font.BOLD, 8);
    public static final Font GUESS_AND_CLUE_FONT = new Font("Helvetica Neue", Font.BOLD, 20);

    public static final Dimension CELL_DIMENSION = new Dimension(50, 50);
    public static final Dimension CANDIDATE_DIMENSION = new Dimension(15, 15);

    CellPanel(int row, int col, MouseListener mListener, KeyListener kListener) {
        this.rowIndex = row;
        this.colIndex = col;
        addMouseListener(mListener);
        addKeyListener(kListener);

        setLayout(new GridBagLayout());
        setSize(CELL_DIMENSION);
        setPreferredSize(CELL_DIMENSION);
        setMaximumSize(CELL_DIMENSION);
        setMinimumSize(CELL_DIMENSION);
        setBackground(BACKGROUND_COLOR);

        // Initialize the guess/clue field
        guessField = new CellField(row, col);
        guessField.addMouseListener(mListener);
        guessField.addKeyListener(kListener);
        guessField.setEditable(false);
        guessField.setFont(GUESS_AND_CLUE_FONT);
        guessField.setForeground(FONT_COLOR);
        guessField.setBackground(BACKGROUND_COLOR);
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setPreferredSize(CELL_DIMENSION);
        guessField.setMinimumSize(CELL_DIMENSION);
        guessField.setBorder(BorderFactory.createEmptyBorder());

        // Initialize the candidate fields
        candidateFields = new CellField[Sudoku.NUM_CANDIDATES];
        for (int i = 0; i < Sudoku.ROWS_PER_BLOCK; i++) {
            for (int j = 0; j < Sudoku.COLS_PER_BLOCK; j++) {
                int displayValueAsInt = i * Sudoku.ROWS_PER_BLOCK + j + 1;
                String displayValue = Integer.toString(displayValueAsInt);

                CellField textField = new CellField(row, col);
                textField.setText(displayValue);
                textField.addMouseListener(mListener);
                textField.addKeyListener(kListener);
                textField.setEditable(false);
                textField.setFont(CANDIDATE_FONT);
                textField.setForeground(CANDIDATE_FONT_COLOR);
                textField.setBorder(BorderFactory.createEmptyBorder());
                textField.setBackground(BACKGROUND_COLOR);
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setPreferredSize(CANDIDATE_DIMENSION);

                candidateFields[displayValueAsInt - 1] = textField;
            }
        }
    }

    private void removeSubComponents() {
        remove(guessField);
        for (int i = 0; i < Sudoku.NUM_CANDIDATES; i++) {
            remove(candidateFields[i]);
        }
    }

    public void showGuess() {
        if (displayType == DisplayType.CELL_GUESS)
            return;

        removeSubComponents();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(guessField, gbc);

        displayType = DisplayType.CELL_GUESS;
    }

    public void showClue(String clueValue) {

        // If the field is a clue field and already shows the same value
        // then return - no need to reconstruct the subcomponents
        if (guessField.getText().equalsIgnoreCase(clueValue) && (displayType == DisplayType.CELL_CLUE))
            return;

        guessField.setText(clueValue);
        guessField.setBackground(CLUE_BACKGROUND_COLOR);

        removeSubComponents();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(guessField, gbc);
        displayType = DisplayType.CELL_CLUE;
    }

    // This method should be replaced
    public void showCandidates(CandidateSet theCandidateList) {
        setCandidates(theCandidateList);
        showCandidates();
    }

    public void showCandidates() {

        // if showing text field remove it and add candidates.
        if (displayType == DisplayType.CELL_CANDIDATES)
            return;

        removeSubComponents();

        for (int i = 0; i < Sudoku.ROWS_PER_BLOCK; i++) {
            for (int j = 0; j < Sudoku.COLS_PER_BLOCK; j++) {
                GridBagConstraints candidateGBC = new GridBagConstraints();
                candidateGBC.weightx = 1;
                candidateGBC.weighty = 1;
                candidateGBC.gridx = j;
                candidateGBC.gridy = i;

                int num = i * Sudoku.ROWS_PER_BLOCK + j;
                CellField textField = candidateFields[num];
                add(textField, candidateGBC);
            }

        }
        displayType = DisplayType.CELL_CANDIDATES;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    private void setPanelColor(Color color) {
        setBackground(color);
        guessField.setBackground(color);
        for (CellField cf : candidateFields)
            cf.setBackground(color);

    }

    public void setIsError(boolean isError) {
        guessField.setError(isError);
    }

    public void setHighlighted(boolean isHighlighted) {
        if (isHighlighted)
            setPanelColor(HIGHLIGHT_COLOR);
        else
            setPanelColor(BACKGROUND_COLOR);

        repaint();
    }

    public void setCandidates(CandidateSet candidateList) {
        for (int i = 0; i < candidateFields.length; i++) {
            CellField cellField = candidateFields[i];
            if (candidateList.contains(i))
                cellField.setText(Integer.toString(i + 1));
            else
                cellField.setText(" ");
        }
    }

    public void setGuessValue(int value) {
        // sets the cell value.
        if (value == 0)
            guessField.setText(" ");
        else
            guessField.setText(Integer.toString(value));

        requestFocus();
    }
}
