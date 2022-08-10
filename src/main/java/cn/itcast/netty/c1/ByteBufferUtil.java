package cn.itcast.netty.c1;

public class ByteBufferUtil {

    private static final char[] BYTE2CHAR = new char[256];
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];
    private static final String[] HEX_PADDING = new String[256];
    private static final String[] HEXDUMP_ROW_PREFIXES = new String[65536 >>> 4];
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] BYTE_PADDING = new String[256];
}
