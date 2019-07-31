package dev.jh.solver.approach.picross;

import com.google.common.collect.ImmutableList;
import dev.jh.solver.Grid;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FullLineApproachTest {

    private static final Approach<PicrossRule> APPROACH = new FullLineApproach();
    private static final Grid EMPTY_3x2 = Grid.empty(3, 2).build();

    @Test
    public void applies() {
        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(3)), EMPTY_3x2.column(0)))
                .contains(EMPTY_3x2.column(0).parse("..."));

        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(2)), EMPTY_3x2.row(1)))
                .contains(EMPTY_3x2.row(1).parse(".."));
    }

    @Test
    public void doesNotApply() {
        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(1, 1)), EMPTY_3x2.column(0))).isEmpty();
        assertThat(APPROACH.apply(new PicrossRule(ImmutableList.of(1)), EMPTY_3x2.row(0))).isEmpty();
    }
}