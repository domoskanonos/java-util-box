package com.dbr.util;

public class StringUtil {


    public static String underscoreToUpperLetter(String value) {

        //value = value.toLowerCase();

        int indexOf = value.indexOf("_");
        while (indexOf > -1) {

            String prefix = value.substring(0, indexOf);
            String suffix = value.substring(indexOf);
            suffix = suffix.substring(1);
            suffix = firstLetterToUpperCase(suffix);

            value = prefix + suffix;

            indexOf = value.indexOf("_");

        }

        return value;

    }

    public static String toSetterMethodPrefix(String value) {
        return "set" + firstLetterToUpperCase(value);
    }

    public static String toGetterMethodName(String value) {
        return "get" + firstLetterToUpperCase(value) + "()";
    }

    public static String firstLetterToUpperCase(String value) {
        return value == null || value.length() == 0 ? null : String.valueOf(value.charAt(0)).toUpperCase() + value.substring(1);
    }

    private static String firstLetterToLowerCase(String value) {
        return value == null || value.length() == 0 ? null : String.valueOf(value.charAt(0)).toLowerCase() + value.substring(1);
    }


    public static String toUpperCase(String value) {
        return value.toUpperCase();
    }

    public static String getStringBetween(String all, String before, String... afters) {
        int beginIndex = all.indexOf(before);
        if (beginIndex > -1) {
            beginIndex += before.length();
        }
        int endIndex = -1;

        for (String after : afters) {
            endIndex = all.indexOf(after);
            if (endIndex > -1) {
                break;
            }
        }

        if (beginIndex == -1 || endIndex == -1) {
            beginIndex = 0;
            endIndex = 0;
        }
        return all.substring(beginIndex, endIndex).trim();
    }

    public static String toDatabaseName(String propertieName) {
        StringBuffer retval = new StringBuffer();
        int index = 0;
        for (char c : propertieName.toCharArray()) {
            String letter = String.valueOf(c);
            if (index > 0 && letter.equals(letter.toUpperCase())) {
                retval.append("_");
            }
            retval.append(letter);
            index++;
        }
        return retval.toString().toUpperCase();
    }

    public static String cleanString(String value) {
        return value.replaceAll("\n", "").replaceAll("\n", "").trim();
    }

    public static String arrayToList(String typeName) {
        return typeName.contains("[]") ? "List<" + typeName.replaceAll("\\[\\]", "") + ">" : typeName;
    }

    /**
     * convert sourceName to java propertieName,
     * f.e. MY_COLUMN -> myColumn
     *
     * @param sourceName
     * @return
     */
    public static String toPropertieName(String sourceName) {
        sourceName = sourceName.replace("-", "_");
        sourceName = sourceName.toLowerCase();
        sourceName = underscoreToUpperLetter(sourceName);
        sourceName = firstLetterToLowerCase(sourceName);
        return sourceName;
    }

}
