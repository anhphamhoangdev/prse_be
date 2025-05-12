package com.hcmute.prse_be.constants;

public class TicketStatusType {
    public static final String NEW = "NEW";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String RESOLVED = "RESOLVED";

    private TicketStatusType() {
        // Prevent instantiation
        // This class is not meant to be instantiated
        // and should only be used for its constants.
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
