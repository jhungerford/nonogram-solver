package dev.jh.solver.approach.picross;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import dev.jh.solver.Line;
import dev.jh.solver.approach.Approach;
import dev.jh.solver.rules.picross.PicrossRule;

import java.util.BitSet;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.IntStream;

import static dev.jh.solver.Square.FILLED;

/**
 * Overlap approach looks at squares that are always filled by rules that don't span the entire line.
 */
public class OverlapApproach implements Approach<PicrossRule> {

    @Override
    public Optional<Line> apply(PicrossRule rule, Line line) {
        // Only applies to partial lines.
        if (rule.minimumLength() == line.length) {
            return Optional.empty();
        }

        // Optimization: rule can't apply when it takes less than half of the squares, since no overlaps are possible.
        if (rule.minimumLength() < line.length / 2) {
            return Optional.empty();
        }

        // Algorithm: Keep a BitSet with enabled bits representing FILLED squares.
        // Run through each of the permutations of segments in the rule, ANDing the permutations together.
        // Any bit left enabled after all of the permutations have been ANDed together is always FILLED.
        BitSet alwaysOn = new BitSet(line.length);
        alwaysOn.set(0, line.length);

        // Stack for iterating over the permutations contains the left-most segments at the bottom.
        // Push segments onto the stack until all segments are on, record configuration,
        // pop as many segments as necessary to not go past the end of the line,
        // shift the right-most segment that can move to the right by one square, iterate.
        Stack<SegmentStart> stack = new Stack<>();
        pushSegments(stack, new SegmentStart(0, rule.segments.get(0), 0), rule.segments);

        while (!stack.isEmpty()) {
            // Record the previous segment configuration.
            alwaysOn.and(fromStack(stack, line.length));

            // Pop segments until one can be moved, or none of the segments can be moved.
            SegmentStart shiftingSegment = stack.pop();
            while (!stack.isEmpty() && !isRoom(shiftingSegment, rule.segments, line.length)) {
                shiftingSegment = stack.pop();
            }

            // If there's still room to move this segment (e.g. the segments aren't at the very end),
            // shift it right one square and fill in the rest of the segments as far left as possible.
            if (isRoom(shiftingSegment, rule.segments, line.length)) {
                pushSegments(stack, shiftingSegment.shiftRight(), rule.segments);
            }
        }

        // None of the squares stay on - this approach doesn't apply.
        if (alwaysOn.nextSetBit(0) == -1) {
            return Optional.empty();
        }

        // Enabled bits represent squares that are always FILLED, since they were FILLED in every permutation.
        for (int b = alwaysOn.nextSetBit(0); b != -1; b = alwaysOn.nextSetBit(b + 1)) {
            line.set(b, FILLED);
        }

        return Optional.of(line);
    }

    /**
     * Pushes segment, starting with start, onto the stack as far to the left as possible.
     *
     * @param stack Stack to push the segments on to.
     * @param start First segment to push on to the stack.
     * @param segments All segments in the rule.
     */
    private void pushSegments(Stack<SegmentStart> stack, SegmentStart start, ImmutableList<Integer> segments) {
        stack.push(start);

        int position = start.start + 1;
        for (int segment = start.segment + 1; segment < segments.size(); segment ++) {
            int segmentLength = segments.get(segment);
            stack.push(new SegmentStart(segment, segmentLength, position));
            position += segmentLength + 1; // One space between each segment so they're as far left as possible.
        }
    }

    /**
     * Converts the given stack of segment starts into a BitSet where enabled bits represent FILLED squares.
     *
     * @param stack  Stack of segment starts.
     * @param lineLength Total length of the line.
     * @return BitSet representing the FILLED squares in the stack of segments.
     */
    private BitSet fromStack(Stack<SegmentStart> stack, int lineLength) {
        BitSet bitSet = new BitSet(lineLength);

        for (SegmentStart segmentStart : stack) {
            bitSet.set(segmentStart.start, segmentStart.start + segmentStart.length);
        }

        return bitSet;
    }

    /**
     * Returns whether there is room for the given segment and the segments to the right of it to fit into the line
     * with one square between each of the segments.
     *
     * @param segmentStart Segment start
     * @param segments     All of the segments in the rule.
     * @param lineLength   Length of the line.
     * @return Whether there's room to shift the segment right one square.
     */
    private boolean isRoom(SegmentStart segmentStart, ImmutableList<Integer> segments, int lineLength) {
        // Figure out how much space this segment and the ones to the right of it will take up, including gaps.
        int remainingLength = segmentStart.start + IntStream.range(segmentStart.segment, segments.size())
                .map(segment -> segments.get(segment) + 1) // Plus one square for each gap.
                .sum() - 1; // Minus one since the last segment has no gap.

        return remainingLength < lineLength;
    }

    /**
     * SegmentStart contains the index of a segment, the length of a segment, and the index of the first square
     * that the segment occupies on a line.
     */
    private static class SegmentStart {
        // Index of the segment in the rule.
        public final int segment;
        // Segment length.
        public final int length;
        // Index of the first square that's on and covered by this segment.
        public final int start;

        public SegmentStart(int segment, int length, int start) {
            this.segment = segment;
            this.length = length;
            this.start = start;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("segment", segment)
                    .add("length", length)
                    .add("start", start)
                    .toString();
        }

        /**
         * Returns a copy of this segment shifted to the right by one square.
         * @return Copy of this segment shifted to the right.
         */
        public SegmentStart shiftRight() {
            return new SegmentStart(segment, length, start + 1);
        }
    }
}
