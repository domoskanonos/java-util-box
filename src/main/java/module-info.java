module com.dbr.util {
    requires commons.lang;
    requires org.apache.commons.codec;
    requires org.apache.commons.io;
    requires java.logging;
    requires java.desktop;
    requires guava;
    exports com.dbr.util;
    exports com.dbr.util.resource;
}