package e.s.hammercalc.core;

import java.util.Arrays;

/**
 * Basic big-integer class.
 * <p></p>
 * We specifically don't use the Java BCL version, as we're
 * aiming for maximum portability.
 */
@SuppressWarnings({"ForLoopReplaceableByForEach", "ManualArrayCopy"})
public class LargeInt {
    // Values of this int

    /** -1 means -ve; +1 means +ve; 0 means 0; Any other value is invalid*/
    private int _sign;
    /** array of ints with [0] being the most significant */
    private int[] _magnitude;

    // cached values

    /** cache of bitCount() value. -1 is 'cache dirty' */
    private transient int _nBits = -1;
    /** cache of bitLength() value. -1 is 'cache dirty' */
    private transient int _nBitLength = -1;
    /**  -m^(-1) mod b, b = 2^32 (for Montgomery multiplication) */
    private transient long _mQuote = -1L;

    /** mask for lower 32 bits of 64 bit source*/
    private static final long IntMask = 0xFFFFffffL;

    /** Large int = 0 */
    public static final LargeInt ZERO = new LargeInt(0, new byte[0]);
    /** Large int = -1 */
    public static final LargeInt NEG_ONE = valueOf(1).negate();
    /** Large int = +1 */
    public static final LargeInt ONE = valueOf(1);
    /** Large int = +2 */
    public static final LargeInt TWO = valueOf(2);
    /** Large int = +10 */
    public static final LargeInt TEN = valueOf(10);

    /** Large int with an invalid value -- Not A Number */
    public static final LargeInt LARGE_NAN = new LargeInt();

    /** Return a large int with the same value as 'v' */
    public static LargeInt fromInt(int v){return LargeInt.valueOf(v);}

    /** Return a large int with the same value as 'v' */
    public static LargeInt fromLong(long v){return LargeInt.valueOf(v);}

    /** Return a large int with the value of floor(f) */
    public static LargeInt fromFloat(double f){
        //BigDecimal x = new BigDecimal(f);

        if (Double.isInfinite(f) || Double.isNaN(f)) return LARGE_NAN;

        // Translate the double into sign, exponent and significand, according
        // to the formulae in JLS, Section 20.10.22.
        long valBits = Double.doubleToLongBits(f);
        int sign = ((valBits >> 63) == 0 ? 1 : -1);
        int exponent = (int) ((valBits >> 52) & 0x7ffL);
        long significand = (exponent == 0
                ? (valBits & ((1L << 52) - 1)) << 1
                : (valBits & ((1L << 52) - 1)) | (1L << 52));
        exponent -= 1075;
        // At this point, val == sign * significand * 2**exponent.

        if (exponent >= 0) {
            LargeInt exp = TWO.pow(exponent);
            return fromInt(sign).multiply(fromLong(significand)).multiply(exp);
        } else {
            LargeInt exp = TWO.pow(-exponent);
            return fromInt(sign).multiply(fromLong(significand)).divide(exp);
        }
    }

    /** convert a set of longs into an array of LargeInts */
    public static LargeInt[] arrayFromInts(long... values) {
        if (values.length < 1) return new LargeInt[0];

        LargeInt[] result = new LargeInt[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = LargeInt.fromLong(values[i]);
        }
        return result;
    }

    /** Return a double that most closely matches this large int */
    public double toFloat(){
        return Double.parseDouble(toFloatString(21));
    }

    /** return a large int version of the given long */
    public static LargeInt valueOf(long val) {
        if (val == 0) return ZERO;

        boolean flip = false;
        if (val < 0) {val = -val; flip = true;}
        // store val into a byte array
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[7 - i] = (byte)val;
            val >>= 8;
        }

