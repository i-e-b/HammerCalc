package e.s.hammercalc.core;

import static java.lang.Double.NaN;

/**
 * Decimal mathematics class.
 * Ported from https://github.com/MikeMcl/decimal.js/
 * Licenced at https://github.com/MikeMcl/decimal.js/blob/master/LICENCE.md
 * <p>
 * Each decimal instance is immutable, and operations
 * return a new instance with the result values.
 */
public class Decimal {
    /**
     * Configuration for numbers and operations
     * Most of these values can be changed at run-time using `Decimal.Config`.
     * These values must be integers within the stated ranges (inclusive).
     */
    public static class Config {
        /**
         * The maximum number of significant digits of the result of a calculation or base conversion.
         * Default = 20
         * Range = 0..1e9 (Const.MAX_DIGITS)
         */
        public static double precision = 20;

        /**
         * The rounding mode used when rounding to `precision`
         * Default is ROUND_HALF_UP (Towards nearest neighbour. If equidistant, away from zero)
         */
        public static Rounding rounding = Rounding.ROUND_HALF_UP;
        /**
         * Limit below which `toString()` uses exponential notation
         * Default = -7
         * Range = -9e15..0 (Const.EXP_LIMIT)
         */
        public static double toExpNeg = -7;
        /**
         * Limit above which `toString()` uses exponential notation
         * Default = 21
         * Range = 0..9e15 (Const.EXP_LIMIT)
         */
        public static double toExpPos = 21;
        /**
         * The minimum exponent value, beneath which underflow to zero occurs.
         * Default = -9e15
         * Range = -9e15..-1 (Const.EXP_LIMIT)
         */
        public static double minE = -Const.EXP_LIMIT;
        /**
         * The maximum exponent value, above which overflow to Infinity occurs.
         * Default = 9e15
         * Range = 1..9e15 (Const.EXP_LIMIT)
         */
        public static double maxE = Const.EXP_LIMIT;
        /**
         * If true, a cryptographically-secure random number is used
         * Default = false
         */
        public static boolean crypto = false;
        /**
         * The modulo mode used when calculating the modulus: a mod n.
         * Default is ROUND_DOWN (JavaScript semantics)
         */
        public static Modulo modulo = Modulo.ROUND_DOWN;
    }

    /**
     * Rounding modes for Decimal.Config
     */
    public enum Rounding {
        /**
         * (0) Round away from zero
         */
        ROUND_UP,

        /**
         * (1) Round toward zero
         */
        ROUND_DOWN,

        /**
         * (2) Round toward positive infinity
         */
        ROUND_CEIL,

        /**
         * (3) Round toward negative infinity
         */
        ROUND_FLOOR,

        /**
         * (4) Round toward nearest neighbour. If equidistant, rounds away from zero
         */
        ROUND_HALF_UP,

        /**
         * (5) Round toward nearest neighbour. If equidistant, rounds toward zero
         */
        ROUND_HALF_DOWN,

        /**
         * (6) Rounds toward nearest neighbour. If equidistant rounds toward even neighbour.
         */
        ROUND_HALF_EVEN,

        /**
         * (7) Rounds toward nearest neighbour. If equidistant rounds toward positive infinity
         */
        ROUND_HALF_CEIL,

        /**
         * (8) Rounds toward nearest neighbour. If equidistant rounds toward negative infinity
         */
        ROUND_HALF_FLOOR;

        // Because Java is crap.
        public static int toInt(Rounding rm) {
            switch (rm) {
                case ROUND_UP:
                    return 0;
                case ROUND_DOWN:
                    return 1;
                case ROUND_CEIL:
                    return 2;
                case ROUND_FLOOR:
                    return 3;
                case ROUND_HALF_UP:
                    return 4;
                case ROUND_HALF_DOWN:
                    return 5;
                case ROUND_HALF_EVEN:
                    return 6;
                case ROUND_HALF_CEIL:
                    return 7;
                case ROUND_HALF_FLOOR:
                    return 8;
            }
            return 0;
        }
    }

    /**
     * Modulo behaviour types for Decimal.Config
     */
    public enum Modulo {
        /**
         * The remainder is positive if the dividend is negative, else is negative
         */
        ROUND_UP,

