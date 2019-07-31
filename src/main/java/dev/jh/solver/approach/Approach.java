package dev.jh.solver.approach;

import dev.jh.solver.Line;
import dev.jh.solver.rules.Rule;

import java.util.Optional;

public interface Approach<R extends Rule> {

    /**
     * Applies the given rule to the line, returning a different line if the rule applies or empty if it doesn't.
     *
     * @param rule Rule to apply to the line
     * @param line Line to apply the rule to
     * @return New changed line, or empty.
     */
    Optional<Line> apply(R rule, Line line);
}
