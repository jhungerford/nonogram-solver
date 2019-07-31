package dev.jh.solver.approach.picross;

import dev.jh.solver.Grid;
import dev.jh.solver.rules.picross.PicrossRule;
import org.junit.Test;

import static dev.jh.solver.approach.picross.PicrossApproach.OVERLAP;
import static org.assertj.core.api.Assertions.assertThat;

public class OverlapApproachTest {

    private static final Grid EMPTY_5x5 = Grid.empty(5, 5).build();
    private static final Grid EMPTY_10x10 = Grid.empty(10, 10).build();

    @Test
    public void applies() {
        assertThat(OVERLAP.apply(PicrossRule.forSegments(4), EMPTY_5x5.row(0)))
                .contains(EMPTY_5x5.row(0).parse(" ... "));

        assertThat(OVERLAP.apply(PicrossRule.forSegments(3), EMPTY_5x5.row(0)))
                .contains(EMPTY_5x5.row(0).parse("  .  "));

        assertThat(OVERLAP.apply(PicrossRule.forSegments(2, 1), EMPTY_5x5.row(0)))
                .contains(EMPTY_5x5.row(0).parse(" .   "));

        // '... . ..  '
        // '... .  .. '
        // '... .   ..'
        // '...  . .. '
        // '...  .  ..'
        // '...   . ..'
        // ' ... . .. '
        // ' ... .  ..'
        // ' ...  . ..'
        // '  ... . ..'
        // '  .       ' - always on
        assertThat(OVERLAP.apply(PicrossRule.forSegments(3, 1, 2), EMPTY_10x10.row(0)))
                .contains(EMPTY_10x10.row(0).parse("  .       "));
    }

    @Test
    public void doesNotApply() {
        // Rule only applies to lines that aren't full
        assertThat(OVERLAP.apply(PicrossRule.forSegments(5), EMPTY_5x5.row(0))).isEmpty();
        assertThat(OVERLAP.apply(PicrossRule.forSegments(3, 1), EMPTY_5x5.row(0))).isEmpty();

        // Rule only applies when there's always an overlap.  1 1 on a 5-length row doesn't always fill a square.
        assertThat(OVERLAP.apply(PicrossRule.forSegments(1, 1), EMPTY_5x5.row(0))).isEmpty();
    }
}