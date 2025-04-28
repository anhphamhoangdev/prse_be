package com.hcmute.prse_be.constants;

public class TicketType {
    public static final String COURSE = "course";
    public static final String INSTRUCTOR = "instructor";
    public static final String PLATFORM = "platform";
    public static final String PAYMENT = "payment";
    public static final String ACCOUNT = "account";
    public static final String OTHER = "other";

    private TicketType() {
        // Prevent instantiation
        // This class is not meant to be instantiated
        // and should only be used for its constants.
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
