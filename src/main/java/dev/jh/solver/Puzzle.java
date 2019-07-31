package dev.jh.solver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import dev.jh.solver.rules.Rule;

import java.util.ArrayList;
import java.util.List;

public class Puzzle<R extends Rule> {
    public final PuzzleType type;
    public final int height;
    public final int width;

    private final ImmutableList<R> rowRules;
    private final ImmutableList<R> columnRules;

    private Puzzle(Builder<R> builder) {
        this.type = builder.type;
        this.height = builder.height;
        this.width = builder.width;
        this.rowRules = ImmutableList.copyOf(builder.rowRules);
        this.columnRules = ImmutableList.copyOf(builder.columnRules);

        Preconditions.checkState(rowRules.size() == height, "Must have a rule for each row.");
        Preconditions.checkState(columnRules.size() == width, "Must have a rule for each column.");
    }

    public R rowRule(int row) {
        return rowRules.get(Preconditions.checkPositionIndex(row, rowRules.size(), "Row"));
    }

    public R columnRule(int column) {
        return columnRules.get(Preconditions.checkPositionIndex(column, columnRules.size(), "Column"));
    }

    public static <R extends Rule> Builder<R> newBuilder(PuzzleType type, int height, int width) {
        return new Builder<>(type, height, width);
    }

    public static class Builder<R extends Rule> {
        private PuzzleType type;
        private int height;
        private int width;
        private List<R> rowRules;
        private List<R> columnRules;

        private Builder(PuzzleType type, int height, int width) {
            this.type = type;
            this.height = height;
            this.width = width;
            this.rowRules = new ArrayList<>(height);
            this.columnRules = new ArrayList<>(width);
        }

        public Builder addRowRule(R rule) {
            this.rowRules.add(rule);
            return this;
        }

        public Builder addColumnRule(R rule) {
            this.columnRules.add(rule);
            return this;
        }

        public Puzzle<R> build() {
            return new Puzzle<>(this);
        }
    }

}
