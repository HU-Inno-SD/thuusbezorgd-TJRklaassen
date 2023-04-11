package nl.hu.inno.thuusbezorgd.domain;

public enum ReviewRating {
    Horrible,
    Poor,
    Average,
    Good,
    Excellent;

    public static ReviewRating fromInt(int rating) {
        ReviewRating parsed = switch (rating) {
            case 1 -> Horrible;
            case 2 -> Poor;
            case 3 -> Average;
            case 4 -> Good;
            case 5 -> Excellent;
            default -> null;
        };

        if (parsed == null) {
            throw new IllegalArgumentException("Unknown rating: " + rating);
        } else {
            return parsed;
        }
    }

    public int toInt() {
        return switch (this) {
            case Horrible -> 1;
            case Poor -> 2;
            case Average -> 3;
            case Good -> 4;
            case Excellent -> 5;
        };
    }
}
