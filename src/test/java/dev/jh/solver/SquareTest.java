package dev.jh.solver;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SquareTest {

    @Test
    public void named() {
        for (Square square : Square.values()) {
            assertThat(Square.named(square.name)).isEqualTo(square);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNamed() {
        Square.named('7');
    }
}