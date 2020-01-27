package com.dbr.abstr;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public abstract class AbstractCSVImporter<T> {

    private Logger log = Logger.getLogger(this.getClass().getName());

    private String rowSplitter;
    private String columnSplitter;

    protected AbstractCSVImporter(String rowSplitter, String columnSplitter) {
        this.rowSplitter = rowSplitter;
        this.columnSplitter = columnSplitter;
    }

    public List<T> getObjects(File file, Boolean withHeaderRow) throws Exception {
        return getObjects(file, null, withHeaderRow);
    }

    public List<T> getObjects(File file, Integer maxLineSize, Boolean withHeaderRow) throws Exception {
        List<T> retval = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        int index = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            index++;
            if (index == 1 && withHeaderRow) {
                continue;
            }
            retval.add(mapObject(line));
            if (maxLineSize != null && maxLineSize.intValue() < index) {
                break;
            }
            if (index % 100 == 0) {
                log.info(String.format("read 100 rows, total rows: %d", index));
            }
        }
        scanner.close();
        return retval;
    }

    public List<T> toObjects(String content, Boolean withHeaderRow) throws Exception {
        List<T> retval = new ArrayList<>();

        List<String> rowsContent = Lists.newArrayList(Splitter.on(this.rowSplitter).split(content));
        int startIndex = withHeaderRow ? 1 : 0;
        for (int i = startIndex; i < rowsContent.size(); i++) {
            String row = rowsContent.get(i);
            retval.add(mapObject(row));
            if (i % 100 == 0) {
                log.info(String.format("read 100 rows, total rows: %d", i));
            }
        }
        return retval;
    }

    public T mapObject(String row) throws Exception {
        List<String> columns = Lists.newArrayList(Splitter.on(this.columnSplitter).split(row));
        return mapObject(columns);
    }

    protected abstract T mapObject(List<String> columns) throws Exception;

}
