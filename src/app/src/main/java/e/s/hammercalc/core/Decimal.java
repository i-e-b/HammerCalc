package e.s.hammercalc.core;

import static java.lang.Double.NaN;

import java.util.regex.Pattern;

/**
 * Decimal mathematics class.
 * <p>
 * Each decimal instance is immutable, and operations
 * return a new instance with the result values.
 * <ul><li>Ported from <a href="https://github.com/MikeMcl/decimal.js/">decimal.js</a></li>
 * <li><a href="https://github.com/MikeMcl/decimal.js/blob/master/LICENCE.md">Licenced under MIT</a></li>
 * <li><a href="https://mikemcl.github.io/decimal.js/">Original documentation</a></li></ul>
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
        public static int precision = 20;

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

        /**
         * Character used to mark decimal place.
         * Default is '{@code .}'
         */
        public static char decimalPlaceChar = '.';
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
        ROUND_HALF_FLOOR,

        /** The default value should be used */
        NOT_SPECIFIED;

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
     * Constants and limits on configuration. Code makes assumptions based on these. They should generally not be changed.
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
        /** convert a character to it's positional value. Undefined for invalid input */
        public static int NUMERALS(char c) {
            int i = c - '0';
            if (i < 10) return i;
            i = (c - 'A')+10;
            if (i < 16) return i;
            return (c - 'a')+10;
        }

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
     * Regular expressions used in parsing
     */
    public static class Regexes {
        public static final Pattern isBinary = Pattern.compile("^0b([01]+(\\.[01]*)?|\\.[01]+)(p[+-]?\\d+)?$", Pattern.CASE_INSENSITIVE);
        public static final Pattern isHex = Pattern.compile("^0x([0-9a-f]+(\\.[0-9a-f]*)?|\\.[0-9a-f]+)(p[+-]?\\d+)?$", Pattern.CASE_INSENSITIVE);
        public static final Pattern isOctal = Pattern.compile("^0o([0-7]+(\\.[0-7]*)?|\\.[0-7]+)(p[+-]?\\d+)?$", Pattern.CASE_INSENSITIVE);
        public static final Pattern isDecimal = Pattern.compile("^(\\d+(\\.\\d*)?|\\.\\d+)(e[+-]?\\d+)?$", Pattern.CASE_INSENSITIVE);

        public static boolean isBinary(String str) {
            return isBinary.matcher(str).find();
        }

        public static boolean isHex(String str) {
            return isHex.matcher(str).find();
        }

        public static boolean isOctal(String str) {
            return isOctal.matcher(str).find();
        }

        public static boolean isDecimal(String str) {
            return isDecimal.matcher(str).find();
        }
    }

    /**
     * String pulled apart to use for parsing
     */
    public static class NumericString {
        /**
         * position of decimal point, or -1 if none
         */
        public int decimalPosition;
        /**
         * sign of number (+1, -1)
         */
        public int sign;
        /**
         * base of the number. One of 2, 8, 10, 16
         */
        public int baseSize;
        /**
         * true if number passed basic tests
         */
        public boolean valid;
        /**
         * digits of the number, excluding prefixes and exponent
         */
        public String mantissa;
        /**
         * exponent of the number, if any
         */
        public String exponent;

        public NumericString(String str) {
            decimalPosition = -1;
            sign = 0;
            valid = false;
            mantissa = "";
            exponent = "";
            baseSize = 10;
            if (str == null || str.length() < 1) return;

            StringBuilder sb_mantissa = new StringBuilder();
            StringBuilder sb_exponent = new StringBuilder();
            StringBuilder sb = sb_mantissa;

            boolean inExp = false; // are we reading an exponent?
            valid = true;
            sign = 1; // assume positive unless we get a sign
            char[] charArray = str.toCharArray();
            for (char c : charArray) {
                switch (c) { // signs and markers
                    case '-':
                    case '−':
                    case '˗':
                    case '－': // negative marker
                    {
                        if (sb.length() == 0) { // only allow as first proper character
                            if (inExp) sb.append('-');
                            else sign = -1;
                        } else {
                            valid = false;
                            return;
                        }
                        break;
                    }
                    case '+':
                    case '˖':
                    case '＋': // positive marker
                    {
                        if (sb.length() == 0) { // only allow as first proper character
                            if (inExp) sb.append('+');
                            else sign = 1;
                        } else {
                            valid = false;
                            return;
                        }
                        break;
                    }
                    case 'p':
                    case 'P': // exponent marker for all bases
                    {
                        if (!inExp) { // only one exponent
                            sb = sb_exponent; // switch, but don't store the P mark
                            inExp = true;
                        } else {
                            valid = false;
                            return;
                        }
                        break;
                    }
                    case 'e':
                    case 'E': // exponent marker OR 14 in hex
                    {
                        if (baseSize == 16) { // hex
                            sb.append('E'); // normal character
                        } else if (!inExp) { // only one exponent
                            sb = sb_exponent; // switch, but don't store the E mark
                            inExp = true;
                        } else {
                            valid = false;
                            return;
                        }
                        break;
                    }
                    // DEC is default,
                    case 'x':
                    case 'X': // HEX base marker
                    case 'b':
                    case 'B': // BIN base marker
                    case 'o':
                    case 'O': // OCT base marker
                    {
                        if (!inExp && sb.length() == 1) {
                            // must be at start of mantissa, and be '0x...', '0b...', '0o...', or uppercase version
                            if (sb.charAt(0) != '0') {
                                valid = false;
                                return;
                            }
                            sb.setLength(0); // remove marker
                            switch (c) {
                                case 'x':
                                case 'X':
                                    baseSize = 16;
                                    break;
                                case 'o':
                                case 'O':
                                    baseSize = 8;
                                    break;
                                case 'b':
                                case 'B':
                                    baseSize = 2;
                                    break;
                            }
                            break;
                        }
                        // fall-through TO DEFAULT if not in marker position
                        // in C#, this would be `goto default;`
                    }
                    default: { // regular characters
                        if (c == Config.decimalPlaceChar) {
                            if (inExp) {
                                valid = false;
                                return;
                            } // no fractional 'E' form (these are not real exponents)
                            decimalPosition = sb.length();
                        } else {
                            switch (baseSize) {
                                case 16: { // integer only, 0..F
                                    if (c >= '0' && c <= '9') sb.append(c);
                                    else if (c >= 'a' && c <= 'f') sb.append(Character.toUpperCase(c));
                                    else if (c >= 'A' && c <= 'F') sb.append(c);
                                    else if (notSeparator(c)) {
                                        valid = false;
                                        return;
                                    }
                                    break;
                                }
                                case 10: { // integer or fractional, 0..9
                                    if (c >= '0' && c <= '9') sb.append(c);
                                    else if (notSeparator(c)) {
                                        valid = false;
                                        return;
                                    }
                                    break;
                                }
                                case 8: { // integer only, 0..7
                                    if (c >= '0' && c <= '7') sb.append(c);
                                    else if (notSeparator(c)) {
                                        valid = false;
                                        return;
                                    }
                                    break;
                                }
                                case 2: { // integer only, 0..1
                                    if (c == '0' || c == '1') sb.append(c);
                                    else if (notSeparator(c)) {
                                        valid = false;
                                        return;
                                    }
                                    break;
                                }
                                default:
                                    if (notSeparator(c)) {
                                        valid = false;
                                        return;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            mantissa = sb_mantissa.toString();
            exponent = sb_exponent.toString();
        }

        /**
         * characters that can be used to break up numbers, and will be ignored
         */
        private boolean notSeparator(char c) {
            switch (c) {
                case ' ':
                case '_':
                case '\'':
                case '`':
                case '.':
                case ',':
                    return Config.decimalPlaceChar == c; // only ignore it if it's not the decimal position character.

                default:
                    return true;
            }
        }
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
     * This flag is set 'true' if an operation loses precision
     */
    private boolean inexact = false;

    /**
     * True if value might need normalising
     */
    private static boolean external = true;

    /**
     * Create a new decimal with zero value
     */
    public Decimal() {
        s = 1;
        e = 0;
        d = DdVec.FromDouble(0);
    }

    /**
     * Create a new decimal from a double value.
     * Note that precision loss in the source double may make
     * the decimal value different to literals in code.
     * Use String constructor for exact values.
     */
    public Decimal(double v) { // L4321
        if (v == 0.0) {
            s = 1 / v < 0 ? -1 : 1; // ?
            e = 0;
            d = DdVec.FromDouble(0);
            return;
        }

        if (v < 0) {
            v = -v;
            s = -1;
        } else {
            s = 1;
        }

        // Fast path for small integers
        int i;
        if ((v == (int) v) && (v < 1e7)) { //L4337
            for (e = 0, i = (int) v; i >= 10; i /= 10) e++; // get log10

            if (external) {
                if (e > Config.maxE) {
                    e = NaN;
                    d = null;
                } else if (e < Config.minE) {
                    e = 0;
                    d = DdVec.FromDouble(0);
                } else {
                    d = DdVec.FromDouble(v);
                }
            } else {
                d = DdVec.FromDouble(v);
            }
            return;
        } else if (v * 0.0 != 0.0) { // Infinity or NaN
            if (Double.isNaN(v)) s = NaN;
            e = NaN;
            d = null;
            return;
        }

        // Slow path for floats
        parseDecimal(this, new NumericString(Double.toString(v)));
    }

    /**
     * Create a new decimal by parsing a string value
     */
    public Decimal(String str) {
        if (str == null || str.equals("")) { // null or empty strings result in NaN
            makeNaN();
            return;
        }
        if (str.equals("NaN")) {
            makeNaN();
            return;
        }
        if (str.equals("Infinity") || str.equals("+Infinity")) {
            s = 1;
            makeInfinite();
            return;
        }
        if (str.equals("-Infinity")) {
            s = -1;
            makeInfinite();
            return;
        }

        NumericString nstr = new NumericString(str);

        if (!nstr.valid) { // badly formed strings result in NaN
            makeNaN();
            return;
        }

        s = nstr.sign;

        if (nstr.baseSize == 10) {
            parseDecimal(this, nstr); // base10 that might be fractional, and might have an exponent
        } else {
            parseOther(this, nstr); // other base, that will be integer without exponent
        }
    }

    /**
     * Create a decimal value as a copy of another
     */
    public Decimal(Decimal v) { // L4293
        s = v.s;
        inexact = v.inexact;
        if (external) { // if either should be normalised
            if (v.d == null || v.e > Config.maxE) {
                // Infinity
                e = NaN;
                d = null;
            } else if (v.e < Config.minE) {
                // Underflow to zero
                e = 0;
                d = DdVec.FromDouble(0);
            } else {
                // in range
                e = v.e;
                d = new DdVec(v.d.toArray()); // duplicate
            }
        } else {
            e = v.e;
            d = new DdVec(v.d.toArray()); // duplicate
        }
    }


    /**
     * Return a new decimal with a not-a-number value
     */
    public static Decimal decimalNaN() {
        Decimal d = new Decimal();
        d.d = null;
        d.e = NaN;
        d.s = NaN;
        return d;
    }

    /**
     * Return a new decimal with a signed zero value
     */
    public static Decimal signedZero(int sign) {
        Decimal d = new Decimal();
        d.d = DdVec.FromDouble(0);
        d.e = 0;
        d.s = sign;
        return d;
    }

    /**
     * Return a new decimal with a signed infinite value
     */
    public static Decimal signedInfinity(int sign) {
        Decimal d = new Decimal();
        d.d = null;
        d.e = NaN;
        d.s = sign;
        return d;
    }

    /**
     * Returns false if the value is infinite or NaN
     */
    public boolean isFinite() {
        return d != null && d.size() > 0 && Double.isFinite(e) && Double.isFinite(s);
    }

    /**
     * Returns true if the value is +infinity or -infinity.
     */
    public boolean isInfinity() {
        return (d == null || d.length() < 1) && !Double.isNaN(s);
    }

    /**
     * Returns true if the value is +infinity
     */
    public boolean isPositiveInfinity() {
        return (d == null || d.length() < 1) && (s >= 1);
    }
    /**
     * Returns true if the value is -infinity
     */
    public boolean isNegativeInfinity() {
        return (d == null || d.length() < 1) && (s <= -1);
    }

    /**
     * Return true if the value is zero, but false for non-zero, infinite, or NaN
     */
    public boolean isZero() {
        return d != null && d.size() == 1 && d.get(0) == 0 && e == 0;
    }

    /** True if this decimal has been through an inexact function */
    public boolean precisionLost(){
        return inexact;
    }

    /**
     * Return absolute value
     */
    public Decimal abs() {
        Decimal x = new Decimal(this);
        if (x.s < 0) x.s = 1;
        return finalise(x, NaN, Config.rounding, false);
    }

    /**
     * Return a new Decimal whose value is the value of this Decimal rounded to a whole number in the direction of positive Infinity.
     */
    public Decimal ceil() {
        return finalise(new Decimal(this), this.e + 1, Rounding.ROUND_CEIL, false);
    }

    /**
     * Return a new Decimal whose value is the value of this Decimal clamped to the range min..max
     */
    public Decimal clamp(Decimal min, Decimal max) {
        if (min.isNaN() || max.isNaN()) return Decimal.NaN();
        if (min.gt(max)) return Decimal.NaN();
        int k = this.cmp(min);
        if (k < 0) return min;
        k = this.cmp(max);
        if (k > 0) return max;
        return this;
    }

    /**
     * Return a new Decimal whose value is the value of this Decimal divided by `y`, rounded to
     * significant digits Config.precision, using rounding mode Config.rounding.
     * <p></p>
     * Special cases:
     *  n / 0 = I; n / N = N; n / I = 0; 0 / n = 0; 0 / 0 = N; 0 / N = N; 0 / I = 0;
     *  N / n = N; N / 0 = N; N / N = N; N / I = N; I / n = I; I / 0 = I; I / N = N; I / I = N;
     *
     */
    public Decimal div(Decimal y) {
        return divide(this, new Decimal(y), Config.precision, Config.rounding, false, -1);
    }

    /**
     * Return a new Decimal whose value is the integer part of dividing the value of this Decimal
     * by the value of `y`, rounded to significant digits Config.precision, using rounding mode Config.rounding.
     */
    public Decimal divToInt (Decimal y){
        return finalise(divide(this, new Decimal(y), 0, Rounding.ROUND_DOWN, true, -1), Config.precision, Config.rounding, false);
    }

    /** Return a new Decimal whose value is this decimal raised to power y, rounded
     * to `precision` significant digits using rounding mode `rounding`.
     * <p>
     * The performance of this method degrades exponentially with increasing digits.
     * For non-integer exponents in particular, the performance of this method may not be adequate.</p>
     * <pre>
     * ECMAScript compliant.
     *
     *   pow(x, NaN)                           = NaN
     *   pow(x, ±0)                            = 1

     *   pow(NaN, non-zero)                    = NaN
     *   pow(abs(x) > 1, +Infinity)            = +Infinity
     *   pow(abs(x) > 1, -Infinity)            = +0
     *   pow(abs(x) == 1, ±Infinity)           = NaN
     *   pow(abs(x) < 1, +Infinity)            = +0
     *   pow(abs(x) < 1, -Infinity)            = +Infinity
     *   pow(+Infinity, y > 0)                 = +Infinity
     *   pow(+Infinity, y < 0)                 = +0
     *   pow(-Infinity, odd integer > 0)       = -Infinity
     *   pow(-Infinity, even integer > 0)      = +Infinity
     *   pow(-Infinity, odd integer < 0)       = -0
     *   pow(-Infinity, even integer < 0)      = +0
     *   pow(+0, y > 0)                        = +0
     *   pow(+0, y < 0)                        = +Infinity
     *   pow(-0, odd integer > 0)              = -0
     *   pow(-0, even integer > 0)             = +0
     *   pow(-0, odd integer < 0)              = -Infinity
     *   pow(-0, even integer < 0)             = +Infinity
     *   pow(finite x < 0, finite non-integer) = NaN
     *
     * For non-integer or very large exponents pow(x, y) is calculated using
     *
     *   x^y = exp(y*ln(x))
     *
     * Assuming the first 15 rounding digits are each equally likely to be any digit 0-9, the
     * probability of an incorrectly rounded result
     * P([49]9{14} | [50]0{14}) = 2 * 0.2 * 10^-14 = 4e-15 = 1/2.5e+14
     * i.e. 1 in 250,000,000,000,000
     *
     * If a result is incorrectly rounded the maximum error will be 1 ulp (unit in last place).
     *</pre>
     * @param y The power to which to raise this Decimal. */
    public Decimal pow(Decimal y){// L2264
        // Precondition checks
        if (this.isNaN() || y.isNaN()) return Decimal.decimalNaN();
        if (y.isPositiveInfinity()) return Decimal.signedInfinity(1);
        if (y.isNegativeInfinity()) return Decimal.signedZero((int)this.s);
        if (y.isZero()) return new Decimal(1);

        Decimal x = new Decimal(this);
        if (x.isInfinity()) return x;
        if (x.isZero()) return x;
        if (x.eq(1)) return x;

        double pr = Config.precision;
        Rounding rm = Config.rounding;

        if (y.eq(1)) return finalise(x, pr, rm, false);

        // y exponent
        double e = Math.floor(y.e / Const.LOG_BASE);

        double k;

        // If y is a small integer, use the 'exponentiation by squaring' algorithm.
        double yn = y.toDouble();
        if ((e >= y.d.length() - 1) && ((k = (long)(yn < 0 ? -yn : yn))) <= Const.MAX_SAFE_INTEGER) {
            Decimal r = intPow(x, (long)k, (int) pr);
            if (y.s < 0) return new Decimal(1).div(r);
            else return finalise(r, pr, rm, false);
        }
        // L2290

        double _s = x.s; // sign

        // if x is negative
        if (_s < 0){
            // if y is not an integer
            if (e < y.d.length() - 1) return Decimal.decimalNaN();

            // Result is positive if x is negative and the last digit of integer y is even.
            if (((int)y.d.get((int)e) & 1) == 0) _s = 1;

            // if x.eq(-1)
            if (x.e == 0 && x.d.get(0) == 1 && x.d.length() == 1) {
                x.s = _s;
                return x;
            }
        }

        // Estimate result exponent.
        // x^y = 10^e,  where e = y * log10(x)
        // log10(x) = log10(x_significand) + x_exponent
        // log10(x_significand) = ln(x_significand) / ln(10)
        k = Math.pow(x.toDouble(), yn);
        boolean finiteK = k == 0 || !Double.isFinite(k);
        if (finiteK){
            String basex = "0." + digitsToString(x.d);
            e = Math.floor(yn * (Math.log(Double.parseDouble(basex)) / Math.log(10) + x.e + 1));
        } else {
            e = new Decimal(Double.toString(k)).e;
        }

        // IEB: Continue here
        return Decimal.decimalNaN(); // delete later
    }

    private String digitsToString(DdVec d) {
        int i;
        long k;
        int indexOfLastWord = d.length() - 1;
        double w = d.get(0);
        String ws;
        StringBuilder str = new StringBuilder();

        if (indexOfLastWord > 0) {
            str.append((long)w);
            for (i = 1; i < indexOfLastWord; i++) {
                ws = Long.toString((long)d.get(i));
                k = (long)Const.LOG_BASE - ws.length();
                if (k != 0) str.append(getZeroString(k));
                str.append(ws);
            }

            w = d.get(i);
            ws = Long.toString((long)w);
            k = (long)Const.LOG_BASE - ws.length();
            if (k != 0) str.append(getZeroString(k));
        } else if (w == 0) {
            return "0";
        }

        // Remove trailing zeros of last w.
        while (w % 10 == 0) w /= 10;

        str.append(w);
        return str.toString();
    }

    private static String getZeroString(long k) {
        StringBuilder zs = new StringBuilder();
        while (k-->0) zs.append('0');
        return zs.toString();
    }

    /** Return this decimal converted to the nearest double value */
    private double toDouble() {
        try {
            return Double.parseDouble(toStringBinary(this, 10, -1, Config.rounding));
        } catch (Exception ex) {
            return NaN;
        }
    }

    /**
     * Return the value of Decimal `x` as a string in base `baseOut`.
     *
     * If the optional `sd` argument is present include a binary exponent suffix.
     */
    private static String toStringBinary(Decimal x, int baseOut, int sd, Rounding rm){
        // L3790
        // TODO
        return "TODO";
    }

    public boolean eq(double other) {
        // TODO
        return false;
    }

    public boolean eq(Decimal other) {
        // TODO
        return false;
    }

    /* IEB:TEMP */
    public boolean gt(Decimal other) {
        // TODO
        return false;
    }

    public int cmp(Decimal other) { // decimal.mjs#L242
        int xs = (int)this.s;
        int ys = (int)other.s;

        // Either NaN or ±Infinity?
        if (!this.isFinite() || !other.isFinite()) {
            if (this.isNaN() || other.isNaN()) return 0;
            if (xs != ys) return xs;
        }

        // Either zero?
        if (this.isZero() || other.isZero()){
            if (!this.isZero()) return xs;
            if (!other.isZero()) return -ys;
            return 0;
        }

        // Signs differ?
        if (xs != ys) return xs;

        // Compare exponents.
        if (this.e != other.e) return ((this.e > other.e) ^ (xs < 0)) ? 1 : -1;

        // Compare digit by digit.
        int xdL = this.d.length();
        int ydL = other.d.length();
        int i, j;
        for (i = 0, j = Math.min(xdL, ydL); i < j; ++i) {
            //if (this.d[i] !== other.d[i]) return this.d[i] > other.d[i] ^ xs < 0 ? 1 : -1;
            if (this.d.get(i) != other.d.get(i)) return ((this.d.get(i) > other.d.get(i)) ^ (xs < 0)) ? 1 : -1;
        }

        // Compare lengths.
        return xdL == ydL ? 0 : xdL > ydL ^ xs < 0 ? 1 : -1;
    }

    /**
     * Return a new decimal with invalid value
     */
    public static Decimal NaN() {
        return new Decimal(NaN);
    }

    /**
     * Return true if decimal is invalid, false otherwise
     */
    public boolean isNaN() {
        return (d == null || d.isEmpty()) && Double.isNaN(s);
    }

    /** Display the internal state of this Decimal */
    public String toRawString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Sign=");
        sb.append((int)s);

        sb.append("; Exponent=");
        if (Double.isFinite(e)) sb.append((long)e);
        else sb.append(e);

        if (d == null){
            sb.append("; Digits=<null>;");
        } else if (d.isEmpty()) {
            sb.append("; Digits=<empty>;");
        } else {
            sb.append("; Digits=");
            boolean s = false;
            double[] digits = d.toArray();
            for (double v : digits) {
                if (s) sb.append(',');
                else s = true;

                sb.append('[');
                sb.append((long)v);
                sb.append(']');
            }
        }

        return sb.toString();
    }

    /*-------------------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------------------*/


    /**
     * Modify this Decimal in place -- for use in constructors only.
     * Parse the input string as an unsigned decimal string.
     * Input string should have been through the filter process before calling
     */
    private static void parseDecimal(Decimal x, NumericString num) {
        // L3513
        // try to find decimal place
        int e = num.decimalPosition;

        // TODO: update NumericString to simplify this
        // TODO: do our own parsing so we can remove substring and string concatenation

        // Exponent form?
        if (num.exponent.length() > 0) {
            e += parseDoubleOrNaN(num.exponent);
        } else if (e < 0) {
            e = num.mantissa.length(); // integer
        }

        // Determine leading zeros
        int i;
        for (i = 0; i < num.mantissa.length() && num.mantissa.charAt(i) == '0'; i++) ;

        // Determine trailing zeros
        int len;
        for (len = num.mantissa.length(); len > 0 && num.mantissa.charAt(len - 1) == '0'; --len) ;

        if (len <= i) { // all zeros
            x.e = 0;
            x.d = DdVec.FromDouble(0);
        } else { // L3540
            String str = num.mantissa.substring(i, len);
            x.d = new DdVec();
            len -= i;
            e = e - i - 1;
            x.e = e;

            // Transform base

            // e is the base10 exponent
            // i is where to slice str to get the first word of the digits array
            i = (e + 1) % (int) Const.LOG_BASE;
            if (e < 0) i += Const.LOG_BASE;

            if (i < len) {
                if (i != 0) x.d.addLast(parseDoubleOrNaN(str, 0, i));
                for (len -= Const.LOG_BASE; i < len; ) {
                    x.d.addLast(parseDoubleOrNaN(str, i, i += Const.LOG_BASE));
                }
                str = str.substring(i);
                i = (int) Const.LOG_BASE - str.length();
            } else {
                i -= len;
            }

            for (; i-- > 0; ) str = str + "0";
            x.d.addLast(parseDoubleOrNaN(str));

            if (external) {
                if (x.e > Config.maxE) { // Overflow to infinity
                    x.d = null;
                    x.e = NaN;
                } else if (x.e < Config.minE) { // underflow to zero
                    x.e = 0;
                    x.d = DdVec.FromDouble(0);
                }
            }
        }
    }


    /**
     * Modify this Decimal in place -- for use in constructors only.
     * Parse the input string as an unsigned unknown number
     * Input string should have been through the filter process before calling
     */
    private static void parseOther(Decimal target, NumericString num) {
        // L3620 (lines before this are in NumericString
        Decimal x = target;

        long p = 0;
        if (num.exponent.length() > 0) {
            double px = parseDoubleOrNaN(num.exponent);
            if (Double.isNaN(px)){
                x.makeNaN();
                return;
            }
            p = (long)px;
        }
        String str = num.mantissa;


        // Convert `str` as an integer then divide the result by `base` raised to a power such that the
        // fraction part will be restored.
        int i = num.mantissa.length();
        Decimal divisor = new Decimal(1);
        Decimal dbase = new Decimal(num.baseSize);
        if (num.decimalPosition >= 0){ // L3635
            // log[10](16) = 1.2041... , log[10](88) = 1.9444....
            i = num.mantissa.length() - num.decimalPosition; // ??? L3638
            divisor = intPow(dbase, i, i*i);
        }


        DdVec xd = convertBase(str, num.baseSize, Const.BASE);
        int xe = xd.length() - 1;

        // Remove trailing zeros. // L3647
        for (i = xe; xd.get(i) == 0; --i) xd.removeLast();
        if (i < 0) { // I think the original code is doing a signed zero here: `return new Ctor(x.s * 0);`
            x.makeZero();
            return;
        }
        x.e = getBase10Exponent(xd, xe);
        x.d = xd;
        external = false;

        // At what precision to perform the division to ensure exact conversion?
        // maxDecimalIntegerPartDigitCount = ceil(log[10](b) * otherBaseIntegerPartDigitCount)
        // log[10](2) = 0.30103, log[10](8) = 0.90309, log[10](16) = 1.20412
        // E.g. ceil(1.2 * 3) = 4, so up to 4 decimal digits are needed to represent 3 hex int digits.
        // maxDecimalFractionPartDigitCount = {Hex:4|Oct:3|Bin:1} * otherBaseFractionPartDigitCount
        // Therefore using 4 * the number of digits of str will always be enough.
        if (num.decimalPosition >= 0) x = divide(x, divisor, num.mantissa.length() * 4, Rounding.NOT_SPECIFIED, false, -1); // L3660

        // Multiply by the binary exponent part if present.
        if (p != 0) {
            if (Math.abs(p) < 54) x = x.times(Math.pow(2, p));
            else x = x.times(Decimal.pow(2, p));
        }
        external = true;
        target.setTo(x);
    }

    /** Internal to 'divide'. Assumes non-zero x and k, and hence non-zero result. */
    private static DdVec multiplyInteger(DdVec src, double k, int base) { //L2677
        DdVec x = new DdVec(src);
        double carry = 0;

        for (int i = x.length(); i > 0; i--) {
            double temp = x.get(i) * k + Math.floor(carry);

            carry = temp / base;
            double val = temp % base;

            x.set(i, (long)(val));
        }

        if (carry != 0) x.addFirst(carry);
        return  x;
    }

    /** Internal to 'divide' */
    private static int compare(DdVec a, DdVec b, int aL, int bL){ // L2693
        if (aL != bL){ // different scale
            return aL > bL ? 1 : -1;
        } else { // same scale, check components from most to least significant
            for (int i = 0; i < aL; i++){
                double av = a.get(i);
                double bv = b.get(i);
                if (av != bv) return av > bv ? 1 : -1;
            }
        }
        return 0;
    }

    /** Internal to 'divide'. Acts in-place */
    private static void subtract(DdVec a, DdVec b, int aL, int base){ // L2710
        int i = 0;

        // Subtract b from a.
        while (aL --> 0) {
            a.increment(aL, -i);
            double av = a.get(aL);
            double bv = b.get(aL);
            i = av < bv ? 1 : 0;
            a.set(aL, i * base + av - bv);
        }
        // Remove leading zeros.
        a.trimLeadingZero();
    }

    /**
     * Perform division in the specified base.
     * @param x in x/y
     * @param y in x/y
     * @param pr precision for result (in significant figures). Use -1 for 'not specified'
     * @param rm rounding mode
     * @param dp use decimal places for significant digits?
     * @param base BIN=2;OCT=8;DEC=10;HEX=16; (positional notation numeric base). Use -1 for default large base.
     * @return new Decimal result
     */
    private static Decimal divide(Decimal x, Decimal y, int pr, Rounding rm, boolean dp, int base) {
        // This is big, and has its own sub-functions. Start at lines 2674 and 2724.
        // precondition checks
        if (x.isNaN() || y.isNaN()) return Decimal.decimalNaN();
        if (x.isInfinity() && y.isInfinity()) return Decimal.decimalNaN();
        if (x.isZero() && y.isZero()) return Decimal.decimalNaN();

        int sign = (x.s == y.s) ? 1 : -1;
        if (x.isZero() || y.isInfinity()) return Decimal.signedZero(sign);
        if (y.isZero()) return Decimal.signedInfinity(sign);

        double e, logBase;
        DdVec xd = x.d;
        DdVec yd = y.d;

        if (base > 0){ // L2742
            logBase = 1;
            e = x.e - y.e;
        } else {
            base = (int)Const.BASE;
            logBase = Const.LOG_BASE;
            e = Math.floor(x.e / logBase) - Math.floor(y.e / logBase);
        }

        int yL = yd.length();
        int xL = xd.length();
        Decimal q = new Decimal(sign);
        DdVec qd = new DdVec();
        q.d = qd;

        // Result exponent may be one less than e.
        // The digit array of a Decimal from toStringBinary may have trailing zeros.
        int i;
        for (i = 0; yd.get(i) == xd.get(i,0.0);) i++;
        if (yd.get(i) > xd.get(i,0.0)) e--;

        double sd;
        if (pr <= 0) { // L2762
            sd = Config.precision;
            pr = (int)sd;
            rm = Config.rounding;
        } else if (dp) {
            sd = pr + (x.e - y.e) + 1;
        } else {
            sd = pr;
        }

        boolean more;
        double k, t;
        if (sd < 0){
            qd.addLast(1);
            more = true;
        } else { // L2774
            // Convert precision in number of base 10 digits to base 1e7 digits.
            sd = Math.floor(sd / logBase + 2);
            i = 0;

            if (yL == 1) {// divisor < 1e7
                k = 0;
                double yd0 = yd.get(0);
                sd++;

                // k is carry
                for (; (i < xL || k != 0) && (sd > 0); i++, sd--){
                    t = k * base + (xd.get(i, 0.0));
                    qd.set(i, Math.floor(t / yd0));
                    k = Math.floor(t % yd0);
                }

                more = (k != 0) || (i < xL);
            } else { // divisor >= 1e7 (L2796)
                // Normalise xd and yd so highest order digit of yd is >= base/2
                k = Math.floor(base / (yd.get(0) + 1));
                if (k > 1) {
                    yd = multiplyInteger(yd, k, base);
                    xd = multiplyInteger(xd, k, base);
                    yL = yd.length();
                    xL = xd.length();
                }

                int xi = yL;
                DdVec rem = xd.slice(0, yL);
                int remL = rem.length();

                // Add zeros to make remainder as long as divisor.
                for (; remL < yL; remL++) rem.addLast(0);

                DdVec yz = new DdVec(yd);
                yz.addFirst(0);
                double yd0 = yd.get(0);
                if (yd.get(1) >= (base / 2.0)) yd0++;

                DdVec prod;
                int prodL;
                do { // L2821
                    k=0;

                    int cmp = compare(yd, rem, yL, remL);

                    if (cmp < 0) { // divisor < remainder  (L2828)
                        // Calculate trial digit, k.
                        double rem0 = rem.get(0);
                        if (yL != remL) rem0 = rem0 * base + (rem.get(1,0));

                        // k will be how many times the divisor goes into the current remainder.
                        k = Math.floor(rem0 / yd0);

                        //  Algorithm:
                        //  1. product = divisor * trial digit (k)
                        //  2. if product > remainder: product -= divisor, k--
                        //  3. remainder -= product
                        //  4. if product was < remainder at 2:
                        //    5. compare new remainder and divisor
                        //    6. If remainder > divisor: remainder -= divisor, k++

                        if (k > 1){ // L2844
                            if (k >= base) k = base - 1;

                            // product = divisor * trial digit.
                            prod = multiplyInteger(yd, k, base);
                            prodL = prod.length();
                            remL = rem.length();
                            // Compare product and remainder.
                            cmp = compare(prod, rem, prodL, remL);

                            // product > remainder.
                            if (cmp == 1) {
                                k--;
                                // Subtract divisor from product.
                                subtract(prod, yL < prodL ? yz : yd, prodL, base);
                            }
                        } else { // L2862
                            // cmp is -1.
                            // If k is 0, there is no need to compare yd and rem again below, so change cmp to 1
                            // to avoid it. If k is 1 there is a need to compare yd and rem again below.
                            if (k == 0) {cmp = 1; k = 1;}
                            prod = new DdVec(yd);
                        } // L2869

                        prodL = prod.length();
                        if (prodL < remL) prod.addFirst(0);

                        // Subtract product from remainder.
                        subtract(rem, prod, remL, base);

                        // If product was < previous remainder.
                        if (cmp == -1) {
                            remL = rem.length();

                            // Compare divisor and new remainder.
                            cmp = compare(yd, rem, yL, remL);

                            // If divisor < new remainder, subtract divisor from remainder.
                            if (cmp < 1) {
                                k++;
                                // Subtract divisor from remainder.
                                subtract(rem, yL < remL ? yz : yd, remL, base);
                            }
                        }

                        remL = rem.length();
                    } else if (cmp == 0){ // L2896
                        k++;
                        rem = DdVec.FromDouble(0);
                    } // else if cmp == 1, k will be 0

                    // Add the next digit, k, to the result array.
                    qd.set(i, k);
                    i++;

                    // Update the remainder.
                    if ((cmp != 0) && (rem.get(0,0) != 0)) {
                        rem.set(remL, xd.get(xi, 0));
                        remL++;
                    } else {
                        rem = DdVec.FromDouble(xd.get(xi));
                        remL = 1;
                    }
                } while ((xi++ < xL || rem.length() > 0) && (sd-- != 0)); //?? L2911

                more = rem.length() > 0; // L2913
            }

            // Leading zero?
            qd.trimLeadingZero();
        } // if (sd < 0) {} else {}

        // logBase is 1 when divide is being used for base conversion.
        if (logBase == 1) {
            q.e = e;
            q.inexact = more;
        } else {
            // To calculate q.e, first get the number of digits of qd[0].
            for (i = 1, k = qd.get(0); k >= 10; k /= 10) i++;
            q.e = i + e * logBase - 1;

            finalise(q, dp ? pr + q.e + 1 : pr, rm, more);
        }

        return q;
    }

    private static Decimal pow(int x, long y) {
        return new Decimal(x).pow(new Decimal(y));
    }

    private static double getBase10Exponent(DdVec digits, int e) {
        // L3143
        double w = digits.get(0);

        // Add the number of digits of the first word of the digits array.
        for ( e *= Const.LOG_BASE; w >= 10; w /= 10) e++;
        return e;
    }

    /**
     * Return a new Decimal whose value is the value of Decimal `x` to the power `n`, where `n` is an
     * integer of type number.
     *
     * Implements 'exponentiation by squaring'. Called by `pow` and `parseOther`.
     *
     * @param x number to be raised
     * @param n integer exponent
     * @param pr precision for result (in significant figures)
     */
    private static Decimal intPow(Decimal x, long n, int pr) {
        boolean isTruncated = false;
        Decimal r = new Decimal(1.0);

        // Max n of 9007199254740991 takes 53 loop iterations.
        // Maximum digits array length; leaves [28, 34] guard digits.
        int k = (int)Math.ceil(pr / Const.LOG_BASE + 4);

        external = false; // turn off normalisation

        for (;;) {
            if ((n % 2) != 0) {
                r = r.times(x);
                if (truncate(r.d, k)) isTruncated = true;
            }

            n = (int)Math.floor(n / 2.0);
            if (n == 0) {

                // To ensure correct rounding when r.d is truncated, increment the last word if it is zero.
                n = r.d.length() - 1;
                if (isTruncated && r.d.get((int)n) == 0) r.d.increment((int)n, 1.0);
                break;
            }

            x = x.times(x);
            truncate(x.d, k);
        }

        external = true;

        return r;
    }

    public Decimal times(Decimal x) { // L1869
        return x; // TODO: implement
    }
    public Decimal times(double x) { // L1869
        return null; // TODO: implement
    }


    /**
     * Set this decimal instance to a not-a-number value
     */
    private void makeNaN() {
        s = NaN;
        e = NaN;
        d = null;
    }

    /**
     * Set this decimal instance to an infinite value, keeping existing sign
     */
    private void makeInfinite() {
        e = NaN;
        d = null;
    }

    /**
     * Set this decimal instance to a zero value, keeping existing sign
     */
    private void makeZero() {
        e = 0;
        d = DdVec.FromDouble(0);
    }

    /** Mutate this decimal to use same values as another (INTERNAL USE ONLY) */
    private void setTo(Decimal x) {
        d = x.d; // ref, not copy
        e = x.e;
        s = x.s;
    }

    /**
     * Round `x` to `sd` significant digits using rounding mode `rm`. Check for over/under-flow.
     */
    private static Decimal finalise(Decimal x, double sd, Rounding rm, boolean isTruncated) {
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
                            xd.addLast(0);
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
                if (roundUp) {
                    // Convert sd to decimal places.
                    sd -= x.e + 1;

                    // 1, 0.1, 0.01, 0.001, 0.0001 etc.
                    xd.clear();
                    xd.addFirst(Math.pow(10, (Const.LOG_BASE - sd % Const.LOG_BASE) % Const.LOG_BASE));
                    x.e = -sd;
                } else {
                    // Zero.
                    xd.clear();
                    xd.addFirst(0);
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
            for (i = xd.length(); xd.get((int) (--i)) == 0; ) xd.removeLast();
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

    /** Truncate vector if required. Does not extend length.
     * Returns true if vector was shortened */
    private static boolean truncate(DdVec arr, int len) {
        if (arr.length() > len) {
            arr.truncateTo(len);
            return true;
        }
        return false;
    }

    /** Convert string of `baseIn` to an array of numbers of `baseOut`.
     Eg. convertBase('255', 10, 16) returns [15, 15].
     Eg. convertBase('ff', 16, 10) returns [2, 5, 5].
     */
    private static DdVec convertBase(String str, double baseIn, double baseOut) {
        int j, i=0;
        DdVec arr = DdVec.FromDouble(0);
        int arrL;
        int strL = str.length();

        for (; i < strL;) {
            for (arrL = arr.length(); arrL-->0;) arr.multiply(arrL, baseIn);
            arr.increment(0, Const.NUMERALS(str.charAt(i++)));
            for (j = 0; j < arr.length(); j++) {
                if (arr.get(j) > baseOut - 1) {
                    if (!arr.hasIndex(j + 1)) arr.addLast(0);
                    arr.increment(j + 1, (long)(arr.get(j) / baseOut));
                    arr.modulo(j, baseOut);//arr[j] %= baseOut;
                }
            }
        }

        arr.reverse();
        return arr;
    }

    /**
     * Parse a string to a double value. If parsing fails, return NaN.
     */
    private static double parseDoubleOrNaN(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ex) {
            return NaN;
        }
    }

    /**
     * Parse a range inside a string to a double value. If parsing fails, return NaN.
     */
    private static double parseDoubleOrNaN(String str, int start, int end) {
        try {
            return Double.parseDouble(str.substring(start, end));
        } catch (Exception ex) {
            return NaN;
        }
    }
}
