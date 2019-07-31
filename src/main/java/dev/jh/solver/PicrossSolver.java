package dev.jh.solver;

import com.google.common.collect.ImmutableList;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;

import java.util.Optional;

import static dev.jh.solver.approach.picross.PicrossApproach.*;

public class PicrossSolver implements Solver<PicrossRule> {

    @Override
    public Grid solve(Puzzle<PicrossRule> puzzle) {
        Grid grid = Grid.empty(puzzle.height, puzzle.width).build();

        ImmutableList<Approach<PicrossRule>> approaches = ImmutableList.of(
                FULL_LINE,
                FULL_LINE_WITH_GAPS,
                OVERLAP
        );

        for (Approach<PicrossRule> approach : approaches) {
            for (int column = 0; column < grid.width; column ++) {
                Optional<Line> filled = approach.apply(puzzle.columnRule(column), grid.column(column));
                if (filled.isPresent()) {
                    grid = filled.get().toGrid();
                }
            }

            for (int row = 0; row < grid.height; row ++) {
                Optional<Line> filled = approach.apply(puzzle.rowRule(row), grid.row(row));
                if (filled.isPresent()) {
                    grid = filled.get().toGrid();
                }
            }
        }

        return grid;
    }
}
