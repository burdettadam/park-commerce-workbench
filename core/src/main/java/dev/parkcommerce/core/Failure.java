package dev.parkcommerce.core;

/** Stable business error codes; adapters decide HTTP/GraphQL representations. */
public final class Failure extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public enum Code { INVALID_INPUT, NOT_FOUND, CONFLICT, SOLD_OUT, EXPIRED, FORBIDDEN, DEPENDENCY_UNAVAILABLE }
    private final Code code;
    public Failure(Code code, String message) { super(message); this.code = code; }
    public Code code() { return code; }
    public static Failure invalid(String message) { return new Failure(Code.INVALID_INPUT, message); }
    public static void require(boolean condition, String message) {
        if (!condition) throw invalid(message);
    }
    public static String id(String value, String label) {
        require(value != null && value.matches("[a-z0-9][a-z0-9-]{0,63}"), label + " must be a lowercase identifier (1-64 characters)");
        return value;
    }
    public static String owner(String owner) {
        require(owner != null && !owner.isBlank() && owner.length() <= 100, "Authenticated subject is required");
        return owner;
    }
}
