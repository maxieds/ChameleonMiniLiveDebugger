/*
This program (The Chameleon Mini Live Debugger) is free software written by
Maxie Dion Schmidt: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The complete license provided with source distributions of this library is
available at the following link:
https://github.com/maxieds/ChameleonMiniLiveDebugger
*/

package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.UUID;

/**
 * <h1>Utils</h1>
 * Misc utility functions for the application.
 *
 * @author Maxie D. Schmidt
 * @since 12/31/17
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static int getFirstResponseCodeIndex(String s) {
        Pattern pattern = Pattern.compile("^\\w*(\\d{3})");
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()) {
            return matcher.start(1);
        }
        else {
            return 0;
        }
    }

    public static String formatUIDString(String hexBytesStr, String delim) {
        if(hexBytesStr == null || hexBytesStr.length() == 0) {
            return "DEVICE UID";
        }
        else if(hexBytesStr.equals("NO UID.")) {
            return hexBytesStr;
        }
        return hexBytesStr.replaceAll("..(?!$)", "$0" + delim);
    }

    /**
     * Converts a string representation of a two-digit byte into a corresponding byte type.
     * @param byteStr
     * @return byte representation of the String
     */
    public static byte hexString2Byte(String byteStr) {
        if (byteStr.length() != 2) {
            Log.e(TAG, "Invalid Byte String: " + byteStr);
            //Crashlytics.log(Log.WARN, TAG, "Invalid Byte String Encountered: " + byteStr);
            return 0x00;
        }
        int lsb = Character.digit(byteStr.charAt(1), 16);
        int msb = Character.digit(byteStr.charAt(0), 16);
        return (byte) (lsb | msb << 4);
    }

    // TODO: javadoc
    public static byte[] hexString2Bytes(String byteStr) {
        if (byteStr.length() % 2 != 0) { // left-pad the string:
            byteStr =  byteStr + "0";
        }
        byte[] byteRep = new byte[byteStr.length() / 2];
        for (int b = 0; b < byteStr.length(); b += 2)
            byteRep[b / 2] = hexString2Byte(byteStr.substring(b, b + 2));
        return byteRep;
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
        } else
            return '.';
    }

    /**
     * Returns an ascii string representing the byte array.
     * @param bytes
     * @return String ascii representation of the byte array
     */
    public static String bytes2Ascii(byte[] bytes) {
        StringBuilder byteStr = new StringBuilder();
        for (int b = 0; b < bytes.length; b++)
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
        if (bytes == null)
            return "<NULL>";
        else if (bytes.length == 0)
            return "";
        StringBuilder hstr = new StringBuilder();
        hstr.append(String.format(Locale.ENGLISH, "%02x", bytes[0]));
        for (int b = 1; b < bytes.length; b++)
            hstr.append(" " + String.format(Locale.ENGLISH, "%02x", bytes[b]));
        return hstr.toString();
    }

    /**
     * Returns a 32-bit integer obtained from the bytes (in lex. order).
     * @param bytesArray
     * @return 32-bit integer
     */
    public static int bytes2Integer32(byte[] bytesArray) {
        int rint = 0;
        for (int b = 0; b < Math.min(bytesArray.length, 4); b++) {
            int rintMask = 0x000000ff << 4 * b;
            rint |= ((int) bytesArray[b]) & rintMask;
        }
        return rint;
    }

    /**
     * Reverses the order of the bytes in the array.
     * @param bytes
     * @return byte[] reversed array
     */
    public static byte[] reverseBytes(byte[] bytes) {
        byte[] revArray = new byte[bytes.length];
        for (int b = 0; b < revArray.length; b++)
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
        for (int s = 0; s < 4; s++) {
            rb = rb | ((bint & mask) >> (8 / (b + 1) - 1));
            mask = mask >>> 1;
        }
        mask = 0x01;
        for (int s = 0; s < 4; s++) {
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
        for (int b = 0; b < bytes.length; b++) {
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
        while ((csvLine = br.readLine()) != null) {
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

    /**
     * Get random bytes seeded by the time. For use with generating random UID's.
     * @param numBytes
     * @return
     */
    public static byte[] getRandomBytes(int numBytes) {
        Random rnGen = new Random(System.currentTimeMillis());
        byte[] randomBytes = new byte[numBytes];
        for (int b = 0; b < numBytes; b++)
            randomBytes[b] = (byte) rnGen.nextInt(0xff);
        return randomBytes;
    }

    public static String trimString(String str, int maxNumChars) {
        if (str.length() <= maxNumChars)
            return str;
        return str.substring(0, maxNumChars) + "...";
    }

    /**
     * Computes a measure of entropy (i.e., how likely the payload data is to be encrypted) by
     * compressing the input byte array and comparing the resulting size (in bytes) to the
     * original array.
     * @param inputBytes
     * @return entropy rating
     */
    public static double computeByteArrayEntropy(byte[] inputBytes) {
        Deflater cmpr = new Deflater();
        cmpr.setLevel(Deflater.BEST_COMPRESSION);
        cmpr.setInput(inputBytes);
        cmpr.finish();
        int cmprByteCount = 0;
        while (!cmpr.finished()) {
            cmprByteCount += cmpr.deflate(new byte[1024]);
        }
        double entropyRatio = (double) cmprByteCount / inputBytes.length;
        Log.i(TAG, String.format(Locale.ENGLISH, "Compressed #%d bytes to #%d bytes ... Entropy ratio = %1.4g", inputBytes.length, cmprByteCount, entropyRatio));
        return entropyRatio;
    }

    public static int parseInt(String numberStr) {
        try {
            int rNum = Integer.parseInt(numberStr);
            return rNum;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Pretty prints the DUMP_MFU command output according to this link:
     * https://www.manualslib.com/manual/815771/Advanced-Card-Acr122s.html?page=47#manual
     * https://shop.sonmicro.com/Downloads/MIFAREULTRALIGHT-UM.pdf
     * @param mfuBytes
     * @return Pretty String Format of the MFU tag
     */
    public static String prettyPrintMFU(String mfuBytes) {
        String pp = " PG | B0 B1 B2 B3 | LOCK AND/OR SPECIAL REGISTERS\n";
        pp += "=================================================\n";
        for (int page = 0; page < mfuBytes.length(); page += 8) {
            int pageNumber = page / 8;
            Log.i(TAG, String.format("prettyPrintMFU: page#% 2d, page=% 2d", pageNumber, page));
            byte[] pageData = Utils.hexString2Bytes(mfuBytes.substring(page, Math.min(page + 8, mfuBytes.length()) - 1));
            if (pageData.length < 4) {
                byte[] pageDataResized = new byte[4];
                System.arraycopy(pageData, 0, pageDataResized, 0, pageData.length);
                pageData = pageDataResized;
            }
            String specialRegs = "";
            int lockBits = 0;
            if (pageNumber == 0) {
                specialRegs = "SN0-2:BCC0";
            } else if (pageNumber == 1) {
                specialRegs = "SN3-6";
            } else if (pageNumber == 2) {
                specialRegs = "BCC1:INT:LOCK0-1";
                lockBits = (pageData[2] << 2) | pageData[3];
            } else if (pageNumber == 3) {
                specialRegs = "OTP0-3";
            } else if (pageNumber >= 4 && pageNumber <= 15) {
                int lockBit = (1 << (15 - pageNumber)) & 0x0000 & lockBits;
                specialRegs = (lockBit == 0) ? "UNLOCKED" : "NO ACCESS";
            } else if (pageNumber == 16) {
                specialRegs = "CFG0";
            } else if (pageNumber == 17) {
                specialRegs = "CFG1";
            } else if (pageNumber == 18) {
                specialRegs = "PWD0-3";
            } else if (pageNumber == 19) {
                specialRegs = "PACK0-1:RFU0-1";
            } else {
                specialRegs = "ONE WAY CTRS";
            }
            String pageLine = String.format(" % 2d | %02x %02x %02x %02x | [%s]", pageNumber, pageData[0], pageData[1], pageData[2], pageData[3], specialRegs);
            if (page + 4 < mfuBytes.length())
                pageLine += "\n";
            pp += pageLine;
        }
        return pp;
    }

    public UUID getUUIDFromInteger(int id) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = id & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public static int getColorFromTheme(int colorResID) {
        return ThemesConfiguration.getThemeColorVariant(colorResID);
    }

    private static final int GPS_LONGITUDE_CINDEX = 0;
    private static final int GPS_LATITUDE_CINDEX = 1;

    public static String[] getGPSLocationCoordinates() {
        try {
            LocationManager locationManager = (LocationManager) LiveLoggerActivity.getInstance().getSystemService(Context.LOCATION_SERVICE);
            Location locGPSProvider = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locNetProvider = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            long gpsProviderLocTime = 0, netProviderLocTime = 0;
            if (locGPSProvider != null) {
                gpsProviderLocTime = locGPSProvider.getTime();
            }
            if (locNetProvider != null) {
                netProviderLocTime = locNetProvider.getTime();
            }
            Location bestLocProvider = (gpsProviderLocTime - netProviderLocTime > 0) ? locGPSProvider : locGPSProvider;
            String[] gpsAttrsArray = new String[]{
                    String.format(Locale.ENGLISH, "%g", bestLocProvider.getLatitude()),
                    String.format(Locale.ENGLISH, "%g", bestLocProvider.getLongitude())
            };
            return gpsAttrsArray;
        } catch(SecurityException secExcpt) {
            Log.w(TAG, "Exception getting GPS coords: " + secExcpt.getMessage());
            secExcpt.printStackTrace();
            return new String[] {
                    "UNK",
                    "UNK"
            };
        }
    }

    public static String getGPSLocationString() {
        String[] gpsCoords = Utils.getGPSLocationCoordinates();
        String gpsLocStr = String.format(Locale.ENGLISH, " -- Location at %s LONG, %s LAT -- ",
                gpsCoords[Utils.GPS_LONGITUDE_CINDEX], gpsCoords[Utils.GPS_LATITUDE_CINDEX]);
        return gpsLocStr;
    }

    public static void displayToastMessage(String toastMsg, int msgDuration) {
        Toast toastDisplay = Toast.makeText(LiveLoggerActivity.getInstance(), toastMsg, msgDuration);
        toastDisplay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        int toastBackgroundColor = Utils.getColorFromTheme(R.attr.colorPrimaryDark);
        toastDisplay.getView().getBackground().setColorFilter(toastBackgroundColor, PorterDuff.Mode.SRC_IN);
        toastDisplay.getView().setAlpha(0.70f);
        toastDisplay.show();
        Log.i(TAG, "TOAST MSG DISPLAYED: " + toastMsg);
    }

    public static void displayToastMessageShort(String toastMsg) {
         Utils.displayToastMessage(toastMsg, Toast.LENGTH_SHORT);
    }

    public static void displayToastMessageLong(String toastMsg) {
        Utils.displayToastMessage(toastMsg, Toast.LENGTH_LONG);
    }

    private static final short[] CRC16_LOOKUP_TABLE = new short[] {
            (short) 0x0000, (short) 0x1189, (short) 0x2312, (short) 0x329B, (short) 0x4624, (short) 0x57AD, (short) 0x6536, (short) 0x74BF,
            (short) 0x8C48, (short) 0x9DC1, (short) 0xAF5A, (short) 0xBED3, (short) 0xCA6C, (short) 0xDBE5, (short) 0xE97E, (short) 0xF8F7,
            (short) 0x1081, (short) 0x0108, (short) 0x3393, (short) 0x221A, (short) 0x56A5, (short) 0x472C, (short) 0x75B7, (short) 0x643E,
            (short) 0x9CC9, (short) 0x8D40, (short) 0xBFDB, (short) 0xAE52, (short) 0xDAED, (short) 0xCB64, (short) 0xF9FF, (short) 0xE876,
            (short) 0x2102, (short) 0x308B, (short) 0x0210, (short) 0x1399, (short) 0x6726, (short) 0x76AF, (short) 0x4434, (short) 0x55BD,
            (short) 0xAD4A, (short) 0xBCC3, (short) 0x8E58, (short) 0x9FD1, (short) 0xEB6E, (short) 0xFAE7, (short) 0xC87C, (short) 0xD9F5,
            (short) 0x3183, (short) 0x200A, (short) 0x1291, (short) 0x0318, (short) 0x77A7, (short) 0x662E, (short) 0x54B5, (short) 0x453C,
            (short) 0xBDCB, (short) 0xAC42, (short) 0x9ED9, (short) 0x8F50, (short) 0xFBEF, (short) 0xEA66, (short) 0xD8FD, (short) 0xC974,
            (short) 0x4204, (short) 0x538D, (short) 0x6116, (short) 0x709F, (short) 0x0420, (short) 0x15A9, (short) 0x2732, (short) 0x36BB,
            (short) 0xCE4C, (short) 0xDFC5, (short) 0xED5E, (short) 0xFCD7, (short) 0x8868, (short) 0x99E1, (short) 0xAB7A, (short) 0xBAF3,
            (short) 0x5285, (short) 0x430C, (short) 0x7197, (short) 0x601E, (short) 0x14A1, (short) 0x0528, (short) 0x37B3, (short) 0x263A,
            (short) 0xDECD, (short) 0xCF44, (short) 0xFDDF, (short) 0xEC56, (short) 0x98E9, (short) 0x8960, (short) 0xBBFB, (short) 0xAA72,
            (short) 0x6306, (short) 0x728F, (short) 0x4014, (short) 0x519D, (short) 0x2522, (short) 0x34AB, (short) 0x0630, (short) 0x17B9,
            (short) 0xEF4E, (short) 0xFEC7, (short) 0xCC5C, (short) 0xDDD5, (short) 0xA96A, (short) 0xB8E3, (short) 0x8A78, (short) 0x9BF1,
            (short) 0x7387, (short) 0x620E, (short) 0x5095, (short) 0x411C, (short) 0x35A3, (short) 0x242A, (short) 0x16B1, (short) 0x0738,
            (short) 0xFFCF, (short) 0xEE46, (short) 0xDCDD, (short) 0xCD54, (short) 0xB9EB, (short) 0xA862, (short) 0x9AF9, (short) 0x8B70,
            (short) 0x8408, (short) 0x9581, (short) 0xA71A, (short) 0xB693, (short) 0xC22C, (short) 0xD3A5, (short) 0xE13E, (short) 0xF0B7,
            (short) 0x0840, (short) 0x19C9, (short) 0x2B52, (short) 0x3ADB, (short) 0x4E64, (short) 0x5FED, (short) 0x6D76, (short) 0x7CFF,
            (short) 0x9489, (short) 0x8500, (short) 0xB79B, (short) 0xA612, (short) 0xD2AD, (short) 0xC324, (short) 0xF1BF, (short) 0xE036,
            (short) 0x18C1, (short) 0x0948, (short) 0x3BD3, (short) 0x2A5A, (short) 0x5EE5, (short) 0x4F6C, (short) 0x7DF7, (short) 0x6C7E,
            (short) 0xA50A, (short) 0xB483, (short) 0x8618, (short) 0x9791, (short) 0xE32E, (short) 0xF2A7, (short) 0xC03C, (short) 0xD1B5,
            (short) 0x2942, (short) 0x38CB, (short) 0x0A50, (short) 0x1BD9, (short) 0x6F66, (short) 0x7EEF, (short) 0x4C74, (short) 0x5DFD,
            (short) 0xB58B, (short) 0xA402, (short) 0x9699, (short) 0x8710, (short) 0xF3AF, (short) 0xE226, (short) 0xD0BD, (short) 0xC134,
            (short) 0x39C3, (short) 0x284A, (short) 0x1AD1, (short) 0x0B58, (short) 0x7FE7, (short) 0x6E6E, (short) 0x5CF5, (short) 0x4D7C,
            (short) 0xC60C, (short) 0xD785, (short) 0xE51E, (short) 0xF497, (short) 0x8028, (short) 0x91A1, (short) 0xA33A, (short) 0xB2B3,
            (short) 0x4A44, (short) 0x5BCD, (short) 0x6956, (short) 0x78DF, (short) 0x0C60, (short) 0x1DE9, (short) 0x2F72, (short) 0x3EFB,
            (short) 0xD68D, (short) 0xC704, (short) 0xF59F, (short) 0xE416, (short) 0x90A9, (short) 0x8120, (short) 0xB3BB, (short) 0xA232,
            (short) 0x5AC5, (short) 0x4B4C, (short) 0x79D7, (short) 0x685E, (short) 0x1CE1, (short) 0x0D68, (short) 0x3FF3, (short) 0x2E7A,
            (short) 0xE70E, (short) 0xF687, (short) 0xC41C, (short) 0xD595, (short) 0xA12A, (short) 0xB0A3, (short) 0x8238, (short) 0x93B1,
            (short) 0x6B46, (short) 0x7ACF, (short) 0x4854, (short) 0x59DD, (short) 0x2D62, (short) 0x3CEB, (short) 0x0E70, (short) 0x1FF9,
            (short) 0xF78F, (short) 0xE606, (short) 0xD49D, (short) 0xC514, (short) 0xB1AB, (short) 0xA022, (short) 0x92B9, (short) 0x8330,
            (short) 0x7BC7, (short) 0x6A4E, (short) 0x58D5, (short) 0x495C, (short) 0x3DE3, (short) 0x2C6A, (short) 0x1EF1, (short) 0x0F78
    };

    public static byte[] calculateByteBufferCRC16(byte[] bufferBytes) {
        if(bufferBytes == null || bufferBytes.length == 0) {
            return new byte[0];
        }
        int byteCount = bufferBytes.length;
        short workingCRC = (short) 0xffff; // x25 CCITT-CRC16 seed
        for (int i = 0; i < byteCount; i++) {
            workingCRC = (short) (CRC16_LOOKUP_TABLE[(bufferBytes[i] ^ (workingCRC >>> 8)) & 0xff] ^ (workingCRC << 8));
        }
        byte[] crcBytes = {
                (byte) (workingCRC & 0x00ff),
                (byte) ((workingCRC >> 8) & 0x00ff)
        };
        return crcBytes;
    }

}