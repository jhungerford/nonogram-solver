package dev.jh.solver.rules.picross;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import dev.jh.solver.rules.Rule;

import java.util.Arrays;
import java.util.Objects;

public class PicrossRule implements Rule {

    public final ImmutableList<Integer> segments;

    public PicrossRule(ImmutableList<Integer> segments) {
        Preconditions.checkArgument(!segments.isEmpty(), "Rule needs at least one segment.");
        this.segments = segments;
    }

    /**
     * Returns the minimum length of this rule counting the segments and empty squares between them.
     * @return Minimum rule length.
     */
    public int minimumLength() {
        return segments.stream()
                .mapToInt(Integer::intValue)
                .sum() + segments.size() - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PicrossRule that = (PicrossRule) o;
        return Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }

    @Override
    public String toString() {
        return Joiner.on(' ').join(segments);
    }

    public static PicrossRule forSegments(int... segments) {
        return new PicrossRule(Arrays.stream(segments)
                .boxed()
                .collect(ImmutableList.toImmutableList()));
    }
}
