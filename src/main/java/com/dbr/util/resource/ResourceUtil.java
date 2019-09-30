package com.dbr.util.resource;

import java.io.*;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

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

    public static String getResourceAsString(String path) {
        StringBuffer retval = new StringBuffer();

        InputStream is = getResourceAsStream(path);
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                retval.append(line);
                retval.append("\n");
            }
        } catch (IOException e) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    throw new ResourceException("unable to close input stream.", e);
                }
            }
            throw new ResourceException("error read resource.", e);
        }

        return retval.toString();

    }

    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }


}
