package dev.parkcommerce.core;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/** Transport-neutral parsing shared by the REST and GraphQL quote adapters. */
public final class QuoteContract {
    private QuoteContract() {}

    /** Boxed quantities distinguish missing fields from an explicitly requested zero. */
    public record Input(String productId, String visitDate, Integer adults, Integer children) {
        public Pricing.Request toRequest() {
            Failure.require(adults != null && children != null, "Both adult and child quantities are required");
            return new Pricing.Request(productId, parseDate(visitDate), adults, children);
        }
    }

    public static LocalDate parseDate(String value) {
        Failure.require(value != null && value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}") && !value.startsWith("0000"),
            "Visit date must be a valid YYYY-MM-DD date (year 0001-9999)");
        try { return LocalDate.parse(value); }
        catch (DateTimeParseException error) {
            throw Failure.invalid("Visit date must be a valid YYYY-MM-DD date (year 0001-9999)");
        }
    }

    public static UUID parseId(String value) {
        // UUID.fromString also accepts abbreviated groups; do not normalize ambiguous wire IDs.
        Failure.require(value != null && value.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"),
            "Quote ID must be a full hyphenated UUID");
        return UUID.fromString(value);
    }
}
