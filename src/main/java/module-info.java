module com.dbr.util {
    requires java.logging;
    requires java.desktop;
    requires com.google.common;
    requires org.apache.commons.text;
    requires org.apache.commons.codec;
    requires org.apache.commons.io;
    exports com.dbr.util;
    exports com.dbr.util.resource;
}