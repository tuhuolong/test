package com.ryeex.test.jrcp;

/**
 * Created by chenhao on 2018/1/5.
 */

public class Utils {
    public static byte[] hexStringToBytes(String hexString) {
        int stringLen = hexString.length();
        byte[] temp = new byte[stringLen];
        int resultLength = 0;
        int nibble = 0, i;
        byte nextByte = 0;
        for (i = 0; i < stringLen; i++) {
            char c = hexString.charAt(i);
            byte b = (byte) Character.digit(c, 16);

            if (b == -1) {
                if (!Character.isWhitespace(c))
                    throw new IllegalArgumentException("Not HexString character: " + c);
                continue;
            }
            if (nibble == 0) {
                nextByte = (byte) (b << 4);
                nibble = 1;
            } else {
                nextByte |= b;
                temp[resultLength++] = nextByte;
                nibble = 0;
            }
        } // for

        if (nibble != 0) {
            throw new IllegalArgumentException("odd number of characters.");
        } else {
            byte[] result = new byte[resultLength];
            System.arraycopy(temp, 0, result, 0, resultLength);
            return result;
        }
    }

    public static String toHexString(byte[] buffer, int offset, int length) {
        final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
        char[] chars = new char[3 * length];
        for (int j = offset; j < offset + length; ++j) {
            chars[3 * (j - offset)] = HEX_CHARS[(buffer[j] & 0xF0) >>> 4];
            chars[3 * (j - offset) + 1] = HEX_CHARS[buffer[j] & 0x0F];
            chars[3 * (j - offset) + 2] = ' ';
        }
        return new String(chars);
    }
}
