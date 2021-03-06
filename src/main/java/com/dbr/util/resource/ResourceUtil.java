package com.dbr.util.resource;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceUtil {

    private static Logger _log = Logger.getLogger(ResourceUtil.class.getName());

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

    public static String loadResourceAsString(Path path, Charset charset) throws IOException {
        _log.log(Level.FINER, "load resource as string, path: {0}", path);
        String content;
        try (Stream<String> lines = Files.lines(path, charset)) {
            content = lines.collect(Collectors.joining("\n"));
        }
        _log.log(Level.FINEST, "content: {0}", content);
        return content;
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


    public static String asDataUri(File file) throws IOException {
        String retVal = "data:";
        String filename = file.getName();
        String fileEnding = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        switch (fileEnding.toUpperCase()) {
            case "JPG":
            case "PNG":
                retVal += "image/";
                break;
            default:
                retVal += "application/";
                break;
        }
        retVal += fileEnding + ";base64,";
        try {
            retVal += Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }

    public static byte[] dataUriToByteArray(String dataUri) {
        if (dataUri.contains("base64")) {
            String base64 = dataUri.substring(dataUri.indexOf("base64") + 7, dataUri.length());
            return Base64.getDecoder().decode(base64);
        } else {
            String data = dataUri.substring(dataUri.indexOf(",") + 1, dataUri.length());
            return data.getBytes();
        }
    }



}
