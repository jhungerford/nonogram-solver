package dev.jh.solver;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.jh.solver.LineType.COLUMN;
import static dev.jh.solver.LineType.ROW;

/**
 * Line represents a row or column in a puzzle.
 *
 * TODO: some puzzles, like mega picross, contain clues that span more than one line.
 */
public class Line {
    public final int length;

    private final LineType type;
    private final int index;
    private final Grid grid;
    private final Map<RowColumn, Square> changes;


    private Line(LineType type, Grid grid, int index, int length) {
        this.length = length;

        this.type = type;
        this.index = index;
        this.grid = grid;
        this.changes = new HashMap<>();
    }

    /**
     * Returns the square at the given index in this line.
     *
     * @param index Index of the square to fetch.
     * @return Square at the given index.
     */
    public Square get(int index) {
        Preconditions.checkPositionIndex(index, length, "Index");
        RowColumn rowColumn = RowColumn.forType(type, this.index, index);

        return changes.getOrDefault(rowColumn, grid.get(rowColumn.row, rowColumn.column));
    }

    /**
     * Sets the square at the given index to the new square value.
     *
     * @param index Index of the square to set in this line.
     * @param square New square to set.
     * @return This line.
     */
    public Line set(int index, Square square) {
        if (get(index) != square) {
            changes.put(RowColumn.forType(type, this.index, index), square);
        }

        return this;
    }

    /**
     * Returns a new line with squares between start and end filled.
     *
     * @param start Index to start filling, inclusive.
     * @param end   Index to end filling, exclusive.
     * @param square Type of square to fill the line segment with.
     * @return This line.
     */
    public Line fill(int start, int end, Square square) {
        Preconditions.checkPositionIndexes(start, end, length);

        for (int i = start; i < end; i ++) {
            set(i, square);
        }

        return this;
    }

    /**
     * Parses the given string into squares in this line.
     *
     * @param str String to parse
     * @return This line with the parsed squares.
     */
    public Line parse(String str) {
        Preconditions.checkArgument(str.length() <= length, "String length must be <= " + length);

        for (int i = 0; i < str.length(); i ++) {
            set(i, Square.named(str.charAt(i)));
        }

        return this;
    }

    /**
     * Returns a new Grid containing the squares that were modified by this line.
     *
     * @return Changed grid.
     */
    public Grid toGrid() {
        if (changes.isEmpty()) {
            return grid;
        }

        Grid.Builder changedGrid = grid.copy();
        for (Map.Entry<RowColumn, Square> change : changes.entrySet()) {
            changedGrid.setSquare(change.getKey().row, change.getKey().column, change.getValue());
        }

        return changedGrid.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return length == line.length &&
                index == line.index &&
                type == line.type &&
                Objects.equals(lineToString(), line.lineToString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, type, index, lineToString());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("index", index)
                .add("line", lineToString())
                .toString();
    }

    private String lineToString() {
        return IntStream.range(0, length)
                .mapToObj(i -> Character.toString(get(i).name))
                .collect(Collectors.joining());
    }

    public static Line row(Grid grid, int index) {
        return new Line(ROW, grid, index, grid.width);
    }

    public static Line column(Grid grid, int index) {
        return new Line(COLUMN, grid, index, grid.height);
    }

    /**
     * Returns a copy of this line.  Since Lines are mutable, this method is useful for forking lines.
     * @return
     */
    public Line copy() {
        Line copy = new Line(type, grid, index, length);

        for (Map.Entry<RowColumn, Square> entry : changes.entrySet()) {
            int index = type == ROW ? entry.getKey().column : entry.getKey().row;
            copy.set(index, entry.getValue());
        }

        return copy;
    }

    /**
     * RowColumn contains a row and column, and a way to compute row and column based on LineType.
     */
    private static class RowColumn {
        public final int row;
        public final int column;

        private RowColumn(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            RowColumn rowColumn = (RowColumn) other;
            return row == rowColumn.row && column == rowColumn.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("row", row)
                    .add("column", column)
                    .toString();
        }

        public static RowColumn forType(LineType type, int lineIndex, int index) {
            if (type == ROW) {
                return new RowColumn(lineIndex, index);
            } else {
                return new RowColumn(index, lineIndex);
            }
        }
    }
}
