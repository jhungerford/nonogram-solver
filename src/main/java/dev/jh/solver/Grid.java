package dev.jh.solver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Grid is an immutable rectangle of squares.
 */
public class Grid {
    private static final Pattern HEIGHT_WIDTH_PATTERN = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)");

    public final int height;
    public final int width;
    public final ImmutableList<ImmutableList<Square>> squares;

    private Grid(Builder builder) {
        this.height = builder.height;
        this.width = builder.width;
        this.squares = Arrays.stream(builder.squares)
                .map(ImmutableList::copyOf)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Returns the given row as a line.
     *
     * @param row Row index.
     * @return Line containing the row squares.
     */
    public Line row(int row) {
        return Line.row(this, Preconditions.checkPositionIndex(row, height, "Row"));
    }

    /**
     * Returns the given column as a line.
     *
     * @param column Column index.
     * @return Line containing the column squares.
     */
    public Line column(int column) {
        return Line.column(this, Preconditions.checkPositionIndex(column, width, "Column"));
    }

    /**
     * Returns the square at the given row and column.
     *
     * @param row Row index
     * @param column Column index
     * @return Square at the row and column
     */
    public Square get(int row, int column) {
        Preconditions.checkPositionIndex(row, height, "Row");
        Preconditions.checkPositionIndex(column, width, "Column");

        return squares.get(row).get(column);
    }

    /**
     * Returns a Builder containing a copy of this grid that can be used to change squares.
     *
     * @return Copy of this grid.
     */
    public Builder copy() {
        return new Builder(height, width).setSquares(squares);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return height == grid.height &&
                width == grid.width &&
                Objects.equals(squares, grid.squares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width, squares);
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();

        for (ImmutableList<Square> row : squares) {
            for (Square column : row) {
                bldr.append(column.name);
            }
            bldr.append('\n');
        }

        return bldr.toString();
    }

    public static Builder parse(ImmutableList<String> lines) {
        Iterator<String> lineIterator = lines.iterator();
        Matcher heightWidthMatcher = HEIGHT_WIDTH_PATTERN.matcher(lineIterator.next());
        if (!heightWidthMatcher.matches()) {
            throw new IllegalArgumentException("First line must be 'height x width");
        }

        int height = Integer.parseInt(heightWidthMatcher.group(1));
        int width = Integer.parseInt(heightWidthMatcher.group(2));

        Grid.Builder grid = Grid.empty(height, width);

        int row = 0;
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            for (int column = 0; column < line.length(); column ++) {
                grid.setSquare(row, column, Square.named(line.charAt(column)));
            }
            row ++;
        }

        return grid;
    }

    public static Builder empty(int height, int width) {
        return new Builder(height, width);
    }

    public static class Builder {
        private final int height;
        private final int width;
        private Square[][] squares;

        private Builder(int height, int width) {
            Preconditions.checkArgument(height > 0, "height must be >= 1");
            Preconditions.checkArgument(width > 0, "width must be >= 1");
            this.height = height;
            this.width = width;
            this.squares = new Square[height][width];

            for (int row = 0; row < height; row ++) {
                Arrays.fill(squares[row], Square.EMPTY);
            }
        }

        public Builder setSquare(int row, int column, Square value) {
            Preconditions.checkPositionIndex(row, height, "row");
            Preconditions.checkPositionIndex(column, width, "column");
            Preconditions.checkNotNull(value, "value must be non-null.");

            squares[row][column] = value;
            return this;
        }

        public Builder setSquares(ImmutableList<ImmutableList<Square>> squares) {
            Preconditions.checkArgument(squares.size() == height, "Height must match");

            for (int row = 0; row < squares.size(); row ++) {
                Preconditions.checkArgument(squares.get(row).size() == width, "Width must match");

                for (int column = 0; column < squares.get(row).size(); column ++) {
                    this.squares[row][column] = squares.get(row).get(column);
                }
            }

            return this;
        }

        public Grid build() {
            return new Grid(this);
        }
    }
}
