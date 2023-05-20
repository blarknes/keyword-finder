package com.keywordfinder.utilities;

/**
 * The Utilities class provides various utility methods for common tasks. These
 * methods can be used across different projects to simplify and streamline
 * common operations.
 */
public class Utilities {

    /**
     * The Utilities class contains only static methods, do not instantiate it.
     */
    private Utilities() {
        throw new IllegalStateException("Utility class. Don't instantiate.");
    }

    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int RANDOM_ID_SIZE = 8;

    /**
     * Generator of random alphanumeric Strings with 8 characters which will be used
     * as unique Ids to identify the user keyword searches.
     * 
     * @return Unique random Id.
     */
    public static String generateRandomSearchId() {
        String searchId = "";
        for (int i = 0; i < RANDOM_ID_SIZE; i++) {
            searchId += ALPHANUMERIC.charAt((int) (Math.random() * ALPHANUMERIC.length()));
        }
        return searchId;
    }
}
