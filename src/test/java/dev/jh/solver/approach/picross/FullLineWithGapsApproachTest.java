package dev.jh.solver.approach.picross;

import com.google.common.collect.ImmutableList;
import dev.jh.solver.Grid;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FullLineWithGapsApproachTest {

    private static final Approach<PicrossRule> APPROACH = new FullLineWithGapsApproach();
    private static final Grid EMPTY_3x2 = Grid.empty(3, 2).build();
    private static final Grid EMPTY_5x5 = Grid.empty(5, 5).build();

    @Test
    public void applies() {
        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(1, 1, 1)), EMPTY_5x5.row(0)))
                .describedAs("rule: '1 1 1' on a 5-wide row")
                .contains(EMPTY_5x5.row(0).parse(". . ."));

        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(3, 1)), EMPTY_5x5.row(0)))
                .describedAs("rule: '3 1' on a 5-wide row")
                .contains(EMPTY_5x5.row(0).parse("... ."));
    }

    @Test
    public void doesNotApply() {
        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(1)), EMPTY_3x2.row(0)))
                .describedAs("rule: '1' on a 2-wide row - unclear which square is filled.")
                .isEmpty();

        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(2, 1)), EMPTY_5x5.row(0)))
                .describedAs("rule: '2 1' on 5-wide row - rule has width 4")
                .isEmpty();

        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(5)), EMPTY_5x5.row(0)))
                .describedAs("rule: '5' on 5-wide row - covered by FullLineApproach.")
                .isEmpty();
    }
}