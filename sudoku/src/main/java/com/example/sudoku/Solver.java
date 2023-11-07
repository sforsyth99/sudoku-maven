package com.example.sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solver {
    String ANSI_RESET = "\u001B[0m";
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";

    int[][] values;
    CandidateSet[][] candidates;

    private Logger logger = LoggerFactory.getLogger("Solver");

    public Solver(int[][] clues) {
        if (clues.length == 0 || clues[0].length == 0)
            throw new IllegalArgumentException("Clues not initialized.");
        // Copy the clues into the values being careful
        // that the inner arrays don't share memory
        values = new int[clues.length][clues[0].length];
        for (int i = 0; i < clues.length; i++)
            for (int j = 0; j < clues[0].length; j++)
                values[i][j] = clues[i][j];

        initCandidates();

    }

    public boolean solve(boolean doForcingChains) {
        while (Sudoku.isSolved(values) != true) {
            boolean progressMade = false;

            // Sole Candidate
            if (solveSoleCandidates()) {
                logger.debug("Solve Sole Candidates");
                progressMade = true;
                logger.debug(SolverHelper.formatSudoku(values));
            }

            if (!progressMade) {
                // Unique Candidate - rows
                logger.debug("Unique candidates - ROWS");
                for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
                    if (solveUniqueCandidates(SolverHelper.makeIndexesForRow(i)))
                        progressMade = true;
                }

                // Unique Candidate - columns
                logger.debug("Unique candidates - COLS");
                for (int i = 0; i < Sudoku.NUM_COLS; i++) {
                    if (solveUniqueCandidates(SolverHelper.makeIndexesForCol(i)))
                        progressMade = true;
                }

                // Unique Candidate - blocks
                logger.debug("Unique candidates - BLOCKS");
                for (int hBlock = 0; hBlock < Sudoku.NUM_H_BLOCKS; hBlock++) {
                    for (int vBlock = 0; vBlock < Sudoku.NUM_V_BLOCKS; vBlock++) {
                        if (solveUniqueCandidates(SolverHelper.makeIndexesForBlockByBlockNum(hBlock, vBlock)))
                            progressMade = true;
                    }
                }
            }

            // Aligned Candidates

            // When all of the X candidates in a row/column are also in the same block,
            // remove those candidates from the rest of the block.

            if (!progressMade) {

                logger.debug("Aligned candidates - ROWS");
                for (int row = 0; row < Sudoku.NUM_ROWS; row++) {
                    if (checkBlocksForAlignedCandidates(SolverHelper.makeIndexesForRow(row)))
                        progressMade = true;
                }

                logger.debug("Aligned candidates - COLS");
                for (int i = 0; i < Sudoku.NUM_COLS; i++) {
                    if (checkBlocksForAlignedCandidates(SolverHelper.makeIndexesForCol(i)))
                        progressMade = true;
                }

                logger.debug("Aligned candidates - BLOCKS");
                for (int hBlock = 0; hBlock < Sudoku.NUM_H_BLOCKS; hBlock++) {
                    for (int vBlock = 0; vBlock < Sudoku.NUM_V_BLOCKS; vBlock++) {
                        if (checkRowsColsForAlignedCandidates(
                                SolverHelper.makeIndexesForBlockByBlockNum(hBlock, vBlock)))
                            progressMade = true;
                    }
                }
            }

            logger.debug(SolverHelper.formatSudoku(values));

            if (!progressMade) {
                // Naked and Hidden tuples - rows
                // Checks for naked and hidden doubles, triples, and quadruples and
                // removes candidates if found
                logger.debug("Tuples - ROWS");
                for (int row = 0; row < Sudoku.NUM_ROWS; row++) {
                    if (updateCandidatesForTuples(SolverHelper.makeIndexesForRow(row)))
                        progressMade = true;
                }

                // Naked and Hidden tuples - columns
                logger.debug("Tuples - COLS");
                for (int i = 0; i < Sudoku.NUM_COLS; i++) {
                    if (updateCandidatesForTuples(SolverHelper.makeIndexesForCol(i)))
                        progressMade = true;
                }

                // Naked and Hidden tuples - blocks
                logger.debug("Tuples - BLOCKS");
                for (int hBlock = 0; hBlock < Sudoku.NUM_H_BLOCKS; hBlock++) {
                    for (int vBlock = 0; vBlock < Sudoku.NUM_V_BLOCKS; vBlock++) {
                        if (updateCandidatesForTuples(SolverHelper.makeIndexesForBlockByBlockNum(hBlock, vBlock)))
                            progressMade = true;
                    }
                }
            }

//          if (!progressMade) {
//              // X-wing
//              if (checkXWing())
//                  progressMade = true;
//          }
            if (!progressMade && doForcingChains) {
                logger.debug(SolverHelper.formatSudoku(values));
                logger.debug(ANSI_RED + "FORCING CHAINS BEGIN");

                if (checkForcingChainsPairs())
                    progressMade = true;
                logger.debug("FORCING CHAINS END" + ANSI_RESET);
                logger.debug(SolverHelper.formatSudoku(values));

            }

//          if (!progressMade) {
//              // Swordfish
//              if (checkSwordfish())
//                  progressMade = true;
//          }

            if (!progressMade) {
                logger.debug(ANSI_RED + "Couldn't find anything else." + ANSI_RESET);
                return false;
            }
        }
        logger.debug("Solved!");
        return true;
    }

    // By the time we get to a forcing chain, there is a good chance that for
    // puzzles that aren't really hard, one of the choices will solve the puzzle.
    private boolean checkForcingChainsPairs() {
        boolean changed = false;
        // Find all the cells with exactly two candidates.
        List<CellIndex> twoCandidateCells = new ArrayList<>();
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                if (candidates[i][j].size() == 2) {
                    twoCandidateCells.add(new CellIndex(i, j));
                }
            }
        }

        // for each of the 2-candidate cells, check the forcing chains until a
        // candidate is found.
        for (int a = 0; a < twoCandidateCells.size(); a++) {
            CellIndex nextTwoCandidateCellIndex = twoCandidateCells.get(a);

            // Find the two possible candidates
            int row = nextTwoCandidateCellIndex.getRow();
            int col = nextTwoCandidateCellIndex.getCol();

            CandidateSet candidateSet = candidates[row][col];
            Object[] candidatesAsArray = candidateSet.toArray();

            Integer optionA = (Integer) candidatesAsArray[0];
            Integer optionB = (Integer) candidatesAsArray[1];

            // make 2 more copies of the puzzle, set each copy to have a
            // different value, and see how far you can get solving them.
            int[][] valuesCopyA = new int[Sudoku.NUM_ROWS][Sudoku.NUM_COLS];
            for (int i = 0; i < valuesCopyA.length; i++) {
                valuesCopyA[i] = values[i].clone();
            }
            valuesCopyA[row][col] = optionA + 1;// storing values as 1-based

            int[][] valuesCopyB = new int[Sudoku.NUM_ROWS][Sudoku.NUM_COLS];
            for (int i = 0; i < valuesCopyB.length; i++) {
                valuesCopyB[i] = values[i].clone();
            }
            valuesCopyB[row][col] = optionB + 1;// storing values as 1-based

            Solver solverA = new Solver(valuesCopyA);
            Solver solverB = new Solver(valuesCopyB);

            logger.debug(ANSI_PURPLE + "Solving A: {},{}. OptionA: {}\n", (row + 1), (col + 1),
                    (optionA + 1));
            solverA.solve(false);
            logger.debug(SolverHelper.formatSudoku(solverA.getValues()));
            logger.debug(
                    ANSI_GREEN + "Solving B: {},{}. OptionA: {}\n", (row + 1), (col + 1), (optionB + 1));
            solverB.solve(false);
            logger.debug(SolverHelper.formatSudoku(solverB.getValues()));
            logger.debug(ANSI_RESET);

            // Look at the solutions - did either choice end up solving the puzzle?
            int[][] solutionA = solverA.getValues();
            int[][] solutionB = solverB.getValues();
            int correctCandidate = -1;

            if (Sudoku.isSolved(solutionA))
                correctCandidate = optionA;

            if (Sudoku.isSolved(solutionB))
                correctCandidate = optionB;

            // See if we can tell if one of the two candidates
            // is the solution. If choosing it solved the puzzle,
            // then it's correct. If choosing it made and invalid puzzle,
            // then the other value is correct.
            if (solverA.hasAnyCellWithEmptyCandidates()) {
                correctCandidate = optionB;
                changed = true;
            } else if (solverB.hasAnyCellWithEmptyCandidates()) {
                correctCandidate = optionA;
                changed = true;
            }

            // TODO: look for other invalid candidates - e.g. same single
            // candidate appearing more than once in a group, three doubles, four triples
            // five quadruples, and more?

            // found the correct candidate, update the puzzle and return.
            if (correctCandidate != -1) {
                updateCellValueAndCandidates(row, col, correctCandidate + 1);// 1-based
                return true;
            } else {
                for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
                    for (int j = 0; j < Sudoku.NUM_COLS; j++) {

                        // the original puzzle's cell is not solved
                        if (values[i][j] == 0) {
                            // And the other two cells have the same
                            // non-zero solution we can solve for that cell.
                            if ((solutionA[i][j] == solutionB[i][j]) && (solutionB[i][j] != 0)) {
                                updateCellValueAndCandidates(i, j, solutionA[i][j]);
                                return true;
                            }
                        }

                    }
                }
            }
        }
        return changed;
    }

    private boolean hasAnyCellWithEmptyCandidates() {
        // returns true if there is a cell with no guess and no
        // candidates - this is an invalid Sudoku
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                if (values[i][j] == 0 && candidates[i][j].size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

//  private boolean checkForcingChainsTriples() {
//      // TODO Do we need to implement forcing chains for triples? Or Quads, etc?
//      // That would only be for very hard puzzles
//      return false;
//  }
//

//  private boolean checkSwordfish() {
//      // TODO Look for the swordfish pattern for very advanced puzzles
//      return false;
//  }
//  private boolean checkXWing() {
//
//      // for each possible candidate 1-9 X
//      // find rows with the following qualities
//      // contains two or more cells with exactly two candidates,
//      // one of which is X
//      // if there is more than one such row
//      // and the cells with the two candidates are in the same column
//      // then we know that the rest of the columns and rest of the rows
//      // can't contain the candidate.
//      return false;
//  }

    private List<CellIndex> getIndexesOfCellsWithCandidate(int candidate, List<CellIndex> indexes) {

        if (candidate < CandidateSet.MIN_CANDIDATE_VALUE || candidate > CandidateSet.MAX_CANDIDATE_VALUE)
            throw new IllegalArgumentException("The candidate is out of range. Value: " + candidate + "Must be betwen "
                    + CandidateSet.MIN_CANDIDATE_VALUE + " and " + CandidateSet.MAX_CANDIDATE_VALUE);

        if (indexes == null)
            throw new IllegalArgumentException("The value indexes cannot be null.");

        List<CellIndex> candidateList = new ArrayList<>();
        // for each cell in the group
        for (int i = 0; i < indexes.size(); i++) {
            int row = indexes.get(i).getRow();
            int col = indexes.get(i).getCol();

            // if that cell has candidate k, count it
            if (candidates[row][col].contains(candidate)) {
                candidateList.add(new CellIndex(row, col));
            }
        }
        return candidateList;
    }
    /*
     * Given a list of row or column indexes, check to see if there are any
     * candidates that appear exactly 2 (or 3) times. If found, we then check to see
     * if they are in the same block. If they are in the same block, then the
     * candidate is removed from the rest of the block.
     */

    private boolean checkBlocksForAlignedCandidates(List<CellIndex> rowOrColIndexes) {

        if (rowOrColIndexes == null || rowOrColIndexes.size() != Sudoku.NUM_ROWS)
            throw new IllegalArgumentException(
                    "The value rowOrColIndexes cannot be null and must contain " + Sudoku.NUM_ROWS + " elements.");

        boolean changed = false;
        // for each possible candidate
        for (int k = 0; k < Sudoku.NUM_CANDIDATES; k++) {
            // find the cells that contain the candidate.
            List<CellIndex> candidateList = getIndexesOfCellsWithCandidate(k, rowOrColIndexes);

            // if there are 2 or 3 cells, check to see if they are all in the same block.
            // if they are, remove the candidate from the rest of the block.
            switch (candidateList.size()) {
            case 2:
                if (SolverHelper.inSameBlock(candidateList.get(0), candidateList.get(1))) {
                    logger.debug(
                            "Row/Col has 2 candidates also in same block. Candidate: {}\nCell 1: {}, {}\nCell 2: {}, {}\n",
                            (k + 1), (candidateList.get(0).getRow() + 1), (candidateList.get(0).getCol() + 1),
                            (candidateList.get(1).getRow() + 1), (candidateList.get(1).getCol() + 1));

                    List<CellIndex> blockCells = SolverHelper.makeIndexesForBlockByCellIndex(
                            candidateList.get(0).getRow(), candidateList.get(0).getCol());
                    if (removeCandidateFromRestOfGroup(k, candidateList, blockCells))
                        changed = true;
                }
                break;
            case 3:
                if (SolverHelper.inSameBlock(candidateList.get(0), candidateList.get(1), candidateList.get(2))) {
                    logger.debug(
                            "Row/Col has 3 candidates also in same block. Candidate: {}\nCell 1: {}, {}\nCell 2 {}, {}\nCell 3: {}, {}\n",
                            (k + 1), (candidateList.get(0).getRow() + 1), (candidateList.get(0).getCol() + 1),
                            (candidateList.get(1).getRow() + 1), (candidateList.get(1).getCol() + 1),
                            (candidateList.get(2).getRow() + 1), (candidateList.get(2).getCol() + 1));

                    List<CellIndex> blockCells = SolverHelper.makeIndexesForBlockByCellIndex(
                            candidateList.get(0).getRow(), candidateList.get(0).getCol());
                    if (removeCandidateFromRestOfGroup(k, candidateList, blockCells))
                        changed = true;
                }
                break;
            }
        }
        return changed;
    }

    /*
     * Given a list of block indexes, check to see if there are any candidates that
     * appear exactly 2 (or 3) times. If found, we then check to see if they are in
     * the same row (column). If they are in the same row (column), then the
     * candidate is removed from the rest of the row (column).
     */

    private boolean checkRowsColsForAlignedCandidates(List<CellIndex> blockIndexes) {

        if (blockIndexes == null || blockIndexes.size() != Sudoku.NUM_CELLS_PER_BLOCK)
            throw new IllegalArgumentException(
                    "The value blockIndexes cannot be null and must have " + Sudoku.NUM_CELLS_PER_BLOCK + " elements.");

        boolean changed = false;
        // for each candidate
        for (int k = 0; k < Sudoku.NUM_CANDIDATES; k++) {
            List<CellIndex> candidateList = getIndexesOfCellsWithCandidate(k, blockIndexes);

            // there are 2 or 3 candidates in the list, check to see if the
            // candidates are all in the same block.
            switch (candidateList.size()) {
            case 2:
                if (SolverHelper.inSameRow(candidateList.get(0), candidateList.get(1))) {
                    logger.debug(
                            "Block has 2 candidates also in same row. Candidate: {}\nCell 1: {},{}\nCell 2: {},{}\n",
                            (k + 1), (candidateList.get(0).getRow() + 1), (candidateList.get(0).getCol() + 1),
                            (candidateList.get(1).getRow() + 1), (candidateList.get(1).getCol() + 1));

                    // List<CellIndex> rowCells =
                    // SolverHelper.makeIndexesForRow(candidateList.get(0).getCol());
                    List<CellIndex> rowCells = SolverHelper.makeIndexesForRow(candidateList.get(0).getRow());
                    if (removeCandidateFromRestOfGroup(k, candidateList, rowCells))
                        changed = true;
                }
                if (SolverHelper.inSameCol(candidateList.get(0), candidateList.get(1))) {
                    logger.debug(
                            "Block has 2 candidates also in same col. Candidate: {}\nCell 1: {},{}\nCell 2: {},{}\n",
                            (k + 1), (candidateList.get(0).getRow() + 1), (candidateList.get(0).getCol() + 1),
                            (candidateList.get(1).getRow() + 1), (candidateList.get(1).getCol() + 1));

                    List<CellIndex> colCells = SolverHelper.makeIndexesForCol(candidateList.get(0).getCol());
                    if (removeCandidateFromRestOfGroup(k, candidateList, colCells))
                        changed = true;
                }
                break;
            case 3:
                if (SolverHelper.inSameRow(candidateList.get(0), candidateList.get(1), candidateList.get(2))) {
                    logger.debug(
                            "Block has 3 candidates also in same row. Candidate: {}\nCell 1: {},{}\nCell 2:{},{}\nCell 3: {}, {}\n",
                            (k + 1), (candidateList.get(0).getRow() + 1), (candidateList.get(0).getCol() + 1),
                            (candidateList.get(1).getRow() + 1), (candidateList.get(1).getCol() + 1),
                            (candidateList.get(2).getRow() + 1), (candidateList.get(2).getCol() + 1));

                    List<CellIndex> rowCells = SolverHelper.makeIndexesForRow(candidateList.get(0).getRow());
                    if (removeCandidateFromRestOfGroup(k, candidateList, rowCells))
                        changed = true;
                }
                if (SolverHelper.inSameCol(candidateList.get(0), candidateList.get(1), candidateList.get(2))) {
                    logger.debug(
                            "Block has 3 candidates also in same col. Candidate: {}\nCell 1: {},{}\nCell 2:{},{}\nCell 3: {}, {}\n",
                            (k + 1), (candidateList.get(0).getRow() + 1), (candidateList.get(0).getCol() + 1),
                            (candidateList.get(1).getRow() + 1), (candidateList.get(1).getCol() + 1),
                            (candidateList.get(2).getRow() + 1), (candidateList.get(2).getCol() + 1));
                    List<CellIndex> colCells = SolverHelper.makeIndexesForCol(candidateList.get(0).getCol());
                    if (removeCandidateFromRestOfGroup(k, candidateList, colCells))
                        changed = true;
                }
                break;
            }
        }
        return changed;
    }

    /*
     * Given an candidate and a list of cells to ignore, removes the candidate from
     * the remaining cells in the group.
     */
    private boolean removeCandidateFromRestOfGroup(int candidateToRemove, List<CellIndex> candidatesToIgnore,
            List<CellIndex> cells) {

        if (candidateToRemove < CandidateSet.MIN_CANDIDATE_VALUE
                || candidateToRemove > CandidateSet.MAX_CANDIDATE_VALUE)
            throw new IllegalArgumentException(
                    "The candidate is out of range. Value: " + candidateToRemove + "Must be betwen "
                            + CandidateSet.MIN_CANDIDATE_VALUE + " and " + CandidateSet.MAX_CANDIDATE_VALUE);

        if (candidatesToIgnore == null)
            throw new IllegalArgumentException("candidatesToIgnore cannot be null.");

        if (cells == null || cells.size() != Sudoku.NUM_ROWS)
            throw new IllegalArgumentException(
                    "List of provided cells should contain " + Sudoku.NUM_ROWS + "elements.");

        boolean changed = false;

        for (CellIndex colCell : cells) {
            boolean ignoreThisBlock = false;
            for (CellIndex candidateToIgnore : candidatesToIgnore) {
                if (candidateToIgnore.equals(colCell))
                    ignoreThisBlock = true;
            }
            if (!ignoreThisBlock) {
                // remove the candidate
                if (candidates[colCell.getRow()][colCell.getCol()].contains(candidateToRemove)) {
                    candidates[colCell.getRow()][colCell.getCol()].remove(candidateToRemove);
                    changed = true;
                    logger.debug("Removed candidate: {} from cell {},{}\n", (candidateToRemove + 1),
                            (colCell.getRow() + 1), (colCell.getCol() + 1));
                }
            }
        }
        return changed;
    }

    // Look for any cells that have only one candidate and solve them.
    private boolean solveSoleCandidates() {
        // for each cell in the Sudoku
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                // if there is exactly one candidate an the cell is not a guess
                // then solve the cell. (Solving the cell updates candidates)
                if (candidates[i][j].size() == 1 && values[i][j] == 0) {
                    CandidateSet candidateSet = candidates[i][j];
                    Object[] candidatesAsArray = candidateSet.toArray();
                    Integer theCandidate = (Integer) candidatesAsArray[0];
                    updateCellValueAndCandidates(i, j, (theCandidate + 1));
                    logger.debug(
                            "Single candidate: {} at {},{}\n", (theCandidate + 1), (i + 1), (j + 1));
                    return true;
                }
            }
        }
        return false;
    }

    private void updateCellValueAndCandidates(int row, int col, int value) {
        // values show 1-based indexes -- change this so
        // only the UI does the translation.

        values[row][col] = value;
        candidates[row][col].clear();

        // update candidates for row
        List<CellIndex> rowIndexes = SolverHelper.makeIndexesForRow(row);
        updateCandidatesForSingle(rowIndexes, value - 1);

        // update candidate for column
        List<CellIndex> colIndexes = SolverHelper.makeIndexesForCol(col);
        updateCandidatesForSingle(colIndexes, value - 1);

        // update candidates for block
        List<CellIndex> blockIndexes = SolverHelper.makeIndexesForBlockByCellIndex(row, col);
        updateCandidatesForSingle(blockIndexes, value - 1);
    }

    /*
     * Removes the value as a possible candidate from each cell in indexes.
     */
    private void updateCandidatesForSingle(List<CellIndex> indexes, int value) {
        for (CellIndex index : indexes) {
            candidates[index.getRow()][index.getCol()].remove(value);
        }
    }

    private boolean findAndRemoveHiddenTuple(List<CellIndex> indexes, List<Integer> tuples) {

        int numTuples = tuples.size();
        boolean changed = false;

        List<CellIndex> cellsWithAllTuples = new ArrayList<>();

        // for each cell, add it to the list if it contains ALL the candidates in tuple
        for (CellIndex cellIndex : indexes) {
            int row = cellIndex.getRow();
            int col = cellIndex.getCol();

            CandidateSet candidateSet = candidates[row][col];

            // does this cell's candidate contain all the values in tuple?
            boolean containsAllTuples = true;
            for (Integer tuple : tuples) {
                if (!candidateSet.contains(tuple))
                    containsAllTuples = false;
            }
            if (containsAllTuples)
                cellsWithAllTuples.add(cellIndex);
        }
        // Return now if there isn't 3 cells with all 3 tuples, 2 cells with both
        // tuples, etc.
        if (cellsWithAllTuples.size() != tuples.size())
            return false;

        // for each tuple count up how many times it occurs in the set. If it occurs
        // more than n times, then return false.
        for (Integer tuple : tuples) {
            int nOccurrences = 0;
            for (CellIndex cellIndex : indexes) {
                int row = cellIndex.getRow();
                int col = cellIndex.getCol();
                CandidateSet candidateSet = candidates[row][col];
                if (candidateSet.contains(tuple))
                    nOccurrences++;
            }
            if (nOccurrences != numTuples)
                return false;
        }

        // We've found a hidden tuple. Remove the other
        // candidates from the cells that have the hidden tuples.
        changed = removeCandidatesExcept(cellsWithAllTuples, tuples);
        return changed;
    }

    private boolean removeCandidatesExcept(List<CellIndex> cells, List<Integer> values) {

        int row = -1;
        int col = -1;
        boolean changed = false;

        // for each cell in the list remove other candidates than those in
        // values
        for (CellIndex cell : cells) {
            row = cell.getRow();
            col = cell.getCol();
            CandidateSet candidateSet = candidates[row][col];

            // Iterate over the candidate set and build up a list of the values
            // that are in the candidate set but not in values. Do not remove
            // them from the candidate set while iterating over it because that ca
            // cause an exception.
            Iterator<Integer> iterator = candidateSet.iterator();
            List<Integer> valuesToRemove = new ArrayList<Integer>();
            while (iterator.hasNext()) {
                Integer value = iterator.next();
                if (!values.contains(value))
                    valuesToRemove.add(value);
            }

            // remove the values from the candidate set.
            for (int v : valuesToRemove) {
                candidateSet.remove(v);
            }
        }

        return changed;
    }

    private boolean findAndRemoveNakedTuple(List<CellIndex> cellIndexes, List<Integer> tuples) {

        List<CellIndex> cellsToRemoveValuesFrom = new ArrayList<>();
        List<CellIndex> cellsWithAllTuples = new ArrayList<>();
        int numTuples = tuples.size();
        for (CellIndex cell : cellIndexes) {
            int rowN = cell.getRow();
            int colN = cell.getCol();

            if (values[rowN][colN] != 0) {
                // cell is a guess - ignore
                continue;
            }
            if (!cellCandidatesContainOthers(cell, tuples)) {
                cellsWithAllTuples.add(cell);
            } else {
                cellsToRemoveValuesFrom.add(cell);
            }
        }

        if (cellsWithAllTuples.size() == numTuples) {
            if (removeTupleCandidates(cellsToRemoveValuesFrom, tuples))
                return true;
        }
        return false;
    }

    // For each cell that we need to remove the tuple from, check to
    // see if it has a value, and if it doesn't attempt to remove the
    // tuple value. Return true if a change was made.
    private boolean removeTupleCandidates(List<CellIndex> cellsToRemoveValuesFrom, List<Integer> valuesToRemove) {
        boolean changed = false;

        for (CellIndex cell : cellsToRemoveValuesFrom) {

            int row = cell.getRow();
            int col = cell.getCol();

            // if the cell is solved, ignore it.
            if (values[row][col] != 0)
                continue;

            for (Integer valueToRemove : valuesToRemove) {
                if (candidates[row][col].contains(valueToRemove)) {
                    candidates[row][col].remove(valueToRemove);
                    changed = true;
                    logger.debug("Removed candidate {} from cell {}, {}\n", valueToRemove, row, col);
                }
            }
        }
        return changed;
    }

    // returns true if the cell at index contains candidates OTHER than the ones in
    // the
    // provided tuples.
    private boolean cellCandidatesContainOthers(CellIndex index, List<Integer> tuples) {
        int numItems = tuples.size();
        assert ((numItems >= 2) && (numItems <= 4));

        int row = index.getRow();
        int col = index.getCol();

        CandidateSet candidateSet = candidates[row][col];
        for (int candidate : candidateSet) {
            if (!(tuples.contains(candidate))) {
                return true;
            }
        }

        return false;
    }

    /*
     * Returns a list of all candidates that are in the provided cells
     */
    private List<Integer> getAllCandidates(List<CellIndex> indexes) {
        HashSet<Integer> allCandidates = new HashSet<>();
        for (CellIndex index : indexes) {
            CandidateSet candidateSet = candidates[index.getRow()][index.getCol()];
            for (Integer intCandidate : candidateSet)
                allCandidates.add(intCandidate);
        }
        return new ArrayList<>(allCandidates);
    }

    private boolean updateCandidatesForTuples(List<CellIndex> indexes) {

        boolean changed = false;

        List<Integer> cellCandidates = getAllCandidates(indexes);
        int n = cellCandidates.size();

        // Use a bit-shifting algorithm to create all of the subsets of
        // the values in indexes. If the subset has 2, 3, or 4 elements
        // we will look for hidden and naked tuples. We don't do this for
        // tuples of size 1 because they are easily removed in another step.
        // We don't do this for values > 4 because it is not necessary.
        for (int i = 0; i < (1 << n); i++) {

            int bitCount = Integer.bitCount(i);
            if (bitCount == 2 || bitCount == 3 || bitCount == 4) {
                List<Integer> subsetsInt = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) > 0) {
                        subsetsInt.add(cellCandidates.get(j));
                    }
                }
                if (findAndRemoveNakedTuple(indexes, subsetsInt))
                    changed = true;
                if (findAndRemoveHiddenTuple(indexes, subsetsInt))
                    changed = true;
            }
        }
        return changed;
    }

    private boolean solveUniqueCandidates(List<CellIndex> indexes) {
        boolean changed = false;

        int[] count = new int[Sudoku.NUM_CANDIDATES];

        // Go through each cell and count up the occurrences of each candidate.
        for (CellIndex cell : indexes) {
            int row = cell.getRow();
            int col = cell.getCol();

            if (values[row][col] == 0) {
                for (int k = 0; k < Sudoku.NUM_CANDIDATES; k++) {
                    if (candidates[row][col].contains(k))
                        count[k] = count[k] + 1;
                }
            }
        }

        // go through each counted element and look for count == 1
        for (int a = 0; a < count.length; a++) {
            if (count[a] == 1) {
                // there is only one element of item a
                // now go back and find it. (could you just remember it?)

                for (CellIndex m : indexes) {
                    int row = m.getRow();
                    int col = m.getCol();

                    // if no guess and has the value (we know it's the only value since there is
                    // only 1)
                    if ((values[row][col] == 0) && (candidates[row][col].contains(a))) {
                        logger.debug("Cell {},{}: has the only {} in the group.\n", (row + 1), (col + 1),
                                (a + 1));
                        updateCellValueAndCandidates(row, col, (a + 1));
                        changed = true;
                    }
                }
            }
        }

        return changed;

    }

    private void initCandidates() {

        // Allocate the memory for the 2x2 array of CandidateSet
        candidates = new CandidateSet[Sudoku.NUM_ROWS][Sudoku.NUM_COLS];
        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                candidates[i][j] = new CandidateSet();
            }
        }

        for (int i = 0; i < Sudoku.NUM_ROWS; i++) {
            for (int j = 0; j < Sudoku.NUM_COLS; j++) {
                if (values[i][j] == 0) {
                    for (int k = 0; k < Sudoku.NUM_CANDIDATES; k++) {
                        int value = k + 1;
                        if (!(Sudoku.rowHasValue(values, i, value) || //
                                Sudoku.colHasValue(values, j, value) || //
                                Sudoku.blockHasValue(values, i, j, value))) {
                            candidates[i][j].add(k);
                        }
                    }
                }
            }
        }
    }

    public int[][] getValues() {
        return values;
    }
}