package com.dbr.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    protected static final Logger logger = LogManager.getLogger(ZipUtil.class);

    private static void addFileEntry(ZipOutputStream zos, File dir, File file) throws IOException {
        logger.info("addFileEntry: {} ", file.getAbsolutePath());

        ZipEntry ze = new ZipEntry(dir.getName() + File.separator + file.getAbsolutePath()
                .substring(dir.getAbsolutePath().length() + 1));
        zos.putNextEntry(ze);
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        zos.closeEntry();
        fis.close();
    }

    private static void addDirectory(ZipOutputStream zos, File dir) throws IOException {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            if (file.getAbsolutePath().contains(".lock")) {
                continue;
            }
            addFileEntry(zos, dir, file);
        }
    }

    public static boolean createZipFile(File zipFile, File folderToZip, boolean removeOldZip) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            if (zipFile.exists()) {
                if (removeOldZip) {
                    if (zipFile.delete()) {
                        throw new RuntimeException("error deleting old zip file: " + zipFile.getAbsolutePath());
                    }
                } else {
                    return false;
                }
            }

            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            addDirectory(zos, folderToZip);
        } catch (IOException e) {
            logger.error("error create zip file:", e);
            throw new RuntimeException(e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    public static void unzipFile(File zipFile, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
