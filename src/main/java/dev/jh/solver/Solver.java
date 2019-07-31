package dev.jh.solver;

import dev.jh.solver.rules.Rule;

public interface Solver<R extends Rule> {
    Grid solve(Puzzle<R> puzzle);
}
