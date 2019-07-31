package dev.jh.solver;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static dev.jh.solver.Square.EMPTY;
import static dev.jh.solver.Square.FILLED;
import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    private static final Grid EMPTY_3x2 = Grid.empty(3, 2).build();
    private static final Grid GRID_3x2 = Grid.empty(3, 2)
            .setSquare(2, 1, FILLED)
            .build();

    @Test
    public void gridRow() {
        assertThat(GRID_3x2.row(0)).isEqualTo(EMPTY_3x2.row(0).parse(""));
        assertThat(GRID_3x2.row(1)).isEqualTo(EMPTY_3x2.row(1).parse("  "));
        assertThat(GRID_3x2.row(2)).isEqualTo(EMPTY_3x2.row(2).parse(" ."));
    }

    @Test
    public void gridColumn() {
        assertThat(GRID_3x2.column(0)).isEqualTo(EMPTY_3x2.column(0).parse("   "));
        assertThat(GRID_3x2.column(1)).isEqualTo(EMPTY_3x2.column(1).parse("  ."));
    }

    @Test
    public void fillRow() {
        Grid filled = EMPTY_3x2.row(0).fill(0, 2, FILLED).toGrid();
        assertThat(filled).isEqualTo(Grid.parse(ImmutableList.of(
                "3x2",
                "..",
                "  ",
                "  "
        )).build());

        assertThat(EMPTY_3x2)
                .describedAs("Underlying array should not change")
                .isEqualTo(Grid.empty(3, 2).build());
    }

    @Test
    public void fillColumn() {
        Grid filled = EMPTY_3x2.column(1).fill(1, 3, FILLED).toGrid();
        assertThat(filled).isEqualTo(Grid.parse(ImmutableList.of(
                "3x2",
                "  ",
                " .",
                " ."
        )).build());

        assertThat(EMPTY_3x2)
                .describedAs("Underlying grid should not change")
                .isEqualTo(Grid.empty(3, 2).build());
    }

    @Test
    public void emptyFill() {
        Line filled = EMPTY_3x2.row(0).fill(0, 0, FILLED);
        assertThat(filled)
                .describedAs("Fill with length 0 shouldn't change the line.")
                .isEqualTo(EMPTY_3x2.row(0));
    }

    @Test
    public void setNoChange() {
        Line line = EMPTY_3x2.row(0)
                .set(0, EMPTY);

        assertThat(line.get(0)).isEqualTo(EMPTY);
        assertThat(line.toGrid()).isEqualTo(EMPTY_3x2);
    }

    @Test
    public void setChangeOnce() {
        Line line = EMPTY_3x2.row(1)
                .set(0, FILLED);

        assertThat(line.get(0)).isEqualTo(FILLED);
        assertThat(line.toGrid()).isEqualTo(Grid.empty(3, 2)
                .setSquare(1, 0, FILLED)
                .build());
    }

    @Test
    public void setChangeBack() {
        Line line = EMPTY_3x2.row(0)
                .set(0, FILLED)
                .set(0, EMPTY);

        assertThat(line.get(0)).isEqualTo(EMPTY);
        assertThat(line.toGrid()).isEqualTo(EMPTY_3x2);
    }
}