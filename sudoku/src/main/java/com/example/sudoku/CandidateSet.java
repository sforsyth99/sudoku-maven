package com.example.sudoku;

import java.util.HashSet;

@SuppressWarnings("serial")
public class CandidateSet extends HashSet<Integer> {

    public static final int MIN_CANDIDATE_VALUE = 0;
    public static final int MAX_CANDIDATE_VALUE = 8;

    @Override
    public boolean add(Integer e) {
        if (e < MIN_CANDIDATE_VALUE || e > MAX_CANDIDATE_VALUE) {
            throw new IllegalArgumentException(
                    "Value must be between" + MIN_CANDIDATE_VALUE + " and " + MAX_CANDIDATE_VALUE);
        }
        return super.add(e);
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Integer)) {
            throw new IllegalArgumentException("Object must be an Integer");
        }
        Integer e = (Integer) o;
        if (e < MIN_CANDIDATE_VALUE || e > MAX_CANDIDATE_VALUE) {
            throw new IllegalArgumentException(
                    "Value must be between" + MIN_CANDIDATE_VALUE + " and " + MAX_CANDIDATE_VALUE);
        }
        return super.remove(o);
    }
}
