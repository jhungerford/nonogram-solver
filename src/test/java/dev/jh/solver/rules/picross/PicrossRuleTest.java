package dev.jh.solver.rules.picross;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PicrossRuleTest {

    @Test(expected = IllegalArgumentException.class)
    public void emptyRuleNotAllowed() {
        new PicrossRule(ImmutableList.of());
    }

    @Test
    public void minimumLengthNoSpaces() {
        assertThat(new PicrossRule(ImmutableList.of(5)).minimumLength()).isEqualTo(5);
    }

    @Test
    public void minimumLengthWithSpaces() {
        assertThat(new PicrossRule(ImmutableList.of(1, 2, 1)).minimumLength()).isEqualTo(6);
    }
}