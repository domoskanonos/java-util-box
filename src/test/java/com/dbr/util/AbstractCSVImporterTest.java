package com.dbr.util;

import com.dbr.abstr.AbstractCSVImporter;
import lombok.Data;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbstractCSVImporterTest {

    @Data
    public class CSVTestKlasse {
        private String name;
        private String surname;
        private Date birthday;
        private Integer count;
        private Boolean isActive;
    }

    @Test
    public void export() throws IOException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        File file = new File("P:/test.csv");
        AbstractCSVImporter<CSVTestKlasse> abstractCSVImporter = new AbstractCSVImporter<CSVTestKlasse>("\n", ";") {
            @Override
            protected CSVTestKlasse mapObject(List<String> columns) throws Exception {
                return null;
            }
        };
        List<CSVTestKlasse> test = new ArrayList<>();
        test.add(createTestObject());
        test.add(createTestObject());
        test.add(createTestObject());
        test.add(createTestObject());
        test.add(createTestObject());
        test.add(createTestObject());
        test.add(createTestObject());
        test.add(createTestObject());
        abstractCSVImporter.toFile(file, test, false, true);
    }

    private CSVTestKlasse createTestObject() {
        CSVTestKlasse CSVTestKlasse = new CSVTestKlasse();
        CSVTestKlasse.setBirthday(new Date());
        CSVTestKlasse.setName("Name");
        CSVTestKlasse.setSurname("Nachname");
        CSVTestKlasse.setCount(10);
        CSVTestKlasse.setIsActive(true);
        return CSVTestKlasse;
    }
}