        /**
         * The remainder has the same sign as the dividend.
         * This uses truncating division and matches JavaScript's '%' operator
         */
        ROUND_DOWN,

        /**
         * The remainder has the same sign as the divisor.
         * This matches Python's '%' operator
         */
        ROUND_FLOOR,

        /**
         * The IEEE 754 remainder function
         */
        ROUND_HALF_EVEN,

        /**
         * The remainder is always positive.
         * Euclidean division: q = sign(x) * floor(a / abs(x)).
         */
        EUCLID
    }

    /**
     * Constants and limits on configuration
     */
    public static class Const {
        /**
         * The maximum exponent magnitude.
         * The limit on the value of `toExpNeg`, `toExpPos`, `minE` and `maxE`.
         * May be in the range 0..9e15
         */
        public static final double EXP_LIMIT = 9e15;

        /**
         * The limit on the value of `precision`,
         * and limit on the value of the first argument to `toDecimalPlaces`, `toExponential`, `toFixed`, `toPrecision` and `toSignificantDigits`
         */
        public static final double MAX_DIGITS = 1e9;

        /**
         * Base conversion alphabet
         */
        public static final char[] NUMERALS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        /**
         * The natural logarithm of 10 to 1025 digits
         */
        public static final String LN10 = "2.3025850929940456840179914546843642076011014886287729760333279009675726096773524802359972050895982983419677840422862486334095254650828067566662873690987816894829072083255546808437998948262331985283935053089653777326288461633662222876982198867465436674744042432743651550489343149393914796194044002221051017141748003688084012647080685567743216228355220114804663715659121373450747856947683463616792101806445070648000277502684916746550586856935673420670581136429224554405758925724208241314695689016758940256776311356919292033376587141660230105703089634572075440370847469940168269282808481184289314848524948644871927809676271275775397027668605952496716674183485704422507197965004714951050492214776567636938662976979522110718264549734772662425709429322582798502585509785265383207606726317164309505995087807523710333101197857547331541421808427543863591778117054309827482385045648019095610299291824318237525357709750539565187697510374970888692180205189339507238539205144634197265287286965110862571492198849978748873771345686209167058";
        public static double LN10_PRECISION = LN10.length() - 1;

        /**
         * Value of π to 1025 digits
         */
        public static final String PI = "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811174502841027019385211055596446229489549303819644288109756659334461284756482337867831652712019091456485669234603486104543266482133936072602491412737245870066063155881748815209209628292540917153643678925903600113305305488204665213841469519415116094330572703657595919530921861173819326117931051185480744623799627495673518857527248912279381830119491298336733624406566430860213949463952247371907021798609437027705392171762931767523846748184676694051320005681271452635608277857713427577896091736371787214684409012249534301465495853710507922796892589235420199561121290219608640344181598136297747713099605187072113499999983729780499510597317328160963185950244594553469083026425223082533446850352619311881710100031378387528865875332083814206171776691473035982534904287554687311595628638823537875937519577818577805321712268066130019278766111959092164201989380952572010654858632789";
        public static double PI_PRECISION = PI.length() - 1;

        /**
         * Base for digit elements
         */
        public static final double BASE = 1e7;

        /**
         * Log10 of BASE
         */
        public static final double LOG_BASE = 7;

        /**
         * Maximum value of a double before epsilon exceeds 1.0
         */
        public static final double MAX_SAFE_INTEGER = 9007199254740991d;
    }

    /**
     * Digits: Array of integer valued doubles 0..10000000
     */
    private DdVec d;

    /**
     * Exponent. Integer-like, -9e15..9e15 inclusive, or NaN
     */
    private double e;

    /**
     * Sign. Integer-like, -1, 1, or NaN
     */
    private double s;

    /**
     * True if value might need normalising
     */
    private boolean external = true;

    /**
     * Create a new decimal from a double value.
     * Note that precision loss in the source double may make
     * the decimal value different to literals in code.
     * Use String constructor for exact values.
     */
    public Decimal(double d) {
        // IEB: TODO!
    }

    /**
     * Create a new decimal by parsing a string value
     */
    public Decimal(String s) {
        // IEB: TODO!
    }

