package com.example.sudoku;

import java.util.List;
import java.util.ArrayList;

public class SolverHelper {

    public static String formatSudoku(int[][] array) {

        StringBuilder sb = new StringBuilder();
        // for each row
        String rowDelimiter = "\n ------- ------- -------\n";
        sb.append(rowDelimiter);
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            // for each cell
            sb.append("| ");
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                int val = array[i][j];
                if (val == 0)
                    sb.append("\u2022"); // bullet point
                else
                    sb.append(val);

                sb.append(' ');
                if (j % 3 == 2)
                    if (j < 8)
                        sb.append("| ");
                    else
                        sb.append("|\n");
            }
            if (i % 3 == 2 && i < 8) {
                sb.append(rowDelimiter);
            }
        }
        sb.append(rowDelimiter);
        return sb.toString();
    }

    public static List<CellIndex> makeIndexesForRow(int row) {
        List<CellIndex> index = new ArrayList<>();
        for (int j = 0; j < Sudoku.NUM_COLS; j++) {
            CellIndex nextIndex = new CellIndex(row, j);
            index.add(nextIndex);
        }
        return index;
    }

    public static List<CellIndex> makeIndexesForCol(int col) {
        List<CellIndex> index = new ArrayList<>();
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            CellIndex nextIndex = new CellIndex(i, col);
            index.add(nextIndex);
        }
        return index;
    }

    // Do we want to use the block index or the cell number? Or both?
    public static List<CellIndex> makeIndexesForBlockByBlockNum(int hBlock, int vBlock) {
        List<CellIndex> index = new ArrayList<>();
        for (int i = 0; i < Sudoku.ROWS_PER_BLOCK; i++) {
            for (int j = 0; j < Sudoku.COLS_PER_BLOCK; j++) {
                int rowN = i + (hBlock * Sudoku.ROWS_PER_BLOCK);
                int colN = j + (vBlock * Sudoku.COLS_PER_BLOCK);

                CellIndex nextIndex = new CellIndex(rowN, colN);
                index.add(nextIndex);
            }
        }
        return index;
    }

    public static List<CellIndex> makeIndexesForBlockByCellIndex(int row, int col) {
        // solve any row, column, or block where a candidate value appears only once.
        int hBlock = row / 3;
        int vBlock = col / 3;
        return (makeIndexesForBlockByBlockNum(hBlock, vBlock));
    }

    public static boolean inSameRow(CellIndex a, CellIndex b) {
        return (a.getRow() == b.getRow());
    }

    public static boolean inSameRow(CellIndex a, CellIndex b, CellIndex c) {
        return (inSameRow(a, b) && inSameRow(b, c));
    }

    public static boolean inSameCol(CellIndex a, CellIndex b) {
        return (a.getCol() == b.getCol());
    }

    public static boolean inSameCol(CellIndex a, CellIndex b, CellIndex c) {
        return (inSameCol(a, b) && inSameCol(b, c));
    }

    public static CellIndex getBlockIndexFromCellIndex(CellIndex cellIndex) {
        int blockRow = cellIndex.getRow() / 3;
        int blockCol = cellIndex.getCol() / 3;
        CellIndex blockIndex = new CellIndex(blockRow, blockCol);
        return blockIndex;
    }

    public static boolean inSameBlock(CellIndex a, CellIndex b) {
        // tell if the two indexes are in the same block.
        return ((getBlockIndexFromCellIndex(a).getRow() == getBlockIndexFromCellIndex(b).getRow())
                && (getBlockIndexFromCellIndex(a).getCol() == getBlockIndexFromCellIndex(b).getCol()));
    }

    public static boolean inSameBlock(CellIndex a, CellIndex b, CellIndex c) {
        // tell if the three indexes are in the same block
        return (inSameBlock(a, b) && inSameBlock(b, c));
    }
}
