package com.dbr.abstr;

import com.dbr.util.JavaUtil;
import com.dbr.util.StringUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public abstract class AbstractCSVImporter<T> {

    private Logger log = Logger.getLogger(this.getClass().getName());

    protected String rowSplitter;
    protected String columnSplitter;
    protected String encoding = "UTF-8";

    protected AbstractCSVImporter(String rowSplitter, String columnSplitter) {
        this.rowSplitter = rowSplitter;
        this.columnSplitter = columnSplitter;
    }

    public void toFile(File file, T item, boolean append) throws IllegalAccessException, IOException, IntrospectionException, InvocationTargetException {
        StringBuffer sb = new StringBuffer();
        int columnCount = 0;
        Class<?> itemClazz = item.getClass();
        for (Field field : itemClazz.getDeclaredFields()) {
            if (columnCount > 0) {
                sb.append(this.columnSplitter);
            }
            sb.append(StringUtil.toString(JavaUtil.invokeGetter(item, field)));
            columnCount++;
        }
        sb.append(rowSplitter);
        FileUtils.writeStringToFile(file, sb.toString(), encoding, append);
    }

    public void toFile(File file, List<T> items, boolean append, boolean withHeaderRow) throws IOException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        if (!append) {
            FileUtils.writeStringToFile(file, "", encoding, append);
            if (withHeaderRow && items.size() > 0) {
                StringBuffer sb = new StringBuffer();
                int columnCount = 0;
                for (Field field : items.get(0).getClass().getDeclaredFields()) {
                    if (columnCount > 0) {
                        sb.append(this.columnSplitter);
                    }
                    sb.append(field.getName());
                    columnCount++;
                }
                sb.append(rowSplitter);
                FileUtils.writeStringToFile(file, sb.toString(), encoding, true);
            }
        }
        for (T item : items) {
            toFile(file, item, true);
        }
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
