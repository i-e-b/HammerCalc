package e.s.hammercalc.core;

/** source for repeating terms of a continued fraction */
interface ITermExpansion{
    /** Number of terms remaining. Use {@code -1} for unknown or infinite */
    long termCount();

    /** Length of section that repeats. {@code 0} for no repeating section */
    long repeatingTerms();

    /** Give term i of the expansion. Starts at zero. */
    LargeInt term(long i);
}

/**
 * Continued fraction numbers.
 *
 * See <a href="https://r-knott.surrey.ac.uk/Fibonacci/cfINTRO.html">Continued Fraction</a>
 * */
public abstract class ContinuedFraction {
    // These two are used differently depending on what mode we're in.

    /** How to interpret this continued fraction */
    private final CfType _type;

    /** Numerator terms. First is the integer part.
     * If this runs out before {@code _under}, a stream of 1s is assumed.
     *
     * When both {@code _over} and {@code _under} are exhausted, we switch
     * to {@code _moreOver} and {@code _moreUnder} if they are supplied.
     */
    private final LargeIntVec _over;

    /** Index {@code _over} returns to when repeating.
     * {@code -1} means no repeating. */
    private final int _overRepeat;

    /** Denominator terms. Index 0 matches to {@code _over}'s index 1.
     * If this runs out before {@code _over}, a stream of 1s is assumed.
     *
     * When both {@code _over} and {@code _under} are exhausted, we switch
     * to {@code _moreOver} and {@code _moreUnder} if they are supplied.
     */
    private final LargeIntVec _under;

    /** Index {@code _under} returns to when repeating.
     * {@code -1} means no repeating. */
    private final int _underRepeat;

    /** Continued numerator terms. These can be infinite and/or repeating.
     * The indexes should exactly match {@code _moreUnder}.
     * If {@code null}, then this is a finite fraction. */
    private final ITermExpansion _moreOver;

    /** Continued denominator terms. These can be infinite and/or repeating.
     * The indexes should exactly match {@code _moveOver}.
     * If {@code null}, then this is a finite fraction. */
    private final ITermExpansion _moreUnder;

    protected ContinuedFraction() {
        _type = CfType.Zero;
        _over = null;
        _overRepeat = -1;
        _under = null;
        _underRepeat = -1;
        _moreOver = null;
        _moreUnder = null;
    }

    /** return this + other */
    public ContinuedFraction add(ContinuedFraction other){
        // TODO: implement
        return null;
    }

    private Fraction element(int i) {
        // TODO: implement
        return null;
    }


    // Bunch of helper functions

    /** 2x2 rational matrix multiply */
    private static Fraction[] matMul(Fraction[] mat1, Fraction[] mat2){
        Fraction[] result = new Fraction[4];

        result[0] = mat1[0].multiply(mat2[0]).add(mat1[1].multiply(mat2[2])).simplify();
        result[1] = mat1[0].multiply(mat2[1]).add(mat1[1].multiply(mat2[3])).simplify();
        result[2] = mat1[2].multiply(mat2[0]).add(mat1[3].multiply(mat2[2])).simplify();
        result[3] = mat1[2].multiply(mat2[1]).add(mat1[3].multiply(mat2[3])).simplify();

        return result;
    }

    private static Fraction fractionSimplify(Fraction frac, int ip){
        LargeInt p = LargeInt.TEN.pow(ip);
        LargeInt pp = p.multiply(p);
        Fraction f = frac;

        boolean flip = false;
        if ( ! f.isPositive()){ // negative or zero
            f = f.negate();
            flip = true;
        }
        while (f.overMagnitude(pp)) f.reduce(p);
        while (f.overMagnitude(p)) f.reduce(LargeInt.TEN);

        if (flip) return f.negate();
        return f;
    }

    private static String toDecimalSpecialCase(ContinuedFraction cf, int precision){
        LargeInt tenP = LargeInt.TEN.pow(precision + 1);
        Fraction cmp = Fraction.fromVulgarFraction(LargeInt.ONE, tenP);

        Fraction a = Fraction.fromVulgarFraction(1,1);
        Fraction b = Fraction.fromVulgarFraction(0,1);
        Fraction c = Fraction.fromVulgarFraction(0,1);
        Fraction d = Fraction.fromVulgarFraction(1,1);
        boolean inPrecision = false;
        for (int i = 0; !inPrecision; i++) {
            Fraction newA = a.multiply(cf.element(i)).add(b);
            Fraction newC = c.multiply(cf.element(i)).add(d);
            b=a;a=newA;
            d=c;c=newC;

            if (!c.isZero() && !d.isZero()){
                Fraction test = a.divide(c).distance(b.divide(d));
                inPrecision = test.compareTo(cmp) < 0;
            }
        }
        Fraction diff = Fraction.fromVulgarFraction(LargeInt.fromInt(5), tenP);
        Fraction result;
        if (a.multiply(c).compareTo(Fraction.ZERO) < 0) {
            result = a.divide(c).subtract(diff);
        } else {
            result = a.divide(c).add(diff);
        }
        return result.toDecimalString(precision);
    }

    private enum CfType {
        /** CF has no terms and is equal to zero */
        Zero,

        /** A simple finite continued fraction.
         * CF has a fixed number of 'under' terms and a single 'over' term.
         * '_moreOver' and '_moreUnder' are not used. */
        Finite,

        /** A simple repeating continued fraction.
         * CF has a fixed number of '_under' terms and a single '_over' term.
         * Once '_under' is exhausted, it restarts at '_underRepeat'.
         * '_moreOver' and '_moreUnder' are not used.
         */
        Periodic,

        /** A continued fraction based on generators */
        Formula, FirstComposite,
        SecondComposite, SqrtComposite, Generalised
    }
}
