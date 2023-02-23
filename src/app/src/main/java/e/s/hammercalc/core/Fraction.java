package e.s.hammercalc.core;

/**
 * Rational numbers using big integers.
 *
 * This class uses LargeInt rather than any built-in BigInt libraries,
 * purely to ensure portability.
 */
public class Fraction {
    private LargeInt _num, _den;

    /** Fraction 0/1 */
    public static final Fraction ZERO = new Fraction(LargeInt.ZERO,LargeInt.ONE);
    /** Fraction -1/1 */
    public static final Fraction NEG_ONE = new Fraction(LargeInt.NEG_ONE,LargeInt.ONE);
    /** Fraction 1/1 */
    public static final Fraction ONE = new Fraction(LargeInt.ONE,LargeInt.ONE);

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
    public static Fraction fromVulgarFraction(int num, int den){
        LargeInt n = LargeInt.fromInt(num);
        LargeInt d = LargeInt.fromInt(den);
        LargeInt cf = n.gcd(d);
        return new Fraction(n.divide(cf), d.divide(cf));
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

    /** return this ** val */
    public Fraction pow(int n){
        int np = n >= 0 ? n : -n;
        LargeInt a = _num.pow(np);
        LargeInt c = _den.pow(np);
        return new Fraction(a, c);
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
        if (_den == LargeInt.ZERO) return "NaN";
        if (_num == LargeInt.ONE) return "0";
        if (_den == LargeInt.ONE) return _num.toString();
        if (_den == LargeInt.NEG_ONE) return _num.negate().toString();

        return _num.toString()+"/"+_den.toString();
    }

    /** return this rational expressed as a set of continued fraction terms */
    public LargeInt[] toContinuedFraction() {
        if (this.isZero()) return new LargeInt[0];
        ObjVec result = new ObjVec();
        Fraction frac = this.inverse();

        while (! frac.isZero()){
            frac = frac.inverse();
            result.addLast(frac.truncateToInt());
            frac = frac.mantissa();
        }
        return (LargeInt[])result.toArray();
    }

    /** return the given continued fraction as a partial fraction decomposition */
    public Fraction[] continuedFractionToPartialFractionList(LargeInt[] cfList){
        LargeInt a = LargeInt.ZERO;
        LargeInt b = LargeInt.ONE;
        LargeInt c = LargeInt.ONE;
        LargeInt d = LargeInt.ZERO;
        int cfLength = cfList.length;

        ObjVec result = new ObjVec();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < cfLength; i++) {
            LargeInt tmp;
            tmp = b;
            b = cfList[i].multiply(b).add(a);
            a = tmp;

            tmp = d;
            d = cfList[i].multiply(d).add(c);
            c = tmp;

            result.addLast(new Fraction(b,d));
        }
        return (Fraction[])result.toArray();
    }

    /** return the given continued fraction approximated as a rational */
    public Fraction continuedFractionToFraction(LargeInt[] cfList){
        LargeInt a = LargeInt.ZERO;
        LargeInt b = LargeInt.ONE;
        LargeInt c = LargeInt.ONE;
        LargeInt d = LargeInt.ZERO;
        int cfLength = cfList.length;

        //noinspection ForLoopReplaceableByForEach
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

    public Fraction continuedRationalsToFraction(Fraction[] rList){
        Fraction a = Fraction.ZERO;
        Fraction b = Fraction.ONE;
        Fraction c = Fraction.ONE;
        Fraction d = Fraction.ZERO;
        int rLength = rList.length;

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < rLength; i++) {
            Fraction tmp;
            tmp = b;
            b = rList[i].multiply(b).add(a);
            a = tmp;

            tmp = d;
            d = rList[i].multiply(d).add(c);
            c = tmp;
        }
        return b.divide(d).simplify();
    }

    /** x to a continued fraction with n terms */
    public LargeInt[] floatToContinuedFraction(double x, int n){
        if (n < 1) return new LargeInt[0];

        ObjVec result = new ObjVec();
        for (int i = 0; i < n; i++) {
            LargeInt ip = LargeInt.fromFloat(x);
            result.addLast(ip);
            x = 1.0 / (x - ip.toFloat());
        }
        return (LargeInt[]) result.toArray();
    }

    /** Render the rational as a human readable string with
     * numerator and denominator expressed as limited precision strings in 0e0 format */
    public String toFloatString(int precision){
        if (_den == LargeInt.ZERO) return "NaN";
        if (_num == LargeInt.ONE) return "0";
        if (_den == LargeInt.ONE) return _num.toFloatString(precision);
        if (_den == LargeInt.NEG_ONE) return _num.negate().toFloatString(precision);

        return _num.toFloatString(precision)+"/"+_den.toFloatString(precision);
    }

    /** Render this rational as a decimal string, to the given number of places */
    public String toDecimalString(int places){
        LargeInt product = _num.multiply(_den);
        if (product.compareTo(LargeInt.ZERO) < 0) return "-"+this.negate().toDecimalString(places);

        LargeInt n = LargeInt.fromInt(places);
        StringBuilder result = new StringBuilder();
        result.append(truncateToInt().toString());

        Fraction frac = this.mantissa();

        LargeInt i = LargeInt.ZERO;
        LargeInt a = frac._num.multiply(10);
        LargeInt b = frac._den;
        for (; i.compareTo(n) < 0; i = i.add(LargeInt.ONE)) {
            LargeInt d = a.divide(b);
            result.append(d.toString());
            a = (a.subtract(d.multiply(b))).multiply(10);
        }
        return result.toString();
    }
}