        LargeInt result = new LargeInt(b);
        if (flip) return result.negate();
        return result;
    }

    /** Create a large int with an 'invalid' value */
    protected LargeInt(){
        _sign = -42;
        _magnitude = new int[0];
    }

    /** Create a large int with a sign and magnitude */
    protected LargeInt(int sign, byte[] mag) {
        if (sign < -1) sign = -1;
        if (sign > 1) sign = 1;

        if (sign == 0) {
            _sign = 0;
            _magnitude = new int[0];
            return;
        }

        // copy bytes
        _magnitude = makeMagnitude(mag, 0);
        _sign = sign;
    }

    /** Create a large int with a sign and magnitude */
    private LargeInt(int sigNum, int[] mag) {
        _sign = sigNum;
        if (mag.length > 0) {
            int i = 0;
            while (i < mag.length && mag[i] == 0) i++;

            if (i == 0) {
                _magnitude = mag;
            } else {
                // strip leading 0 bytes
                int[] newMag = new int[mag.length - i];
                System.arraycopy(mag, i, newMag, 0, newMag.length);

                _magnitude = newMag;
                if (newMag.length == 0) _sign = 0;
            }
        } else {
            _magnitude = mag;
            _sign = 0;
        }
    }

    /** Parse a string into a large in, with base 10 */
    public LargeInt(String strVal){
        this(strVal, 10);
    }

    /** Restore a large int from a magnitude array. Result is always positive */
    public static LargeInt fromByteArray(byte[] compact) {
        return new LargeInt(compact);
    }

    /** Restore a large int from the format given in `toStorage()` */
    public static LargeInt fromStorage(byte[] byteVal) {
        if (byteVal.length < 1) return LARGE_NAN;

        LargeInt result = new LargeInt();
        result._sign = byteVal[0];

        if (byteVal.length == 1) {
            result._magnitude = new int[0];
            return result;
        }

        // strip leading zero bytes and return magnitude bytes
        result._magnitude = makeMagnitude(byteVal, 1);
        return result;
    }

    private void makeNaN(){
        _sign = -42;
        _magnitude = new int[0];
    }

    /** Parse a string into a large int, with the given radix (base number) */
    public LargeInt(String strVal, int rdx) {
        if (strVal.length() == 0) { // NaN valued
            makeNaN();
            return;
        }

        NumberStyles style;
        switch (rdx) {
            case 10:
                style = NumberStyles.Base10;
                break;
            case 16:
                style = NumberStyles.Base16;
                break;
            default:
                makeNaN();
                return;
        }

        strVal = strVal.trim();
        int index = 0;
        _sign = 1;

        if (strVal.charAt(0) == '-') {
            if (strVal.length() == 1) {
                makeNaN();
                return;
            }

            _sign = -1;
            index = 1;
        }

        // strip leading zeros from the string value
        while (index < strVal.length() && intParse(strVal, index, style) == 0) {
            index++;
        }

        if (index >= strVal.length()) {// zero value - we're done
            _sign = 0;
            _magnitude = new int[0];
            return;
        }

        // could we work out the max number of ints required to store
        // strVal.length digits in the given base, then allocate that
        // storage in one hit?, then generate the magnitude in one hit too?
        // (optimise this by taking chunks of digits instead?)

        LargeInt accum = ZERO;
        LargeInt radix = valueOf(rdx);
        while (index < strVal.length()) {
            int digit = intParse(strVal, index, style);
            if (digit >= 0) {
                accum = accum.multiply(radix).add(valueOf(digit));
            } else if (digit < -1){
                // fault
                makeNaN();
                return;
            }
            index++;
        }

        _magnitude = accum._magnitude;
    }

    /** parse a single character as an int value.
     * Returns -1 if char should be ignored. Returns -2 if invalid */
    private int intParse(String strVal, int index, NumberStyles style) {
        char c = strVal.charAt(index);
        // ignore common spacers '_', ' ', '-', '.', ','
        switch (c){
            case ' ': case '_': case '-': case '.': case ',': case '\'': return -1;
        }
        switch (style){
            case Base10:{
                int v = c - '0';
                if (v < 0) return -2;
                if (v <= 9) return v;
                return 0;
            }
            case Base16:{
                int v = c - '0';
                if (v < 0) return -2;
                if (v <= 9) return v;

                v = c - 'A';
                if (v > 5) v = c - 'a';
                if (v > 5 || v < 0) return -2;

                return 10 + v;
            }
        }
        return -2;
    }

    /** Create a large int with a preset byte value (positive sign only) */
    protected LargeInt(byte[] byteVal) {
        if (byteVal.length == 0) {
            _sign = 0;
            _magnitude = new int[0];
            return;
        }

        _sign = 1;

        // strip leading zero bytes and return magnitude bytes
        _magnitude = makeMagnitude(byteVal, 0);
    }

    /** Create a large int with a given number of bits, set to random values */
    public static LargeInt randomBits(int numBits) {
        if (numBits < 0) numBits = 0;

        int nBytes = (numBits + 7) / 8;

        byte[] b = new byte[nBytes];

        if (nBytes > 0) {
            nextRndBytes(b);
            // strip off any excess bits in the MSB
            b[0] &= rndMask[8 * nBytes - numBits];
        }

        LargeInt result = new LargeInt();
        result._magnitude = makeMagnitude(b, 0);
        result._sign = 1;
        result._nBits = -1;
        result._nBitLength = -1;
        return result;
    }

    /** return the absolute value of this integer */
    public LargeInt abs() {
        if (isNaN()) return LARGE_NAN;
        return (_sign >= 0) ? this : negate();
    }

    /**
     * return a = a + b - b preserved.
     */
    private int[] add(int[] a, int[] b) {
        int tI = a.length - 1;
        int vI = b.length - 1;
        long m = 0;

        while (vI >= 0) {
            m += (a[tI] & IntMask) + (b[vI--] & IntMask);
            a[tI--] = (int)m;
            m = (long)(m >>> 32);//or `m = (long)((ulong)m >> 32);` if no `>>>`
        }

        while (tI >= 0 && m != 0) {
            m += (a[tI] & IntMask);
            a[tI--] = (int)m;
            m = (long)(m >>> 32);
        }

        return a;
    }

    /** return this + val */
    public LargeInt add(LargeInt val) {
        if (isNaN() || val.isNaN()) return LARGE_NAN;
        if (val._sign == 0 || val._magnitude.length == 0) return this;
        if (_sign == 0 || _magnitude.length == 0) return val;

        if (val._sign < 0) {
            if (_sign > 0) return subtract(val.negate());
        } else {
            if (_sign < 0) return val.subtract(negate());
        }

        // both LargeInts are either +ve or -ve; set the sign later
        int[] mag, op;
        if (_magnitude.length < val._magnitude.length)
        {
            mag = new int[val._magnitude.length + 1];

            System.arraycopy(val._magnitude, 0, mag, 1, val._magnitude.length);
            op = _magnitude;
        }
        else
        {
            mag = new int[_magnitude.length + 1];

            System.arraycopy(_magnitude, 0, mag, 1, _magnitude.length);
            op = val._magnitude;
        }

        return new LargeInt(_sign, add(mag, op));
    }

    /** return number of bits set to 1 */
    public int bitCount() {
        if (_nBits == -1) {
            _nBits = 0;
            for (int i = 0; i < _magnitude.length; i++) {
                _nBits += bitCounts[_magnitude[i] & 0xff];
                _nBits += bitCounts[(_magnitude[i] >> 8) & 0xff];
                _nBits += bitCounts[(_magnitude[i] >> 16) & 0xff];
                _nBits += bitCounts[(_magnitude[i] >> 24) & 0xff];
            }
        }

        return _nBits;
    }

    /** bitLen(val) is the number of bits in val. */
    private static int bitLen(int w) {
        // Binary search - decision tree (5 tests, rarely 6)
        return (w < 1 << 15
                ? (w < 1 << 7
                ? (w < 1 << 3
                ? (w < 1 << 1
                ? (w < 1      ? (w < 0 ? 32 : 0) : 1)
                : (w < 1 << 2 ? 2 : 3))
                : (w < 1 << 5
                ? (w < 1 << 4 ? 4 : 5)
                : (w < 1 << 6 ? 6 : 7)))
                : (w < 1 << 11
                ? (w < 1 << 9 ? (w < 1 << 8 ? 8 : 9) : (w < 1 << 10 ? 10 : 11))
                : (w < 1 << 13 ? (w < 1 << 12 ? 12 : 13) : (w < 1 << 14 ? 14 : 15))))
                : (w < 1 << 23
                ? (w < 1 << 19
                ? (w < 1 << 17 ? (w < 1 << 16 ? 16 : 17) : (w < 1 << 18 ? 18 : 19))
                : (w < 1 << 21 ? (w < 1 << 20 ? 20 : 21) : (w < 1 << 22 ? 22 : 23)))
                : (w < 1 << 27
                ? (w < 1 << 25 ? (w < 1 << 24 ? 24 : 25) : (w < 1 << 26 ? 26 : 27))
                : (w < 1 << 29 ? (w < 1 << 28 ? 28 : 29) : (w < 1 << 30 ? 30 : 31)))));
    }
    private int bitLength(int idx, int[] mag) {
        int bitLength;
        if (mag.length == 0) return 0;

        while (idx != mag.length && mag[idx] == 0) idx++;

        if (idx == mag.length) return 0;

        // bit length for everything after the first int
        bitLength = 32 * ((mag.length - idx) - 1);

        // and determine bit length of first int
        bitLength += bitLen(mag[idx]);

        if (_sign < 0) {
            // Check if magnitude is a power of two
            boolean pow2 = ((bitCounts[mag[idx] & 0xff])
                    + (bitCounts[(mag[idx] >> 8) & 0xff])
                    + (bitCounts[(mag[idx] >> 16) & 0xff]) + (bitCounts[(mag[idx] >> 24) & 0xff])) == 1;

            for (int i = idx + 1; i < mag.length && pow2; i++) pow2 = (mag[i] == 0);
            bitLength -= (pow2 ? 1 : 0);
        }

        return bitLength;
    }

    /** return number of bits needed to express this integer */
    public int bitLength() {
        if (isNaN()) return 0;
        if (_nBitLength == -1) {
            if (_sign == 0) {
                _nBitLength = 0;
            } else {
                _nBitLength = bitLength(0, _magnitude);
            }
        }

        return _nBitLength;
    }

    /**
     * unsigned comparison on two arrays - note the arrays may
     * start with leading zeros.
     */
    private int compareTo(int xIdx, int[] x, int yIdx, int[] y) {
        while (xIdx != x.length && x[xIdx] == 0) xIdx++;
        while (yIdx != y.length && y[yIdx] == 0) yIdx++;

        if ((x.length - xIdx) < (y.length - yIdx)) return -1;
        if ((x.length - xIdx) > (y.length - yIdx)) return 1;

        // lengths of magnitudes the same, test the magnitude values
        while (xIdx < x.length) {
            long v1 = x[xIdx++] & IntMask;
            long v2 = y[yIdx++] & IntMask;
            if (v1 < v2) return -1;
            if (v1 > v2) return 1;
        }

        return 0;
    }

    /** Return 0 if ints are equal; <p>
     * -1 if 'val' is greater than 'this'; </p>
     * 1 if 'val' is less
     * */
    public int compareTo(LargeInt val) {
        if (isNaN() || val.isNaN()) return 0;
        if (_sign < val._sign) return -1;
        if (_sign > val._sign) return 1;

        int mag = compareTo(0, _magnitude, 0, val._magnitude);
        if (_sign < 0) return -mag;
        return mag;
    }

    /**
     * return z = x / y - done in place (z value preserved, x contains the
     * remainder)
     */
    private int[] divide(int[] x, int[] y) {
        int xyCmp = compareTo(0, x, 0, y);
        int[] count;

        if (xyCmp > 0) {
            int[] c;
            int shift = bitLength(0, x) - bitLength(0, y);

            if (shift > 1) {
                c = shiftLeft(y, shift - 1);
                count = shiftLeft(ONE._magnitude, shift - 1);
                if (shift % 32 == 0) {
                    // Special case where the shift is the size of an int.
                    int[] countSpecial = new int[shift / 32 + 1];
                    System.arraycopy(count, 0, countSpecial, 1, countSpecial.length - 1);
                    countSpecial[0] = 0;
                    count = countSpecial;
                }
            } else {
                c = new int[x.length];
                count = new int[1];

                System.arraycopy(y, 0, c, c.length - y.length, y.length);
                count[0] = 1;
            }

            int[] iCount = new int[count.length];

            subtract(0, x, 0, c);
            System.arraycopy(count, 0, iCount, 0, count.length);

            int xStart = 0;
            int cStart = 0;
            int iCountStart = 0;

            for (;;) {
                int cmp = compareTo(xStart, x, cStart, c);

                while (cmp >= 0) {
                    subtract(xStart, x, cStart, c);
                    add(count, iCount);
                    cmp = compareTo(xStart, x, cStart, c);
                }

                xyCmp = compareTo(xStart, x, 0, y);

                if (xyCmp > 0) {
                    if (x[xStart] == 0) xStart++;

                    shift = bitLength(cStart, c) - bitLength(xStart, x);

                    if (shift == 0) {
                        shiftRightOne(cStart, c);
                        shiftRightOne(iCountStart, iCount);
                    } else {
                        shiftRight(cStart, c, shift);
                        shiftRight(iCountStart, iCount, shift);
                    }

                    if (c[cStart] == 0) cStart++;
                    if (iCount[iCountStart] == 0) iCountStart++;

                } else if (xyCmp == 0) {
                    add(count, ONE._magnitude);
                    for (int i = xStart; i != x.length; i++) {
                        x[i] = 0;
                    }

                    break;
                } else break;
            }
        } else if (xyCmp == 0) {
            count = new int[1];
            count[0] = 1;
            zero(x); // no remainder if dividing by self
        } else {
            count = new int[1];
            // remainder is entire value if dividing by a larger number
        }

        return count;
    }

    /** Divide returning quotient and discarding remainder. If dividing by zero, a value of LARGE_NAN is returned */
    public LargeInt divide(LargeInt val) {
        if (isNaN() || val.isNaN()) return LARGE_NAN;
        if (val._sign == 0) return LARGE_NAN;
        if (_sign == 0) return ZERO;

        if (val.compareTo(ONE) == 0) return this;

        int[] mag = new int[_magnitude.length];
        System.arraycopy(_magnitude, 0, mag, 0, mag.length);

        return new LargeInt(_sign * val._sign, divide(mag, val._magnitude));
    }

    /** Divide returning both quotient [0] and remainder [1]. If dividing by zero, an empty array is returned */
    public LargeInt[] divideAndRemainder(LargeInt val) {
        if (val._sign == 0) return new LargeInt[0];

        LargeInt[] biggies = new LargeInt[2];

        if (_sign == 0) {
            biggies[0] = biggies[1] = ZERO;
            return biggies;
        }

        if (val.compareTo(ONE) == 0) {
            biggies[0] = this;
            biggies[1] = ZERO;

            return biggies;
        }

        int[] remainder = new int[_magnitude.length];
        System.arraycopy(_magnitude, 0, remainder, 0, remainder.length);

        int[] quotient = divide(remainder, val._magnitude);

        biggies[0] = new LargeInt(_sign * val._sign, quotient);
        biggies[1] = new LargeInt(_sign, remainder);

        return biggies;
    }

    /** Return true if both ints have exactly the same value */
    public boolean equals(LargeInt other){
        return compareTo(other) == 0;
    }

    /** return the factorial of this int */
    public LargeInt factorial(){
        if (isNaN()) return LARGE_NAN;
        if (_sign == 0) return ONE;
        if (_sign < 0) return this.abs().factorial();

        LargeInt accum = ONE;
        LargeInt count = this;
        while (count.compareTo(ONE) == 1){
            accum = accum.multiply(count);
            count = count.subtract(ONE);
        }
        return accum;
    }

    /** return the bit position of the lowest bit value set to 1 */
    public int getLowestSetBit() {
        if (isNaN()) return -1;
        if (equals(ZERO)) return -1;
        int w = _magnitude.length - 1;

        while (w >= 0) {
            if (_magnitude[w] != 0) break;
            w--;
        }

        int b = 31;

        while (b > 0) {
            if (_magnitude[w] << b == 0x80000000) break;
            b--;
        }

        return (((_magnitude.length - 1) - w) * 32 + (31 - b));
    }

    /** Find the greatest common denominator of two values */
    public LargeInt gcd(LargeInt val) {
        if (isNaN() || val.isNaN()) return LARGE_NAN;
        if (val._sign == 0) return abs();
        if (_sign == 0) return val.abs();

        LargeInt r;
        LargeInt u = this.abs();
        LargeInt v = val.abs();

        while (v._sign != 0) {
            r = u.mod(v);
            u = v;
            v = r;
        }

        return u;
    }

    /** Return this integer truncated to 64 bits */
    public long longValue() {
        long val;

        if (_magnitude.length == 0) return 0;

        int _1 = _magnitude.length - 1;
        int _2 = _magnitude.length - 2;

        if (_magnitude.length > 1) {
            val = ((long)_magnitude[_2] << 32) | (_magnitude[_1] & IntMask);
        } else {
            val = (_magnitude[_1] & IntMask);
        }

        if (_sign < 0) return -val;
        return val;
    }

    /** return the larger of two values */
    public LargeInt max(LargeInt val) {
        return (compareTo(val) > 0) ? this : val;
    }

    /** return the smaller of two values */
    public LargeInt min(LargeInt val) {
        return (compareTo(val) < 0) ? this : val;
    }

    /** return this % m */
    public LargeInt mod(LargeInt m) {
        if (isNaN() || m.isNaN()) return LARGE_NAN;
        if (m._sign <= 0) return LARGE_NAN;

        LargeInt biggie = remainder(m);
        return (biggie._sign >= 0 ? biggie : biggie.add(m));
    }

    /**
     * Calculate the numbers u1, u2, and u3 such that:
     *
     * u1 * a + u2 * b = u3
     *
     * where u3 is the greatest common divider of a and b.
     * a and b using the extended Euclid algorithm (refer p. 323
     * of The Art of Computer Programming vol 2, 2nd ed).
     * This also seems to have the side effect of calculating
     * some form of multiplicative inverse.
     *
     * @param a    First number to calculate gcd for
     * @param b    Second number to calculate gcd for
     * @param u1Out      the return object for the u1 value
     * @param u2Out      the return object for the u2 value
     * @return     The greatest common divisor of a and b
     */
    private static LargeInt extEuclid(LargeInt a, LargeInt b, LargeInt u1Out, LargeInt u2Out) {
        LargeInt res;

        LargeInt u1 = ONE;
        LargeInt u3 = a;
        LargeInt v1 = ZERO;
        LargeInt v3 = b;

        while (v3.compareTo(ZERO) > 0) {
            LargeInt q, tn;

            q = u3.divide(v3);

            tn = u1.subtract(v1.multiply(q));
            u1 = v1;
            v1 = tn;

            tn = u3.subtract(v3.multiply(q));
            u3 = v3;
            v3 = tn;
        }

        u1Out._sign = u1._sign;
        u1Out._magnitude = u1._magnitude;

        res = u3.subtract(u1.multiply(a)).divide(b);
        u2Out._sign = res._sign;
        u2Out._magnitude = res._magnitude;

        return u3;
    }

    /** Return modular multiplicative inverse of this and m. `m` must be positive
     * The result 'x' should conform to: (a*x)%m = 1 */
    public LargeInt modInverse(LargeInt m) {
        if (isNaN() || m.isNaN()) return LARGE_NAN;
        if (m._sign != 1) return LARGE_NAN;

        LargeInt x = new LargeInt();
        LargeInt y = new LargeInt();

        LargeInt gcd = extEuclid(this, m, x, y);

        if (!gcd.equals(ONE))
        {
            throw new ArithmeticException("Numbers not relatively prime.");
        }

        if (x.compareTo(ZERO) < 0)
        {
            x = x.add(m);
        }

        return x;
    }

    /**
     * zero out the array x
     */
    private void zero(int[] x) {Arrays.fill(x, 0);}

    /**
     * Montgomery multiplication: a = x * y * R^(-1) mod m
     *
     * Based algorithm 14.36 of Handbook of Applied Cryptography.
     *
     * <li> m, x, y should have length n </li>
     * <li> a should have length (n + 1) </li>
     * <li> b = 2^32, R = b^n </li>
     *
     * The result is put in x
     *
     * NOTE: the indices of x, y, m, a different in HAC and in Java
     */
    private void multiplyMonty(int[] a, int[] x, int[] y, int[] m, long mQuote) {
        int n = m.length;
        int nMinus1 = n - 1;
        long y_0 = y[n - 1] & IntMask;

        // 1. a = 0 (Notation: a = (a_{n} a_{n-1} ... a_{0})_{b} )
        for (int i = 0; i <= n; i++) a[i] = 0;

        // 2. for i from 0 to (n - 1) do the following:
        for (int i = n; i > 0; i--) {
            long x_i = x[i - 1] & IntMask;

            // 2.1 u = ((a[0] + (x[i] * y[0]) * mQuote) mod b
            long u = ((((a[n] & IntMask) + ((x_i * y_0) & IntMask)) & IntMask) * mQuote) & IntMask;

            // 2.2 a = (a + x_i * y + u * m) / b
            long prod1 = x_i * y_0;
            long prod2 = u * (m[n - 1] & IntMask);
            long tmp = (a[n] & IntMask) + (prod1 & IntMask) + (prod2 & IntMask);
            long carry = (long)(prod1 >>> 32) + (long)(prod2 >>> 32) + (long)(tmp >>> 32);
            for (int j = nMinus1; j > 0; j--) {
                prod1 = x_i * (y[j - 1] & IntMask);
                prod2 = u * (m[j - 1] & IntMask);
                tmp = (a[j] & IntMask) + (prod1 & IntMask) + (prod2 & IntMask) + (carry & IntMask);
                carry = (long)(carry >>> 32) + (long)(prod1 >>> 32) +
                        (long)(prod2 >>> 32) + (long)(tmp >>> 32);
                a[j + 1] = (int)tmp; // division by b
            }

            carry += (a[0] & IntMask);
            a[1] = (int)carry;
            a[0] = (int)(carry >>> 32);
        }

        // 3. if x >= m the x = x - m
        if (compareTo(0, a, 0, m) >= 0) {
            subtract(0, a, 0, m);
        }

        // put the result in x
        for (int i = 0; i < n; i++) x[i] = a[i + 1];
    }

    /** Calculate mQuote = -m^(-1) mod b with b = 2^32 (32 = word size) */
    private long getMQuote() {
        if (_mQuote != -1L) {// cached
            return _mQuote;
        }

        if ((_magnitude[_magnitude.length - 1] & 1) == 0) {
            return -1L; // not for even numbers
        }

        byte[] bytes = { 1, 0, 0, 0, 0 };
        LargeInt b = new LargeInt(1, bytes); // 2^32
        _mQuote = negate().mod(b).modInverse(b).longValue();
        return _mQuote;
    }

    /**
     * return w with w = x * x - w is assumed to have enough space.
     */
    private void square(int[] w, int[] x) {
        long u1, u2, c;

        if (w.length != 2 * x.length) { // invalid -- caller did not leave enough space
            zero(w); zero(x);
            return;
        }

        for (int i = x.length - 1; i != 0; i--) {
            long  v = (x[i] & IntMask);

            u1 = v * v;
            u2 = (long)(u1 >>> 32);
            u1 &= IntMask;

            u1 += (w[2 * i + 1] & IntMask);

            w[2 * i + 1] = (int)u1;
            c = u2 + (u1 >> 32);

            for (int j = i - 1; j >= 0; j--) {
                u1 = (x[j] & IntMask) * v;
                u2 = (long)(u1 >>> 31); // multiply by 2!
                u1 = (u1 & 0x7fffffff) << 1; // multiply by 2!
                u1 += (w[i + j + 1] & IntMask) + c;

                w[i + j + 1] = (int)u1;
                c = u2 + (long)(u1 >>> 32);
            }

            c += w[i] & IntMask;
            w[i] = (int)c;
            w[i - 1] = (int)(c >> 32);
        }

        u1 = (x[0] & IntMask);
        u1 *= u1;
        u2 = (long)(u1 >>> 32);
        u1 &= IntMask;

        u1 += (w[1] & IntMask);

        w[1] = (int)u1;
        w[0] = (int)(u2 + (u1 >> 32) + w[0]);
    }

    /**
     * return x with x = y * z - x is assumed to have enough space.
     */
    private int[] multiply(int[] x, int[] y, int[] z) {
        for (int i = z.length - 1; i >= 0; i--) {
            long a = z[i] & IntMask;
            long value = 0;

            for (int j = y.length - 1; j >= 0; j--) {
                value += a * (y[j] & IntMask) + (x[i + j + 1] & IntMask);

                x[i + j + 1] = (int)value;

                value = (long)(value >>> 32);
            }

            x[i] = (int)value;
        }

        return x;
    }

    /** return (this**exponent)%m */
    public LargeInt modPow(LargeInt exponent, LargeInt m) {
        if (isNaN() || m.isNaN() || exponent.isNaN()) return LARGE_NAN;
        int[] qVal = null; // 'Z' in most literature
        int[] accum = null; // 'Y' in most literature

        // Montgomery exponentiation is only possible if the modulus is odd,
        // but AFAIK, this is always the case for crypto algorithms
        boolean useMonty = ((m._magnitude[m._magnitude.length - 1] & 1) == 1);
        long mQ = 0;
        if (useMonty) {
            mQ = m.getMQuote();

            // tmp = this * R mod m
            LargeInt tmp = shiftLeft(32 * m._magnitude.length).mod(m);
            qVal = tmp._magnitude;

            useMonty = (qVal.length == m._magnitude.length);

            if (useMonty) {
                accum = new int[m._magnitude.length + 1];
            }
        }

        if (!useMonty) {
            if (_magnitude.length <= m._magnitude.length) {
                qVal = new int[m._magnitude.length];

                System.arraycopy(_magnitude, 0, qVal, qVal.length - _magnitude.length, _magnitude.length);
            } else {
                // in normal practice we'll never see this...
                LargeInt tmp = remainder(m);
                qVal = new int[m._magnitude.length];
                System.arraycopy(tmp._magnitude, 0, qVal, qVal.length - tmp._magnitude.length, tmp._magnitude.length);
            }

            accum = new int[m._magnitude.length * 2];
        }

        int[] rVal = new int[m._magnitude.length];

        // from LSW to MSW
        for (int i = 0; i < exponent._magnitude.length; i++) {
            int v = exponent._magnitude[i];
            int bits = 0;

            if (i == 0) {
                while (v > 0) {
                    v <<= 1;
                    bits++;
                }
                // first time in initialise y
                System.arraycopy(qVal, 0, rVal, 0, qVal.length);

                v <<= 1;
                bits++;
            }

            while (v != 0) {
                if (useMonty) {
                    // Montgomery square algo doesn't exist, and a normal
                    // square followed by a Montgomery reduction proved to
                    // be almost as heavy as a Montgomery multiply.
                    multiplyMonty(accum, rVal, rVal, m._magnitude, mQ);
                } else {
                    square(accum, rVal);
                    remainder(accum, m._magnitude);
                    System.arraycopy(accum, accum.length - rVal.length, rVal, 0, rVal.length);
                    zero(accum);
                }

                bits++;

                if (v < 0) {
                    if (useMonty) {
                        multiplyMonty(accum, rVal, qVal, m._magnitude, mQ);
                    } else {
                        multiply(accum, rVal, qVal);
                        remainder(accum, m._magnitude);
                        System.arraycopy(accum, accum.length - rVal.length, rVal, 0, rVal.length);
                        zero(accum);
                    }
                }

                v <<= 1;
            }

            while (bits < 32) {
                if (useMonty) {
                    multiplyMonty(accum, rVal, rVal, m._magnitude, mQ);
                } else {
                    square(accum, rVal);
                    remainder(accum, m._magnitude);
                    System.arraycopy(accum, accum.length - rVal.length, rVal, 0, rVal.length);
                    zero(accum);
                }

                bits++;
            }
        }

        if (useMonty) {
            // Return y * R^(-1) mod m by doing y * 1 * R^(-1) mod m
            zero(qVal);
            qVal[qVal.length - 1] = 1;
            multiplyMonty(accum, rVal, qVal, m._magnitude, mQ);
        }

        return new LargeInt(1, rVal);
    }

    /** return this * val */
    public LargeInt multiply(LargeInt val) {
        if (isNaN() || val.isNaN()) return LARGE_NAN;
        if (_sign == 0 || val._sign == 0) return ZERO;

        int[] res = new int[_magnitude.length + val._magnitude.length];

        return new LargeInt(_sign * val._sign, multiply(res, _magnitude, val._magnitude));
    }

    /** return this * val */
    public LargeInt multiply(int val) {
        if (isNaN()) return LARGE_NAN;
        if (val == 0) return ZERO;
        if (val == 1) return this;
        if (val == -1) return this.negate();
        return multiply(LargeInt.fromInt(val));
    }

    /** reverse sign of value */
    public LargeInt negate() {
        if (isNaN()) return LARGE_NAN;
        return new LargeInt(-_sign, _magnitude);
    }

    /** return this**exp */
    public LargeInt pow(int exp) {
        if (isNaN()) return LARGE_NAN;
        if (exp < 0) return ZERO;
        if (_sign == 0) return (exp == 0 ? ONE : this);

        LargeInt y, z;
        y = ONE;
        z = this;

        while (exp != 0) {
            if ((exp & 0x1) == 1) {
                y = y.multiply(z);
            }

            exp >>= 1;
            if (exp != 0) {
                z = z.multiply(z);
            }
        }

        return y;
    }

    /**
     * return x = x % y - done in place (y value preserved)
     */
    private int[] remainder(int[] x, int[] y) {
        int xyCmp = compareTo(0, x, 0, y);

        if (xyCmp > 0) {
            int[] c;
            int shift = bitLength(0, x) - bitLength(0, y);

            if (shift > 1) {
                c = shiftLeft(y, shift - 1);
            } else {
                c = new int[x.length];

                System.arraycopy(y, 0, c, c.length - y.length, y.length);
            }

            subtract(0, x, 0, c);

            int xStart = 0;
            int cStart = 0;

            for (;;) {
                int cmp = compareTo(xStart, x, cStart, c);

                while (cmp >= 0) {
                    subtract(xStart, x, cStart, c);
                    cmp = compareTo(xStart, x, cStart, c);
                }

                xyCmp = compareTo(xStart, x, 0, y);

                if (xyCmp > 0) {
                    if (x[xStart] == 0) xStart++;

                    shift = bitLength(cStart, c) - bitLength(xStart, x);

                    if (shift == 0) {
                        shiftRightOne(cStart, c);
                    } else {
                        shiftRight(cStart, c, shift);
                    }

                    if (c[cStart] == 0) cStart++;

                } else if (xyCmp == 0) {
                    for (int i = xStart; i != x.length; i++) x[i] = 0;
                    break;
                }
                else break;
            }
        } else if (xyCmp == 0) {
            zero(x);
        }

        return x;
    }

    /** return the remainder of division by val */
    public LargeInt remainder(LargeInt val) {
        if (isNaN() || val.isNaN()) return LARGE_NAN;
        if (val._sign == 0) return LARGE_NAN;
        if (_sign == 0) return ZERO;

        int[] res = new int[_magnitude.length];

        System.arraycopy(_magnitude, 0, res, 0, res.length);
        return new LargeInt(_sign, remainder(res, val._magnitude));
    }

    /**
     * do a left shift - this returns a new array.
     */
    private int[] shiftLeft(int[] mag, int n) {
        int nInts = (int)(n >>> 5);
        int nBits = n & 0x1f;
        int magLen = mag.length;
        int[] newMag;

        if (nBits == 0) {
            newMag = new int[magLen + nInts];
            for (int i = 0; i < magLen; i++) {
                newMag[i] = mag[i];
            }
        } else {
            int i = 0;
            int nBits2 = 32 - nBits;
            int highBits = (int)(mag[0] >>> nBits2);

            if (highBits != 0) {
                newMag = new int[magLen + nInts + 1];
                newMag[i++] = highBits;
            } else {
                newMag = new int[magLen + nInts];
            }

            int m = mag[0];
            for (int j = 0; j < magLen - 1; j++) {
                int next = mag[j + 1];

                newMag[i++] = (m << nBits) | (int)(next >>> nBits2);
                m = next;
            }

            newMag[i] = mag[magLen - 1] << nBits;
        }

        return newMag;
    }

    /** return this << n */
    public LargeInt shiftLeft(int n) {
        if (isNaN()) return LARGE_NAN;
        if (_sign == 0 || _magnitude.length == 0) return ZERO;
        if (n == 0) return this;
        if (n < 0) return shiftRight(-n);
        return new LargeInt(_sign, shiftLeft(_magnitude, n));
    }

    /**
     * do a right shift - this does it in place.
     */
    private int[] shiftRight(int start, int[] mag, int n) {
        int nInts = (int)(n >>> 5) + start;
        int nBits = n & 0x1f;
        int magLen = mag.length;

        if (nInts != start) {
            int delta = (nInts - start);

            for (int i = magLen - 1; i >= nInts; i--) {
                mag[i] = mag[i - delta];
            }

            for (int i = nInts - 1; i >= start; i--) {
                mag[i] = 0;
            }
        }

        if (nBits != 0) {
            int nBits2 = 32 - nBits;
            int m = mag[magLen - 1];

            for (int i = magLen - 1; i >= nInts + 1; i--) {
                int next = mag[i - 1];

                mag[i] = (int)(m >>> nBits) | (next << nBits2);
                m = next;
            }

            mag[nInts] = (int)(mag[nInts] >>> nBits);
        }

        return mag;
    }

    /**
     * do a right shift by one - this does it in place.
     */
    private void shiftRightOne(int start, int[] mag) {
        int magLen = mag.length;
        int m = mag[magLen - 1];

        for (int i = magLen - 1; i >= start + 1; i--) {
            int next = mag[i - 1];

            mag[i] = ((int)(m >>> 1)) | (next << 31);
            m = next;
        }

        mag[start] = (int)(mag[start] >>> 1);
    }

    /** return this >> n */
    public LargeInt shiftRight(int n) {
        if (isNaN()) return LARGE_NAN;
        if (n == 0) return this;
        if (n < 0) return shiftLeft(-n);

        if (n >= bitLength()) {
            return (_sign < 0 ? valueOf(-1) : ZERO);
        }

        int[] res = new int[_magnitude.length];

        System.arraycopy(_magnitude, 0, res, 0, res.length);

        return new LargeInt(_sign, shiftRight(0, res, n));
    }

    /** Return sign. -1 is negative, 1 is positive, 0 is zero-value */
    public int sign() {return _sign;}

    /**
     * returns x = x - y - we assume x is >= y
     */
    private int[] subtract(int xStart, int[] x, int yStart, int[] y) {
        int iT = x.length - 1;
        int iV = y.length - 1;
        long m;
        int borrow = 0;

        do {
            m = (x[iT] & IntMask) - (y[iV--] & IntMask) + borrow;

            x[iT--] = (int)m;

            if (m < 0) {
                borrow = -1;
            } else {
                borrow = 0;
            }
        } while (iV >= yStart);

        while (iT >= xStart) {
            m = (x[iT] & IntMask) + borrow;
            x[iT--] = (int)m;

            if (m >= 0) break;
        }

        return x;
    }

    /** return this - val */
    public LargeInt subtract(LargeInt val) {
        if (isNaN() || val.isNaN()) return LARGE_NAN;
        if (val._sign == 0 || val._magnitude.length == 0) return this;
        if (_sign == 0 || _magnitude.length == 0) return val.negate();

        if (val._sign < 0) {
            return add(val.negate());
        } else {
            if (_sign < 0) return add(val.negate());
        }

        LargeInt large, small;
        int compare = compareTo(val);
        if (compare == 0) return ZERO;

        if (compare < 0) {
            large = val;
            small = this;
        } else {
            large = this;
            small = val;
        }

        int[] res = new int[large._magnitude.length];

        System.arraycopy(large._magnitude, 0, res, 0, res.length);

        return new LargeInt(_sign * compare, subtract(0, res, 0, small._magnitude));
    }

    /** return true if the bit at offset 'n' is set to 1 */
    public boolean testBit(int n) {
        if (isNaN()) return false;
        if (n < 0) return false;

        if ((n / 32) >= _magnitude.length) {
            return _sign < 0;
        }

        return ((_magnitude[(_magnitude.length - 1) - n / 32] >> (n % 32)) & 1) > 0;
    }

    /** convert to a byte array for storage. Negative values are represented as 2's compliment */
    public byte[] toByteArray() {
        if (isNaN()) return new byte[0];
        int bitLength = this.bitLength();
        byte[] bytes = new byte[bitLength / 8 + 1];

        int bytesCopied = 4;
        int mag = 0;
        int ofs = _magnitude.length - 1;
        int carry = 1;
        long lMag;
        for (int i = bytes.length - 1; i >= 0; i--) {
            if (bytesCopied == 4 && ofs >= 0) {
                if (_sign < 0) {
                    // we are dealing with a +ve number and we want a -ve one, so
                    // invert the magnitude ints and add 1 (propagating the carry)
                    // to make a 2's complement -ve number
                    lMag = ~_magnitude[ofs--] & IntMask;
                    lMag += carry;
                    if ((lMag & ~IntMask) != 0) carry = 1;
                    else carry = 0;
                    mag = (int)(lMag & IntMask);
                } else {
                    mag = _magnitude[ofs--];
                }

                bytesCopied = 1;
            } else {
                mag = (int)(mag >>> 8);
                bytesCopied++;
            }

            bytes[i] = (byte)mag;
        }

        return bytes;
    }

    /** Output a string in 0e1 form with the given precision
     * This is useful for parsing into a floating point value. */
    public String toFloatString(int precision){
        if (isNaN()) return "";
        String full = this.toString();
        int sign = (_sign < 0) ? 1 : 0;

        if (precision < 1) precision = 8;
        if (full.length() <= precision) return full;
        precision += sign;

        int exponent = full.length() - precision;
        return full.substring(0, precision)+"e"+exponent;
    }

    /** convert to a storage format. Can be exactly recovered with 'fromStorage' */
    public byte[] toStorage(){
        if (isNaN() || !isValid()) return new byte[0];

        int bitLength = this.bitLength();
        byte[] bytes = new byte[bitLength / 8 + 2];

        int bytesCopied = 4;
        int mag = 0;
        int ofs = _magnitude.length - 1;
        for (int i = bytes.length - 1; i >= 1; i--) {
            if (bytesCopied == 4 && ofs >= 0) {
                mag = _magnitude[ofs--];
                bytesCopied = 1;
            } else {
                mag = (int)(mag >>> 8);
                bytesCopied++;
            }

            bytes[i] = (byte)mag;
        }

        bytes[0] = (byte)_sign;

        return bytes;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return toString(10);
    }

    /** output this int as a string at radix 'rdx' (must be 10 or 16) */
    public String toString(int rdx) {
        if (isNaN()) return "";
        if (_sign == 0) return "0";

        StringBuilder s = new StringBuilder();
        String h;

        if (rdx == 16) { // ints line up with characters
            for (int i = 0; i < _magnitude.length; i++) {
                h = "0000000" + Integer.toHexString(_magnitude[i]);
                h = h.substring(h.length() - 8);
                s.append(h);
            }
        } else {
            // This is algorithm 1a from chapter 4.4 in Semi-numerical Algorithms, slow but it works
            ObjVec S = new ObjVec();
            LargeInt bs = new LargeInt(Integer.toString(rdx));
            // The sign is handled separately.
            // Notice however that for this to work, radix 16 _MUST_ be a special case,
            LargeInt u = new LargeInt(abs().toString(16), 16);
            LargeInt b;

            // For speed, maye these test should look directly a u.magnitude.Length?
            while (!u.equals(ZERO)) {
                b = u.mod(bs);
                if (b.equals(ZERO)) S.addFirst("0");
                else {
                    // see how to interact with different bases
                    S.addFirst(Integer.toString(b._magnitude[0]));
                }

                u = u.divide(bs);
            }

            // Then pop the stack
            while (S.size() != 0) s.append(S.removeFirst());
        }

        // Strip leading zeros.
        while (s.toString().length() > 1 && s.toString().charAt(0) == '0')
            s = new StringBuilder(s.substring(1));

        if (s.toString().length() == 0)
            s = new StringBuilder("0");
        else if (_sign == -1)
            s.insert(0, "-");

        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (obj.getClass() != this.getClass()) return false;

        final LargeInt other = (LargeInt) obj;
        if (this._sign != other._sign || this._magnitude.length != other._magnitude.length) {
            return false;
        }

        for (int i = 0; i < _magnitude.length; i++) {
            if (_magnitude[i] != other._magnitude[i]) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = _sign * 0xAAAA5555;

        for (int i = 0; i < _magnitude.length; i++) {
            hash = ((hash << 8) | _magnitude[i]) ^ (_magnitude[i] << 4);
        }
        return hash;
    }

    private static void nextRndBytes(byte[] bytes) {RandomNumberGenerator.Fill(bytes);}

    /** Bit count of bytes 0..255 */
    private static final byte[] bitCounts = {
        0, 1, 1, 2, 1, 2, 2, 3,   1, 2, 2, 3, 2, 3, 3, 4,   1, 2, 2, 3, 2, 3, 3, 4,   2, 3, 3, 4, 3, 4, 4, 5,
        1, 2, 2, 3, 2, 3, 3, 4,   2, 3, 3, 4, 3, 4, 4, 5,   2, 3, 3, 4, 3, 4, 4, 5,   3, 4, 4, 5, 4, 5, 5, 6,
        1, 2, 2, 3, 2, 3, 3, 4,   2, 3, 3, 4, 3, 4, 4, 5,   2, 3, 3, 4, 3, 4, 4, 5,   3, 4, 4, 5, 4, 5, 5, 6,
        2, 3, 3, 4, 3, 4, 4, 5,   3, 4, 4, 5, 4, 5, 5, 6,   3, 4, 4, 5, 4, 5, 5, 6,   4, 5, 5, 6, 5, 6, 6, 7,
        1, 2, 2, 3, 2, 3, 3, 4,   2, 3, 3, 4, 3, 4, 4, 5,   2, 3, 3, 4, 3, 4, 4, 5,   3, 4, 4, 5, 4, 5, 5, 6,
        2, 3, 3, 4, 3, 4, 4, 5,   3, 4, 4, 5, 4, 5, 5, 6,   3, 4, 4, 5, 4, 5, 5, 6,   4, 5, 5, 6, 5, 6, 6, 7,
        2, 3, 3, 4, 3, 4, 4, 5,   3, 4, 4, 5, 4, 5, 5, 6,   3, 4, 4, 5, 4, 5, 5, 6,   4, 5, 5, 6, 5, 6, 6, 7,
        3, 4, 4, 5, 4, 5, 5, 6,   4, 5, 5, 6, 5, 6, 6, 7,   4, 5, 5, 6, 5, 6, 6, 7,   5, 6, 6, 7, 6, 7, 7, 8
    };
    private static final byte[] rndMask = { (byte)255, 127, 63, 31, 15, 7, 3, 1 };
    private static int[] makeMagnitude(byte[] bytes, int offset) {
        int i;
        int[] mag;
        int firstSignificant = offset;

        // strip leading zeros
        while (firstSignificant < bytes.length && bytes[firstSignificant] == 0){
            firstSignificant++;
        }

        if (firstSignificant >= bytes.length) return new int[0];
        int nInts = (bytes.length - firstSignificant + 3) / 4;
        int bCount = (bytes.length - firstSignificant) % 4;
        if (bCount == 0)
            bCount = 4;

        mag = new int[nInts];
        int v = 0;
        int magnitudeIndex = 0;
        for (i = firstSignificant; i < bytes.length; i++) {
            v <<= 8;
            v |= bytes[i] & 0xff;
            bCount--;
            if (bCount <= 0)
            {
                mag[magnitudeIndex] = v;
                magnitudeIndex++;
                bCount = 4;
                v = 0;
            }
        }

        if (magnitudeIndex < mag.length) {
            mag[magnitudeIndex] = v;
        }

        return mag;
    }

    /** Return true if this int has a NaN value */
    public boolean isNaN() {
        return _sign == -42 && _magnitude.length == 0;
    }

    /** Return true if this int has a valid value */
    public boolean isValid() {
        return (_sign >= -1 && _sign <= 1) && _magnitude != null;
    }

    public boolean isZero() {
        return _sign == 0 && _magnitude.length < 1;
    }
}
