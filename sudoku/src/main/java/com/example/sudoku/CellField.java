package com.example.sudoku;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextField;

/*
 * Extends JTextField so that it knows it's row and column 
 * position in the Sudoku. This makes it easier to respond
 * to mouse and key events.
 */

@SuppressWarnings("serial")
public class CellField extends JTextField {

    private int rowIndex;
    private int colIndex;
    private boolean isError = false;

    CellField(int rowIndex, int colIndex) {

        if (rowIndex < 0 || rowIndex > 8)
            throw new IllegalArgumentException("Invalid row number");
        if (colIndex < 0 || colIndex > 8)
            throw new IllegalArgumentException("Invalid column number");

        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw a red dot at bottom corner if it is an error
        if (isError) {
            int x = getWidth() - 15;
            int y = getHeight() - 15;
            g.setColor(Color.RED);
            g.fillOval(x, y, 10, 10);
        }
    }

}
