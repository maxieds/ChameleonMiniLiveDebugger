package com.maxieds.chameleonminilivedebugger;

import android.text.format.Time;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * <h1>Utils</h1>
 * Misc utility functions for the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class Utils {

    /**
     * Converts a string representation of a two-digit byte into a corresponding byte type.
     * @param byteStr
     * @return byte representation of the String
     */
    public static byte hexString2Byte(String byteStr) {
        if (byteStr.length() != 2) {
            Log.e(TAG, "Invalid Byte String: " + byteStr);
            return 0x00;
        }
        int lsb = Character.digit(byteStr.charAt(1), 16);
        int msb = Character.digit(byteStr.charAt(0), 16);
        return (byte) (lsb | msb << 4);
    }

    /**
     * Returns an ascii print character (or '.' representation for non-print characters) of the input byte.
     * @param b
     * @return char print character (or '.')
     */
    public static char byte2Ascii(byte b) {
        int decAsciiCode = (int) b;
        if (b >= 32 && b <= 127) {
            char ch = (char) b;
            return ch;
        }
        else
            return '.';
    }

    /**
     * Returns an ascii string representing the byte array.
     * @param bytes
     * @return String ascii representation of the byte array
     */
    public static String bytes2Ascii(byte[] bytes) {
        StringBuilder byteStr = new StringBuilder();
        for(int b = 0; b < bytes.length; b++)
            byteStr.append(String.valueOf(byte2Ascii(bytes[b])));
        return byteStr.toString();
    }

    /**
     * Returns a space-separated string of the input bytes in their two-digit
     * hexadecimal format.
     * @param bytes
     * @return String hex string representation
     */
    public static String bytes2Hex(byte[] bytes) {
        if(bytes == null)
            return "<NULL>";
        else if(bytes.length == 0)
            return "";
        StringBuilder hstr = new StringBuilder();
        hstr.append(String.format(Locale.ENGLISH, "%02x", bytes[0]));
        for(int b = 1; b < bytes.length; b++)
            hstr.append(" " + String.format(Locale.ENGLISH, "%02x", bytes[b]));
        return hstr.toString();
    }

    /**
     * Reverses the order of the bytes in the array.
     * @param bytes
     * @return byte[] reversed array
     */
    public static byte[] reverseBytes(byte[] bytes) {
        byte[] revArray = new byte[bytes.length];
        for(int b = 0; b < revArray.length; b++)
            revArray[revArray.length - b - 1] = bytes[b];
        return revArray;
    }

    /**
     * Returns a byte with its bits reversed in lexocographical order.
     * @param b
     * @return byte reversed byte
     * @ref Utils.reverseBits (reverse hex representation of a byte array).
     */
    public static byte reverseBits(byte b) {
        int bint = (int) b;
        int rb = 0x00;
        int mask = 0x01 << 7;
        for(int s = 0; s < 4; s++) {
            rb = rb | ((bint & mask) >> (8 / (b + 1) - 1));
            mask = mask >>> 1;
        }
        mask = 0x01;
        for(int s = 0; s < 4; s++) {
            rb = rb | ((bint & mask) << (8 / (b + 1) - 1));
            mask = mask << 1;
        }
        return (byte) rb;
    }

    /**
     * Computes the reverse hex representation of the byte array.
     * @param bytes
     * @return byte[] reversed in initial order and byte-wise bits
     */
    public static byte[] reverseBits(byte[] bytes) {
        byte[] revBytes = reverseBytes(bytes);
        for(int b = 0; b < bytes.length; b++) {
            revBytes[b] = reverseBits(revBytes[b]);
        }
        return revBytes;
    }

    /**
     * Returns a standard timestamp of the current Android device's time.
     * @return String timestamp (format: %Y-%m-%d-%T)
     */
    public static String getTimestamp() {
        Time currentTime = new Time();
        currentTime.setToNow();
        return currentTime.format("%Y-%m-%d-%T");
    }

    /**
     * Parses a CSV (comma delimited) file.
     * @param fdStream
     * @return List of String[] separated line entries
     * @throws IOException
     * @see ApduUtils
     * @see res/raw/*
     */
    public static List<String[]> readCSVFile(InputStream fdStream) throws IOException {
        List<String[]> csvLines = new ArrayList<String[]>();
        BufferedReader br = new BufferedReader(new InputStreamReader(fdStream));
        String csvLine;
        while((csvLine = br.readLine()) != null) {
            String[] parsedRow = csvLine.split(",");
            csvLines.add(parsedRow);
        }
        fdStream.close();
        return csvLines;
    }

    /**
     * Determine whether an input string is in hex format.
     * @param str
     * @return boolean truth value
     */
    public static boolean stringIsHexadecimal(String str) {
        return str.matches("-?[0-9a-fA-F]+");
    }

    /**
     * Determine whether an input string is in purely decimal format.
     * @param str
     * @return boolean truth value
     */
    public static boolean stringIsDecimal(String str) {
        return str.matches("-?[0-9]+");
    }

}