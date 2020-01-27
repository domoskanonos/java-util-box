package com.dbr.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;

public class CSVUtil {

    private static Logger _log = Logger.getLogger(CSVUtil.class.getName());

    public static String readContent(File file, Integer rowCount, String rowSplit) throws FileNotFoundException {
        String content = "";
        Scanner scanner = new Scanner(file);
        int lines = 0;
        while (scanner.hasNextLine()) {
            lines++;
            content += scanner.nextLine() + rowSplit;
            if (lines % 100 == 0) {
                _log.info(String.format("read 100 rows, total rows: %d", lines));
            }
            if (rowCount != null && lines > rowCount.intValue()) {
                break;
            }
        }
        scanner.close();
        return content;
    }

}
