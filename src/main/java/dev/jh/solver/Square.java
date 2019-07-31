package dev.jh.solver;

public enum Square {
    /** Squares that haven't been touched yet. */
    EMPTY(' '),
    /** Squares that are definitely filled in. */
    FILLED('.'),
    /** Squares that are definitely not filled. */
    GAP('x');

    public final char name;

    Square(char name) {
        this.name = name;
    }

    public static Square named(char name) {
        for (Square square : values()) {
            if (square.name == name) {
                return square;
            }
        }

        throw new IllegalArgumentException("No square named " + name);
    }

    @Override
    public String toString() {
        return Character.toString(name);
    }
}
