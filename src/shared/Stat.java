package shared;

/**
 * Stat enumerates the integer values [1,10]. We use an enumerator because we want the rigorous type
 * definition provided by an enum. This guarantees that a Stat won't exist with values outside the range
 * provided by the enum.
 * We use an enum--instead of a class, even if we want methods--because a class with an integer field would not
 * guarantee the int was between 1 and 10.  With an enum those integer values are set once when the program is
 * compiled and then cannot be touched during runtime.
 * I am adamant that my usage of an enum fits within Java Standards. Java is a strongly typed language, and it makes
 * sense to have a strict type to back our stats
 */
public enum Stat {
    ONE(1) {
        @Override
        public Stat decrement() {
            return ONE;
        }
    }, TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10) {
        @Override
        public Stat increment() {
            return TEN;
        }
    };

    private final int statNumeral;

    Stat(int i) {
        statNumeral = i;
    }

    public static Stat fromInt(int i) {
        return Stat.values()[i - 1];
    }

    public int getStatNumeral() {
        return statNumeral;
    }

    public Stat increment() {
        return values()[ordinal() + 1];
    }

    public Stat decrement() {
        return values()[ordinal() - 1];
    }
}