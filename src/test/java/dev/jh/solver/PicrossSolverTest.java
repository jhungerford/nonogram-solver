package dev.jh.solver;

import com.google.common.collect.ImmutableList;
import dev.jh.solver.rules.picross.PicrossRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.jh.solver.PuzzleType.PICROSS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PicrossSolverTest {

    private static final Pattern HEIGHT_WIDTH_PATTERN = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)");

    @Parameterized.Parameters(name = "{0}")
    public static ImmutableList<Object[]> tests() throws IOException, URISyntaxException {
        return Files.list(Path.of(PicrossSolverTest.class.getResource("/picross/puzzles/").toURI())).map(puzzle -> {
            String name = puzzle.toFile().getName();
            Path solution = puzzle.getParent().resolve("../solutions/" + name).normalize();
            if (! solution.toFile().exists()) {
                throw new IllegalStateException("No solution for " + name);
            }

            return new Object[]{name, puzzle, solution};
        }).collect(ImmutableList.toImmutableList());
    }

    private String name;
    private Path puzzle;
    private Path solution;

    public PicrossSolverTest(String name, Path puzzle, Path solution) {
        this.name = name;
        this.puzzle = puzzle;
        this.solution = solution;
    }

    @Test
    public void solve() throws IOException {
        Puzzle<PicrossRule> puzzle = loadPuzzle();
        Grid solution = loadSolution();

        PicrossSolver solver = new PicrossSolver();
        assertThat(solver.solve(puzzle)).isEqualTo(solution);
    }

    private Puzzle<PicrossRule> loadPuzzle() throws IOException {
        // Puzzle format:
        // first line is height x width
        // Remaining height + width lines contain clues, which are space separated numbers describing connected squares.
        // Row clues come before height clues.

        // TODO: skip empty rows.
        // TODO: does loadPuzzle() and loadSolution() need testing?
        // TODO: genericize puzzle loading by puzzle type.
        try (BufferedReader in = Files.newBufferedReader(puzzle)) {
            Matcher heightWidthMatcher = HEIGHT_WIDTH_PATTERN.matcher(in.readLine());
            if (! heightWidthMatcher.matches()) {
                throw new IOException("First puzzle line must be 'height x width");
            }

            int height = Integer.parseInt(heightWidthMatcher.group(1));
            int width = Integer.parseInt(heightWidthMatcher.group(2));

            Puzzle.Builder<PicrossRule> puzzle = Puzzle.newBuilder(PICROSS, height, width);

            for (int row = 0; row < height; row++) {
                puzzle.addRowRule(parseRule(in.readLine()));
            }

            for (int column = 0; column < width; column ++) {
                puzzle.addColumnRule(parseRule(in.readLine()));
            }

            return puzzle.build();
        }
    }

    private PicrossRule parseRule(String line) throws IOException {
        ImmutableList<Integer> segments = Arrays.stream(line.split("\\s+"))
                .map(Integer::parseInt)
                .collect(ImmutableList.toImmutableList());

        return new PicrossRule(ImmutableList.copyOf(segments));
    }

    private Grid loadSolution() throws IOException {
        // Solution format:
        // first line is height x width
        // Remaining lines are squares.  Short lines end with empty squares.
        return Grid.parse(ImmutableList.copyOf(Files.readAllLines(solution))).build();
    }
}