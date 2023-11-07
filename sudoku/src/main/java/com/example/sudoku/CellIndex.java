package com.example.sudoku;

public class CellIndex {

    public static final int INDEX_LOWER_BOUND = 0;
    public static final int INDEX_UPPER_BOUND = (Sudoku.NUM_ROWS - 1);
    private int row;
    private int col;

    public CellIndex(int row, int col) {
        if (row < INDEX_LOWER_BOUND || row > INDEX_UPPER_BOUND)
            throw new IllegalArgumentException("Row index must be between" + INDEX_LOWER_BOUND + " and " + INDEX_UPPER_BOUND);

        if (col < INDEX_LOWER_BOUND || col > INDEX_UPPER_BOUND)
            throw new IllegalArgumentException("Column index must be between" + INDEX_LOWER_BOUND + " and " + INDEX_UPPER_BOUND);
        
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col; 
    }

    public boolean equals(CellIndex other) {
        if ((this.row == other.row) && (this.col == other.col))
            return true;
        return false;
    }

}
