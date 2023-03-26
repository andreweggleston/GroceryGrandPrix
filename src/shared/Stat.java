package shared;

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