    /**
     * Create a decimal value as a copy of another
     */
    public Decimal(Decimal other) {
        d = new DdVec(other.d.toArray());
        e = other.e;
        s = other.s;
        external = other.external;
    }

    /**
     * Returns false if the value is infinite or NaN
     */
    public boolean isFinite() {
        return d != null && d.size() > 0 && Double.isFinite(e) && Double.isFinite(s);
    }

    /** Return true if the value is zero, but false for non-zero, infinite, or NaN */
    public boolean isZero(){
        return d != null && d.size() == 1 && d.get(0) == 0 && e == 0;
    }

    /** Return absolute value */
    public Decimal abs(){
        Decimal x = new Decimal(this);
        if (x.s < 0) x.s = 1;
        return finalise(x, NaN, Config.rounding, false);
    }

    /** Return a new Decimal whose value is the value of this Decimal rounded to a whole number in the direction of positive Infinity. */
    public Decimal ceil(){
        return finalise(new Decimal(this), this.e + 1, Rounding.ROUND_CEIL, false);
    }

    /** Return a new Decimal whose value is the value of this Decimal clamped to the range min..max */
    public Decimal clamp(Decimal min, Decimal max){
        if (min.isNaN() || max.isNaN()) return Decimal.NaN();
        if (min.gt(max)) return Decimal.NaN();
        int k = this.cmp(min);
        if (k < 0) return min;
        k = this.cmp(max);
        if (k > 0) return max;
        return this;
    }

    /* IEB:TEMP */ public boolean gt(Decimal other){
        return false;
    }

    public int cmp(Decimal other){ // decimal.mjs#L242
        // IEB: CONTINUE HERE
        if (!this.isFinite() || !other.isFinite()){
            if (this.isNaN() || other.isNaN()) return 0;
            if (this.s != other.s) return (int)this.s;
        }

        // IEB: TEMP
        return 0;
    }

    /** Return a new decimal with invalid value */
    public static Decimal NaN() {return new Decimal(NaN);}

    /** Return true if decimal is invalid, false otherwise */
    public boolean isNaN() {return d == null || d.size() < 1 || Double.isNaN(s);}

