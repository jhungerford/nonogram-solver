package dev.jh.solver.approach.picross;

import dev.jh.solver.Line;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;

import java.util.Optional;

import static dev.jh.solver.Square.FILLED;

/**
 * Approach that fills in all of the squares in a row or column if the rule size matches the row / column size.
 * Example: 5 fills a line with length 5.  Pretty self explanatory.
 */
public class FullLineApproach implements Approach<PicrossRule> {

    @Override
    public Optional<Line> apply(PicrossRule rule, Line line) {
        if (rule.segments.size() != 1 || rule.segments.get(0) != line.length) {
            return Optional.empty();
        }

        return Optional.of(line.fill(0, line.length, FILLED));
    }
}
