package dev.jh.solver.approach.picross;

import dev.jh.solver.Line;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;

import java.util.Optional;

public enum PicrossApproach implements Approach<PicrossRule> {

    FULL_LINE(new FullLineApproach()),
    FULL_LINE_WITH_GAPS(new FullLineWithGapsApproach()),
    OVERLAP(new OverlapApproach());

    private final Approach<PicrossRule> approach;

    PicrossApproach(Approach<PicrossRule> approach) {
        this.approach = approach;
    }

    @Override
    public Optional<Line> apply(PicrossRule rule, Line line) {
        return approach.apply(rule, line);
    }
}
