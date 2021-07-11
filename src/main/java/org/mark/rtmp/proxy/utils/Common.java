package org.mark.rtmp.proxy.utils;

import java.util.Random;

public class Common {

    private static final Random RANDOM_FACTORY = new Random();

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * generate random byte array
     *
     * @param size array size
     * @return random byte array
     */
    public static byte[] generateRandomByteArray(int size) {
        byte[] output = new byte[size];
        RANDOM_FACTORY.nextBytes(output);
        return output;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * little end 24 bits;
     *
     * @param bytes bytes
     * @return result
     */
    public static int readIntMediumLE(byte[] bytes) {
        return (bytes[0]) & 0xff | ((bytes[1] & 0xff) << 8) | ((bytes[2] & 0xff) << 16);
    }

    /**
     * little end 32 bits;
     *
     * @param bytes bytes
     * @return result
     */
    public static int readIntLE(byte[] bytes) {
        return (bytes[0]) & 0xff | ((bytes[1] & 0xff) << 8) | ((bytes[2] & 0xff) << 16) | ((bytes[3] & 0xff) << 24);
    }

    /**
     * big end of 24 bits;
     *
     * @param bytes bytes
     * @return res
     */
    public static int readIntMedium(byte[] bytes) {
        return ((bytes[0] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[2] & 0xff);
    }

    /**
     * big end of 24 bits;
     *
     * @param bytes bytes
     * @return res
     */
    public static int readInt(byte[] bytes) {
        return ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | (bytes[2] & 0xff) << 8 | (bytes[3] & 0xff);
    }

}
