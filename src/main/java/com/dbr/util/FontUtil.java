package com.dbr.util;

import org.apache.commons.lang.StringEscapeUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FontUtil {

    private static Logger logger = Logger.getLogger(FontUtil.class.getName());

    public static void main(String[] args) {
        Font font = FontUtil.getGoogleFontByName("Roboto");
        System.out.println(font.getName());
    }

    public static Font getGoogleFontByName(String fontName) {
        return getGoogleFontByName(fontName, 1);
    }

    public static Font getGoogleFontByName(String fontName, int size) {
        if (!isRegistered(fontName)) {
            try {
                registerGoogleFontByName(fontName);
            } catch (Exception e) {
                throw new RuntimeException("error register font: " + fontName, e);
            }
        }
        return getFont(fontName, size);
    }

    public static Font getFont(Font font, int size) {
        return new Font(font.getName(), font.getStyle(), size);
    }

    public static Font getFont(String fontName, int size) {
        return new Font(fontName, Font.PLAIN, size);
    }

    public static Font registerFontFromFile(File fontFile) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!ge.registerFont(font)) {
            System.out.println("Fehler register font: " + fontFile.getAbsolutePath());
            throw new RuntimeException("error register font: " + fontFile.getAbsolutePath());
        }
        return font;
    }

    private static Font registerGoogleFontByName(String name) throws IOException, FontFormatException {
        String urlAsString = "https://fonts.google.com/download?family=" + StringEscapeUtils.escapeHtml(name);
        logger.info("register font from url: " + urlAsString);
        return registerFontFromZipFile(new URL(urlAsString));
    }

    public static Font registerFontFromZipFile(URL url) throws IOException, FontFormatException {
        ZipInputStream stream = new ZipInputStream(url.openStream());
        ZipEntry entry;
        while ((entry = stream.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (entryName.endsWith(".ttf") || entryName.endsWith(".otf")) {
                Font font = registerFont(stream);
                stream.closeEntry();
                return font;
            }
        }
        return null;
    }

    public static boolean isRegistered(String fontName) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String font : ge.getAvailableFontFamilyNames()) {
            if (font.equals(fontName)) {
                return true;
            }
        }
        return false;
    }

    public static Font registerFont(InputStream stream) throws IOException, FontFormatException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
        if (!ge.registerFont(font)) {
            throw new RuntimeException("can't register font from stream." + font.getName());
        }
        return font;
    }

}
