package com.dbr.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

    public static File getResource(String path) {
        return new File(ResourceUtil.class.getResource(path).getFile());
    }

    public static InputStream getResourceAsStream(String path) {
        InputStream is = null;
        try {
            is = ResourceUtil.class.getResource(path).openStream();
        } catch (IOException ioe) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe2) {
                    throw new ResourceException(String.format("can't close input stream, path=%s", path), ioe2);
                }
            }
            throw new ResourceException(String.format("can't open stream, path=%s", path), ioe);
        }
        return is;
    }

}
