package com.hcmute.prse_be.constants;

public class UserType {
    public static final String STUDENT = "STUDENT";
    public static final String INSTRUCTOR = "INSTRUCTOR";
    public static final String ADMIN = "ADMIN";
    public static final String SYSTEM = "SYSTEM";

    private UserType() {
        // Prevent instantiation
        // This class is not meant to be instantiated
        // and should only be used for its constants.
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
