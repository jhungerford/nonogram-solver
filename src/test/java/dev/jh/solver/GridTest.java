package dev.jh.solver;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static dev.jh.solver.Square.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GridTest {

    @Test
    public void parse() {
        Grid parsed = Grid.parse(ImmutableList.of(
                "3x2",
                "..",
                "",
                " ."
        )).build();

        Grid expected = Grid.empty(3, 2)
                .setSquare(0, 0, FILLED)
                .setSquare(0, 1, FILLED)
                .setSquare(2, 1, FILLED)
                .build();

        assertThat(parsed).isEqualTo(expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty0x0() {
        Grid.empty(0, 0);
    }

    @Test
    public void empty2x2() {
        Grid grid = Grid.empty(2, 2).build();

        assertThat(grid.width).isEqualTo(2);
        assertThat(grid.height).isEqualTo(2);
        assertThat(grid.squares).containsExactly(
                ImmutableList.of(EMPTY, EMPTY),
                ImmutableList.of(EMPTY, EMPTY)
        );
    }

    @Test
    public void empty3x4() {
        Grid grid = Grid.empty(4, 3).build();

        assertThat(grid.width).isEqualTo(3);
        assertThat(grid.height).isEqualTo(4);
        assertThat(grid.squares).containsExactly(
                ImmutableList.of(EMPTY, EMPTY, EMPTY),
                ImmutableList.of(EMPTY, EMPTY, EMPTY),
                ImmutableList.of(EMPTY, EMPTY, EMPTY),
                ImmutableList.of(EMPTY, EMPTY, EMPTY)
        );
    }

    @Test
    public void setSquare() {
        Grid grid = Grid.empty(2, 2)
                .setSquare(0, 0, FILLED)
                .setSquare(1, 1, GAP)
                .build();

        assertThat(grid.width).isEqualTo(2);
        assertThat(grid.height).isEqualTo(2);
        assertThat(grid.squares).containsExactly(
                ImmutableList.of(FILLED, EMPTY),
                ImmutableList.of(EMPTY, GAP)
        );
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setSquareOutOfBounds() {
        Grid.empty(2, 2)
                .setSquare(2, 2, FILLED);
    }

    @Test
    public void copy() {
        Grid grid = Grid.empty(3, 2).build();
        assertThat(grid.copy().build()).isEqualTo(grid);
    }

    @Test
    public void copyChangeSquares() {
        Grid original = Grid.empty(3, 2).build();
        Grid copy = original.copy()
                .setSquare(1, 1, FILLED)
                .build();

        assertThat(copy).isNotEqualTo(original);
        assertThat(copy).isEqualTo(Grid.parse(ImmutableList.of(
                "3x2",
                "  ",
                " .",
                "  "
        )).build());
    }

    @Test
    public void get() {
        Grid grid = Grid.parse(ImmutableList.of(
                "3x2",
                "  ",
                ". ",
                " ."
        )).build();

        assertThat(grid.get(0, 0)).isEqualTo(EMPTY);
        assertThat(grid.get(0, 1)).isEqualTo(EMPTY);

        assertThat(grid.get(1, 0)).isEqualTo(FILLED);
        assertThat(grid.get(1, 1)).isEqualTo(EMPTY);

        assertThat(grid.get(2, 0)).isEqualTo(EMPTY);
        assertThat(grid.get(2, 1)).isEqualTo(FILLED);
    }
}