    /**
     * Round `x` to `sd` significant digits using rounding mode `rm`. Check for over/under-flow.
     */
    private Decimal finalise(Decimal x, double sd, Rounding rm, boolean isTruncated) {
        // rd: the rounding digit, i.e. the digit after the digit that may be rounded up.
        // w: the word of xd containing rd, a base 1e7 number.
        // xdi: the index of w within xd.
        // digits: the number of digits of w.
        // i: what would be the index of rd within w if all the numbers were 7 digits long (i.e. if they had leading zeros)
        // j: if > 0, the actual index of rd within w (if < 0, rd is a leading zero).

        long i, j, k;
        long digits;
        long sdl = (long) sd;
        int xdi;
        long rd;
        double w;
        boolean roundUp;

        out:
        if (!Double.isNaN(sd) && Double.isFinite(sd)) {
            DdVec xd = x.d;
            if (!x.isFinite()) return x; // Infinity / NaN

            // Get the length of the first word of the digits array xd.
            for (digits = 1, k = (long) xd.get(0); k >= 10; k /= 10) digits++;
            i = sdl - digits;
            if (i < 0) {
                i += Const.LOG_BASE;
                j = sdl;
                xdi = 0;
                w = xd.get(xdi);

                // Get the rounding digit at index j of w.
                rd = (long) (w / Math.pow(10, digits - j - 1) % 10);
            } else { // decimal.mjs#L2973
                xdi = (int) Math.ceil((i + 1) / Const.LOG_BASE);
                k = xd.length();
                if (xdi >= k) {
                    if (isTruncated) {
                        // Needed by `naturalExponential`, `naturalLogarithm` and `squareRoot`.
                        while (k++ <= xdi) {
                            xd.insert(0);
                        }
                        w = rd = 0;
                        digits = 1;
                        i %= Const.LOG_BASE;
                        j = (long) (i - Const.LOG_BASE + 1);
                    } else { // not truncated
                        break out;
                    }
                } else { // xdi < k
                    w = xd.get(xdi);
                    k = (long) w;

                    // Get the number of digits of w.
                    for (digits = 1; k >= 10; k /= 10) digits++;

                    // Get the index of rd within w.
                    i %= (int) Const.LOG_BASE;

                    // Get the index of rd within w, adjusted for leading zeros.
                    // The number of leading zeros of w is given by LOG_BASE - digits.
                    j = i - (long) Const.LOG_BASE + digits;

                    // Get the rounding digit at index j of w.
                    rd = j < 0 ? 0 : (long) (w / Math.pow(10, digits - j - 1) % 10);
                } // if (xdi >= k) {} else {}
            } // if (i < 0) {} else {}

            // decimal.mjs#L3006
            // Are there any non-zero digits after the rounding digit?
            isTruncated = isTruncated || sd < 0 || xd.hasIndex(xdi + 1) || (j < 0 ? w : w % Math.pow(10, digits - j - 1)) != 0;
            // The expression `w % Math.pow(10, digits - j - 1)` returns all the digits of w to the right
            // of the digit at (left-to-right) index j, e.g. if w is 908714 and j is 2, the expression
            // will give 714.

            int rmi = Rounding.toInt(rm);
            roundUp = rmi < 4
                    ? (rd != 0 || isTruncated) && (rmi == 0 || rmi == (x.s < 0 ? 3 : 2))
                    : rd > 5 || rd == 5 && (rmi == 4 || isTruncated || rmi == 6 &&

                    // Check whether the digit to the left of the rounding digit is odd.
                    ((i > 0 ? (j > 0 ? ((w / Math.pow(10, digits - j)) != 0) : false) : (((long) (xd.get(xdi - 1)) % 10) & 1) != 0) ||
                            rmi == (x.s < 0 ? 8 : 7)));

            // No significant digits?
            if (sd < 1 || xd.get(0) == 0) {
                xd.clear();
                if (roundUp) {
                    // Convert sd to decimal places.
                    sd -= x.e + 1;

                    // 1, 0.1, 0.01, 0.001, 0.0001 etc.
                    xd.insert(Math.pow(10, (Const.LOG_BASE - sd % Const.LOG_BASE) % Const.LOG_BASE));
                    x.e = -sd;
                } else {
                    // Zero.
                    xd.insert(0);
                    x.e = 0;
                }

                return x;
            }

            // Remove excess digits. //decimal.mjs#L3041
            if (i == 0) {
                xd.truncateTo(xdi);
                k = 1;
                xdi--;
            } else {
                xd.truncateTo(xdi + 1);
                k = (long) Math.pow(10, Const.LOG_BASE - i);

                // E.g. 56700 becomes 56000 if 7 is the rounding digit.
                // j > 0 means i > number of leading zeros of w.
                xd.set(xdi, j > 0 ? (long) (w / Math.pow(10, digits - j) % Math.pow(10, j)) * k : 0);
            }

            if (roundUp) {
                for (; ; ) {
                    // Is the digit to be rounded up in the first word of xd?
                    if (xdi == 0) {

                        // i will be the length of xd[0] before k is added.
                        for (i = 1, j = (long) xd.get(0); j >= 10; j /= 10) i++;
                        j = (long) xd.get(0) + k;
                        xd.set(0, j);
                        for (k = 1; j >= 10; j /= 10) k++;

                        // if i != k the length has increased.
                        if (i != k) {
                            x.e++;
                            if (xd.get(0) == Const.BASE) xd.increment(0, 1.0);
                        }

                        break;
                    } else {
                        xd.increment(xdi, k);
                        if (xd.get(xdi) != Const.BASE) break;
                        xd.set(xdi--, 0);
                        k = 1;
                    }
                }
            }

            // Remove trailing zeros.
            for (i = xd.length(); xd.get((int) (--i)) == 0; ) xd.pop();
        } // out: if( !Double.isNaN(sd) && Double.isFinite(sd)){

        if (external) {
            if (x.e > Config.maxE) {// Overflow?
                // Infinity.
                x.d = null;
                x.e = NaN;
            } else if (x.e < Config.minE) {// Underflow?
                // Zero.
                x.e = 0;
                x.d = new DdVec(1);
                // Ctor.underflow = true;
            } // else Ctor.underflow = false;
        }

        return x;
    }
}
