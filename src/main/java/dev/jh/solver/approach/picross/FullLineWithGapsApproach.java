package dev.jh.solver.approach.picross;

import dev.jh.solver.Line;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;

import java.util.Optional;

import static dev.jh.solver.Square.FILLED;

/**
 * Approach that handles rules that span a full line, but have gaps in the middle.
 * Example: 2 1 fills a line with length 4, since the gap takes one square.
 */
public class FullLineWithGapsApproach implements Approach<PicrossRule> {

    @Override
    public Optional<Line> apply(PicrossRule rule, Line line) {
        // FullLineApproach handles single full-line segment.
        if (rule.segments.size() == 1) {
            return Optional.empty();
        }

        // Gaps take one square - check if the segments and gaps fill the whole line.
        if (rule.minimumLength() != line.length) {
            return Optional.empty();
        }

        // Fill in the segments with gaps.
        int start = 0;
        for (int segment : rule.segments) {
            line.fill(start, start + segment, FILLED);
            start += segment + 1;
        }

        return Optional.of(line);
    }
}
