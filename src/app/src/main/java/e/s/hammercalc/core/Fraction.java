package e.s.hammercalc.core;

/**
 * Rational numbers using big integers.
 *
 * This class uses LargeInt rather than any built-in BigInt libraries,
 * purely to ensure portability.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public class Fraction {
    private final LargeInt _num;
    private final LargeInt _den;

    /** Fraction 0/1 */
    public static final Fraction ZERO = new Fraction(LargeInt.ZERO,LargeInt.ONE);
    /** Fraction -1/1 */
    public static final Fraction NEG_ONE = new Fraction(LargeInt.NEG_ONE,LargeInt.ONE);
    /** Fraction 1/1 */
    public static final Fraction ONE = new Fraction(LargeInt.ONE,LargeInt.ONE);
    /** Fraction 1/2 */
    public static final Fraction HALF = new Fraction(LargeInt.ONE,LargeInt.TWO);
    /** Fraction -1/2 */
    public static final Fraction NEG_HALF = new Fraction(LargeInt.NEG_ONE,LargeInt.TWO);

    protected Fraction(LargeInt num, LargeInt den){
        if (den.sign() < 0){
            _num = num.negate();
            _den = den.negate();
        } else {
            _num = num;
            _den = den;
        }
    }

    /** new fraction representing <c>num/den</c> */
    public static Fraction fromVulgarFraction(LargeInt num, LargeInt den){
        LargeInt cf = num.gcd(den);
        return new Fraction(num.divide(cf), den.divide(cf));
    }

    /** new fraction representing <c>num/den</c> */
    public static Fraction fromVulgarFraction(String num, String den){
        LargeInt n = new LargeInt(num);
        LargeInt d = new LargeInt(den);
        LargeInt cf = n.gcd(d);
        return new Fraction(n.divide(cf), d.divide(cf));
    }

    /** new fraction representing <c>num/den</c> */
    public static Fraction fromVulgarFraction(int num, int den){
        LargeInt n = LargeInt.fromInt(num);
        LargeInt d = LargeInt.fromInt(den);
        LargeInt cf = n.gcd(d);
        return new Fraction(n.divide(cf), d.divide(cf));
    }

    /** new fraction representing <c>i/1</c> */
    public static Fraction fromInteger(int i){
        LargeInt n = LargeInt.fromInt(i);
        LargeInt d = LargeInt.ONE;
        return new Fraction(n, d);
    }

    /** new fraction representing <c>i/1</c> */
    public static Fraction fromInteger(LargeInt n){
        LargeInt d = LargeInt.ONE;
        return new Fraction(n, d);
    }

    /** return absolute value of this rational */
    public Fraction abs(){
        if (isPositive()) return this;
        return this.negate();
    }

    /** return this + val */
    public Fraction add(Fraction val){
        LargeInt a = _num.multiply(val._den);
        LargeInt b = _den.multiply(val._num);
        LargeInt c = _den.multiply(val._den);
        return new Fraction(a.add(b), c);
    }

    /** return this - val */
    public Fraction subtract(Fraction val){
        LargeInt a = _num.multiply(val._den);
        LargeInt b = _den.multiply(val._num);
        LargeInt c = _den.multiply(val._den);
        return new Fraction(a.subtract(b), c);
    }

    /** return this * val */
    public Fraction multiply(Fraction val){
        LargeInt a = _num.multiply(val._num);
        LargeInt c = _den.multiply(val._den);
        return new Fraction(a, c);
    }

    /** return this / val */
    public Fraction divide(Fraction val){
        LargeInt a = _num.multiply(val._den);
        LargeInt c = _den.multiply(val._num);
        return new Fraction(a, c);
    }

    /** return this ** val
     * For fractional powers and square roots, see the continued fractions */
    public Fraction pow(int n){
        if (n == 0) return ONE;
        if (n > 0) {
            LargeInt a = _num.pow(n);
            LargeInt c = _den.pow(n);
            return new Fraction(a, c);
        } else {
            LargeInt a = _num.pow(-n);
            LargeInt c = _den.pow(-n);
            return new Fraction(c, a);
        }
    }

    /** return a rational that expresses the fractional part of the value */
    public Fraction mantissa(){
        return this.subtract(this.truncate());
    }

    /** Truncate rational to integer valued rational */
    public Fraction truncate(){
        return new Fraction(_num.divide(_den), LargeInt.ONE);
    }

    /** Truncate rational to integer */
    public LargeInt truncateToInt(){
        return _num.divide(_den);
    }

    /** return this rational simplified to smalled numerator and denominator */
    public Fraction simplify(){
        LargeInt d = _num.gcd(_den).abs();
        if (d.equals(LargeInt.ONE)) return this;
        return new Fraction(_num.divide(d), _den.divide(d));
    }

    /** return the multiplicative inverse of this rational
     * (i.e. a/b becomes b/a) */
    public Fraction inverse(){return new Fraction(_den, _num);}

    /** Return 0 if ints are equal; <p>
     * -1 if 'val' is greater than 'this'; </p>
     * 1 if 'val' is less */
    public int compareTo(Fraction val) {
        LargeInt a = this._num.multiply(val._den);
        LargeInt b = this._den.multiply(val._num);

        LargeInt c = a.subtract(b);
        return c.compareTo(LargeInt.ZERO);
    }

    /** Return true if both ints have exactly the same value */
    public boolean equals(Fraction other){
        return compareTo(other) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        final Fraction other = (Fraction) obj;
        return this.equals(other);
    }

    @Override
    public int hashCode() {
        return _num.hashCode() ^ _den.hashCode();
    }

    /** return true if this rational is greater than zero */
    public boolean isPositive(){return _num.sign() == 1;}

    /** returns true if this rational is zero valued */
    public boolean isZero(){return _num.equals(LargeInt.ZERO);}

    /** returns true if this rational is integer valued */
    public boolean isInteger(){
        LargeInt intVal = _num.divide(_den).multiply(_den);
        return intVal.equals(this._num);
    }

    /** Return a rational with same magnitude and opposite sign */
    public Fraction negate(){return new Fraction(_num.negate(), _den);}

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        if (_den.equals(LargeInt.ZERO)) return "NaN";
        if (_num.equals(LargeInt.ZERO)) return "0";

        Fraction f = simplify();
        if (f._den.equals(LargeInt.ONE)) return f._num.toString();
        if (f._den.equals(LargeInt.NEG_ONE)) return f._num.negate().toString();

        return f._num.toString() + "/" + f._den;
    }

    /** return this rational expressed as a set of continued fraction terms */
    public LargeInt[] toContinuedFractionArray() {
        int sign = this.compareTo(Fraction.ZERO);
        if (sign == 0) return new LargeInt[0];
        LargeIntVec result = new LargeIntVec();

        if (sign > 0) { // positive values
            Fraction frac = this.inverse();

            while (!frac.isZero()) {
                frac = frac.inverse();
                result.addLast(frac.truncateToInt());
                frac = frac.mantissa();
            }
        } else { // negative values
            // WE want all positive terms beyond the first, so:
            // Where fr is our fraction, I is the integer part, M is mantissa.
            // fr = I + M; -fr = -I - M; -fr = -I -1 + (1 - I)
            LargeInt I = this.truncateToInt().subtract(LargeInt.ONE);
            Fraction m = Fraction.ONE.subtract(this.negate().mantissa());

            Fraction frac = m.inverse();

            while (!frac.isZero()) {
                frac = frac.inverse();
                result.addLast(frac.truncateToInt());
                frac = frac.mantissa();
            }

            result.removeFirst(); // should be a zero
            result.addFirst(I); // put back the integer we sliced
        }
        return result.toArray();
    }

    /** return the given continued fraction approximated as a rational */
    public static Fraction continuedFractionToFraction(LargeInt[] cfList){
        LargeInt a = LargeInt.ZERO;
        LargeInt b = LargeInt.ONE;
        LargeInt c = LargeInt.ONE;
        LargeInt d = LargeInt.ZERO;
        int cfLength = cfList.length;

        for (int i = 0; i < cfLength; i++) {
            LargeInt tmp;
            tmp = b;
            b = cfList[i].multiply(b).add(a);
            a = tmp;

            tmp = d;
            d = cfList[i].multiply(d).add(c);
            c = tmp;
        }
        return new Fraction(b,d).simplify();
    }

    /** x to a continued fraction with n terms */
    public static LargeInt[] floatToContinuedFraction(double x, int n){
        if (n < 1) return new LargeInt[0];

        LargeIntVec result = new LargeIntVec();
        for (int i = 0; i < n; i++) {
            LargeInt ip = LargeInt.fromFloat(x);
            result.addLast(ip);
            x = 1.0 / (x - ip.toFloat());
        }
        return result.toArray();
    }

    /** Render the rational as a human readable string with
     * numerator and denominator expressed as limited precision strings in 0e0 format */
    public String toFloatString(int precision){
        if (_den.equals(LargeInt.ZERO)) return "NaN";
        if (_num.equals(LargeInt.ZERO)) return "0";

        Fraction f = simplify();

        if (f._den.equals(LargeInt.ONE)) return _num.toFloatString(precision);
        if (f._den.equals(LargeInt.NEG_ONE)) return _num.negate().toFloatString(precision);

        return f._num.toFloatString(precision)+"/"+f._den.toFloatString(precision);
    }

    /** Render this rational as a decimal string, to the given number of places */
    public String toDecimalString(int places){
        LargeInt product = _num.multiply(_den);
        if (product.compareTo(LargeInt.ZERO) < 0) return "-"+this.negate().toDecimalString(places);

        LargeInt n = LargeInt.fromInt(places);
        StringBuilder result = new StringBuilder();
        result.append(truncateToInt().toString());

        Fraction frac = this.mantissa();
        if (frac.isZero()) return result.toString();

        result.append('.');

        LargeInt i = LargeInt.ZERO;
        LargeInt a = frac._num.multiply(10);
        LargeInt b = frac._den;
        for (; i.compareTo(n) < 0; i = i.add(LargeInt.ONE)) {
            LargeInt d = a.divide(b);
            result.append(d.toString());
            a = (a.subtract(d.multiply(b))).multiply(10);
            if (a.isZero()) break; // terminate if we get an exact decimal
        }

        return result.toString();
    }
}
