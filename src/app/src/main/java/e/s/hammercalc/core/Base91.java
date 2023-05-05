package e.s.hammercalc.core;

import java.util.Arrays;

/**
 * Base91 transcoding for byte strings.
 * This is an encoding for printable ASCII characters which
 * gives a very compact representation.
 * */
public class Base91 {

    /** Characters used in the encoding and decoding. These can be modified, but must match for any given input/output.
     * All chars in the string must be unique, and there must be exactly 91 chars. */
    private static final String CODE_PAGE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"";

    private static final int BASE = 91;

    /** lookup table for encoding */
    private static byte[] ENCODING_TABLE;
    /** lookup table for decoding */
    private static byte[] DECODING_TABLE;

    /** Takes array of byte values, returns array of character values */
    public static byte[] encode(byte[] data) {
        if (ENCODING_TABLE == null || DECODING_TABLE == null) PrepareTables();
        int worstCaseSize = (data.length * 5) / 4;
        byte[] out = new byte[worstCaseSize];
        int ebq = 0;
        int en = 0;
        int p = 0;

        // Main
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < data.length; i++) {
            int b = data[i];
            ebq |= (b & 255) << en;
            en += 8;
            if (en > 13) {
                int ev = ebq & 8191;

                if (ev > 88) {
                    ebq >>= 13;
                    en -= 13;
                } else {
                    ev = ebq & 16383;
                    ebq >>= 14;
                    en -= 14;
                }
                out[p++]= ENCODING_TABLE[ev % BASE];
                out[p++]= ENCODING_TABLE[ev / BASE];
            }
        }

        // Flush
        if (en > 0) {
            out[p++]= ENCODING_TABLE[ebq % BASE];
            if (en > 7 || ebq > 90) {
                out[p++]= ENCODING_TABLE[ebq / BASE];
            }
        }

        return Arrays.copyOfRange(out, 0, p - 1);
    }

    private static void PrepareTables() {
        ENCODING_TABLE = CODE_PAGE.getBytes();

        DECODING_TABLE = new byte[BASE];
        for (int i = 0; i < 256; i++) {
            DECODING_TABLE[i] = -1; // any -1 is invalid
        }
        for (int i = 0; i < BASE; ++i) {
            DECODING_TABLE[ENCODING_TABLE[i]] = (byte)(i & 0xFF);
        }
    }

    /** Takes array of character values, returns array of byte values */
    public static byte[] decode(byte[] data) throws Exception {
        if (ENCODING_TABLE == null || DECODING_TABLE == null) PrepareTables();
        int dbq = 0;
        int dn = 0;
        int dv = -1;
        int p = 0;

        byte[] output = new byte[data.length]; // should be average of 23% smaller, but we're conservative for simplicity

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < data.length; ++i) {
            if (DECODING_TABLE[data[i]] == -1) throw new Exception("Invalid data");
            if (dv == -1) {
                dv = DECODING_TABLE[data[i]];
            } else {
                dv += DECODING_TABLE[data[i]] * BASE;
                dbq |= dv << dn;
                dn += (dv & 8191) > 88 ? 13 : 14;
                do {
                    output[p++]=(byte)(dbq & 0xFF);
                    dbq >>= 8;
                    dn -= 8;
                } while (dn > 7);
                dv = -1;
            }
        }

        if (dv != -1) {
            output[p++]=(byte)((dbq | dv << dn) & 0xFF);
        }

        return Arrays.copyOfRange(output, 0, p - 1);
    }
}
