package com.dbr.util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class StringUtilTest {

    @Test
    public void getType() {
        Assert.assertEquals(StringUtil.getType("1", "0", "1", "1"), DataTypes.TYPE_BOOLEAN);
        Assert.assertEquals(StringUtil.getType("1", "0", "1", "2"), DataTypes.TYPE_SHORT);
        Assert.assertEquals(StringUtil.getType("1", "0", "32768", "2"), DataTypes.TYPE_INTEGER);
        Assert.assertEquals(StringUtil.getType("1", "0", "2147483648", "2"), DataTypes.TYPE_LONG);
        Assert.assertEquals(StringUtil.getType("1", "0", "B", "2"), DataTypes.TYPE_STRING);
        Assert.assertEquals(StringUtil.getType("1", "0", "2001-07-04T12:08:56.235-0700", "2"), DataTypes.TYPE_STRING);
        Assert.assertEquals(StringUtil.getType("2001-07-04T12:08:56.235-0700", "2001-07-04T12:08:56.235-0700", "2001-07-04T12:08:56.235-0700", "2001-07-04T12:08:56.235-0700"), DataTypes.TYPE_DATE_ISO8601);
        Assert.assertEquals(StringUtil.getType("2019-06-25T11:55:18Z", "2019-06-25T11:55:18Z", "2019-06-25T11:55:18Z", "2019-06-25T11:55:18Z"), DataTypes.TYPE_DATE_ISO8601);
    }
}