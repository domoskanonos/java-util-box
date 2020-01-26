package com.dbr.util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class StringUtilTest {

    @Test
    public void getType() {
        Assert.assertEquals(StringUtil.getType("1", "0", "1", "1"), "Boolean");
        Assert.assertEquals(StringUtil.getType("1", "0", "1", "2"), "Short");
        Assert.assertEquals(StringUtil.getType("1", "0", "32768", "2"), "Integer");
        Assert.assertEquals(StringUtil.getType("1", "0", "2147483648", "2"), "Long");
        Assert.assertEquals(StringUtil.getType("1", "0", "B", "2"), "String");
    }
}