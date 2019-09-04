package com.dbr.util;

import java.io.File;

public class ResourceUtil {

    public static String getPackageNameAsPath(Class<?> clazz) {
        return getPackageNameAsPath(clazz.getPackage().getName());
    }

    public static String getPackageNameAsPath(String packageName) {
        return packageName != null && packageName.length() > 0 ?
               packageName.replace(".", File.separator)
                        + File.separator :
                "";
    }

    public static String getSourcePath(Class<?> clazz) {
        return ResourceUtil.class.getResource("/").getPath().substring(1) + "/../../src/main/java/" + getPackageNameAsPath(clazz);
    }
}
