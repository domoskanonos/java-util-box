package com.dbr.util;

import com.dbr.abstr.AbstractCSVImporter;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbstractCSVImporterTest {

    public class CSVTestKlasse {
        private String name;
        private String surname;
        private Date birthday;
        private Integer count;
        private Boolean isActive;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getActive() {
            return isActive;
        }

        public void setActive(Boolean active) {
            isActive = active;
        }
    }

    @Test
    public void export() throws IOException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        File file = new File(System.getProperty("user.home") + "/test.csv");
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
        CSVTestKlasse.setActive(true);
        return CSVTestKlasse;
    }
}