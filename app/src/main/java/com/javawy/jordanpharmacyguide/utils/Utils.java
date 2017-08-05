package com.javawy.jordanpharmacyguide.utils;

/**
 * Created by Rami Nassar on 7/4/2017.
 */

public class Utils {

    /**
     * Is Blank Or Null
     *
     * @param value : String Value
     * @return True if String Value Is Blank Or Null
     */
    public static boolean isBlankOrNull(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * Is Double Value
     *
     * @param value : Double Value
     * @return True if String Value Is Double
     */
    public static boolean isDouble(String value) {
        try {
            if(isBlankOrNull(value)) {
                return true;
            }

            return Double.valueOf(value) != null;

        } catch (Exception e) {
            return false;
        }

    }
}