package dev.jh.solver.approach.picross;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dev.jh.solver.Grid;
import dev.jh.solver.Line;
import dev.jh.solver.rules.picross.PicrossRule;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static dev.jh.solver.Square.FILLED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that contains assertions about each of the approaches.  Applies every assertion to every permutation of lines
 * and rules of length 5.  Total number of checks is 2^5 (for the board) * 13 (for the rules) * approaches,
 * so enumerating a length 5 line is tractable.  This test contains general assertions about each of the approaches -
 * each approach has a separate test that checks correctness.
 */
public class PicrossApproachTest {

    private ImmutableMap<LineRule, ImmutableList<PicrossApproach>> LINE_RULE_APPROACHES;

    @Before
    public void setUp() {

        // Permutations of a 5 square line (EMPTY and FILLED) for now.
        // Use the binary representation of 0-31 to generate the permutations.
        Grid empty = Grid.empty(1, 5).build();
        ImmutableList<Line> allLines = IntStream.range(0, (int) Math.pow(2, 5))
                .mapToObj(i -> {
                    Line line = empty.row(0);

                    for (int bit = 0; bit < 5; bit++) {
                        if ((i & 1 << bit) != 0) {
                            line.set(bit, FILLED);
                        }
                    }

                    return line;
                })
                .collect(ImmutableList.toImmutableList());

        // Rules that can apply to the line.
        ImmutableList<PicrossRule> allRules = ImmutableList.of(
                PicrossRule.forSegments(5),
                PicrossRule.forSegments(4),
                PicrossRule.forSegments(3),
                PicrossRule.forSegments(3, 2),
                PicrossRule.forSegments(3, 1),
                PicrossRule.forSegments(2),
                PicrossRule.forSegments(2, 2),
                PicrossRule.forSegments(2, 1),
                PicrossRule.forSegments(1),
                PicrossRule.forSegments(1, 3),
                PicrossRule.forSegments(1, 2),
                PicrossRule.forSegments(1, 1),
                PicrossRule.forSegments(1, 1, 1)
        );

        // Determine which approaches can apply the rule to the line.
        ImmutableMap.Builder<LineRule, ImmutableList<PicrossApproach>> approaches = ImmutableMap.builder();

        for (Line line : allLines) {
            for (PicrossRule rule : allRules) {
                ImmutableList<PicrossApproach> approachesThatApply = Arrays.stream(PicrossApproach.values())
                        .filter(approach -> approach.apply(rule, line.copy()).isPresent())
                        .collect(ImmutableList.toImmutableList());

                if (! approachesThatApply.isEmpty()) {
                    approaches.put(new LineRule(line, rule), approachesThatApply);
                }
            }
        }

        LINE_RULE_APPROACHES = approaches.build();
    }

    @Test
    public void noOverlap() {
        for (Map.Entry<LineRule, ImmutableList<PicrossApproach>> entry : LINE_RULE_APPROACHES.entrySet()) {
            String description = String.format("%s solve %s - only one approach should solve the line and rule.",
                    Joiner.on(' ').join(entry.getValue()),
                    entry.getKey());

            assertThat(entry.getValue())
                    .describedAs(description)
                    .hasSizeLessThanOrEqualTo(1);
        }
    }

    @Test
    public void ruleMakesProgress() {
        ImmutableSet<PicrossApproach> approachesWithProgress = LINE_RULE_APPROACHES.values().stream()
                .flatMap(ImmutableList::stream)
                .collect(ImmutableSet.toImmutableSet());

        assertThat(approachesWithProgress)
                .describedAs("All approaches should make some progress one at least one line and rule.")
                .containsAll(ImmutableSet.copyOf(PicrossApproach.values()));
    }

    private static class LineRule {
        public final Line line;
        public final PicrossRule rule;

        public LineRule(Line line, PicrossRule rule) {
            this.line = line;
            this.rule = rule;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LineRule lineRule = (LineRule) o;
            return Objects.equals(line, lineRule.line) &&
                    Objects.equals(rule, lineRule.rule);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, rule);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("line", line)
                    .add("rule", rule)
                    .toString();
        }
    }
}