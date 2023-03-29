package e.s.hammercalc.core;
///////////////////////////////////////////////////////////////////////////////////////////////
// NOTE
//
// This file is a mess, and probably will remain so while I am learning continued
// fractions, with their various variants.
// Nothing is here is likely to be "correct" unless covered by multiple tests.
//
// https://r-knott.surrey.ac.uk/Fibonacci/cfINTRO.html
///////////////////////////////////////////////////////////////////////////////////////////////

public class ContinuedFraction {

    public static class Constants {
        public static ContinuedFraction C_PiUnder4(){
            return new ContinuedFraction(new PiUnder4());
        }
    }

    private CFGeneralTerms _terms;

    public ContinuedFraction(CFGeneralTerms terms) {
        _terms = terms;
    }

    public static ContinuedFraction fromRational(Fraction f){
        return new ContinuedFraction(new TermsFromRational(f));
    }

    public CfSimplifier Simplify() {
        return new CfSimplifier(_terms);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        int maxTerms = 50;
        for (int i = 0; i < maxTerms; i++) {
            LargeInt term = _terms.getLeft();
            LargeInt over = _terms.getOver();

            if (i == 1) sb.append("; ");
            else if (i > 1) sb.append(", ");

            if (over.equals(LargeInt.ONE)) {
                sb.append(term.toString());
            } else {
                sb.append(over.toString());
                sb.append('/');
                sb.append(term.toString());
            }

            if (!_terms.next()) break;
        }
        return sb.toString();
    }

    public static class CfSimplifier implements CFSimpleTerms {
        LargeInt a, b, c, d, nextValue;
        CFGeneralTerms source;
        boolean moreTerms;

        public CfSimplifier(CFGeneralTerms source) {
            this.source = source;
            moreTerms = true;
            a = LargeInt.ZERO;
            b = LargeInt.ONE;
            c = LargeInt.ONE;
            d = LargeInt.ZERO;
            nextValue = LargeInt.ZERO;
            next();
        }

        @Override
        public LargeInt getLeft() {
            return nextValue;
        }

        @Override
        public boolean next() {
            if (!moreTerms) return false;

            while (moreTerms) {
                LargeInt p = source.getLeft();
                LargeInt q = source.getOver();

                LargeInt a1 = b.multiply(q);
                LargeInt b1 = b.multiply(p).add(a);
                LargeInt c1 = d.multiply(q);
                LargeInt d1 = d.multiply(p).add(c);

                a = a1;
                b = b1;
                c = c1;
                d = d1;

                if (c1.isZero()) {
                    moreTerms = source.next();
                    continue;
                }
                if (d1.isZero()) {
                    moreTerms = source.next();
                    continue;
                }

                LargeInt ac = a.divide(c);
                LargeInt bd = b.divide(d);

                if (ac.equals(bd)) {
                    nextValue = ac;

                    a1 = c;
                    b1 = d;
                    c1 = a.subtract(c.multiply(nextValue));
                    d1 = b.subtract(d.multiply(nextValue));

                    a = a1;
                    b = b1;
                    c = c1;
                    d = d1;
                    return true;
                }
                moreTerms = source.next();
            }
            // last term
            nextValue = b;
            return true;
        }
    }

    private static class TermsFromRational implements CFGeneralTerms {
        private Fraction _f;
        private LargeInt[] _f2;
        private int _state; // state machine. `-1` is terminate
        private LargeInt _next;

        public TermsFromRational(Fraction f) {
            _f = f;
            _state = 0;
            next(); // run first time
        }

        @Override
        public LargeInt getOver() {
            return LargeInt.ONE;
        }

        @Override
        public LargeInt getLeft() {
            return _next;
        }

        @Override
        public boolean next() {
            if (_state < 0) return false;

            if (_state == 0){
                if (_f.getNumerator().equals(LargeInt.ZERO)){ // 0/d
                    _next = LargeInt.ZERO;
                    _state = -1;
                    return true;
                }
                _state++;
            }

            if (_state == 1){
                if (_f.getDenominator().equals(LargeInt.ZERO)){ // n/0
                    _next = LargeInt.ZERO;
                    _state = -2;
                    return false;
                }
                _state++;
            }

            if (_state == 2){
                if (_f.getDenominator().compareTo(_f.getNumerator()) > 0){ // [0..1)
                    _next = LargeInt.ZERO;
                    _f = _f.inverse();
                    _state++;
                    return true;
                }
                _state++;
            }

            // Main sequence loop, state=3
            while (true) {
                if (_state == 3) {
                    _f2 = _f.divMod();

                    if (_f2[1].equals(_f.getDenominator())) { // 1/1 or equivalent
                        _next = _f2[0].add(LargeInt.ONE);
                        _state = -1;
                        return true;
                    }
                    _state++;
                }

                if (_state == 4) {
                    if (_f2[0].isZero()) { // should have ended before
                        _next = LargeInt.ZERO;
                        _state = -1;
                        return false;
                    }
                    _state++;
                }

                if (_state == 5) {
                    _next = _f2[0];
                    _state++;
                    return true;
                }

                if (_state == 6) {
                    if (_f2[1].isZero()){
                        _next = LargeInt.ZERO;
                        _state = -1;
                        return false;
                    }
                }

                _state = 3;
                _f = new Fraction(_f2[1], _f2[0]);
            }
        }
    }
}



/**
 * Represents 4/π as a general continued fraction
 */
class PiUnder4 implements CFGeneralTerms {
    private LargeInt index = LargeInt.ONE;

    @Override
    public LargeInt getOver() {
        return index.multiply(index);
    }

    @Override
    public LargeInt getLeft() {
        return index.multiply(2).decrement();
    }

    @Override
    public boolean next() {
        index = index.increment();
        return true;
    }
}

/**
 * source for terms of a general continued fraction
 */
interface CFGeneralTerms {

    /**
     * Get the "over" part of the current term.
     * This should be called before the first call to {@code next()}
     */
    LargeInt getOver();

    /**
     * Get the "left" part of the current term.
     * This should be called before the first call to {@code next()}
     */
    LargeInt getLeft();

    /**
     * Move to the next pair of terms. This should be called
     * after consuming the first terms. Return false if no more
     * terms in the set.
     */
    boolean next();
}

interface CFSimpleTerms {
    /**
     * Get the "left" part of the current term.
     * This should be called before the first call to {@code next()}
     */
    LargeInt getLeft();

    /**
     * Move to the next pair of terms. This should be called
     * after consuming the first terms. Return false if no more
     * terms in the set.
     */
    boolean next();
}

/**
 * source for repeating terms of a simple continued fraction
 */
interface CFSimpleTermMultiExpansion {
    /**
     * Number of terms remaining. Use {@code -1} for unknown or infinite
     */
    long termCount();

    /**
     * Number of terms before the repeating section. {@code 0} if the repeating
     * section starts immediately
     */
    long prefixTerms();

    /**
     * Length of section that repeats. {@code 0} for no repeating section
     */
    long repeatingTerms();

    /**
     * Give term i of the expansion. Starts at zero.
     */
    LargeInt term(long i);
}

/**
 * Continued fraction numbers.
 * <p>
 * See <a href="https://r-knott.surrey.ac.uk/Fibonacci/cfINTRO.html">Continued Fraction</a>
 */
abstract class ContinuedFraction_BITS {
    // These two are used differently depending on what mode we're in.

    /**
     * How to interpret this continued fraction
     */
    private final CfType _type;

    /**
     * Numerator terms. First is the integer part.
     * If this runs out before {@code _under}, a stream of 1s is assumed.
     * <p>
     * When both {@code _over} and {@code _under} are exhausted, we switch
     * to {@code _moreOver} and {@code _moreUnder} if they are supplied.
     */
    private final LargeIntVec _over;

    /**
     * Index {@code _over} returns to when repeating.
     * {@code -1} means no repeating.
     */
    private final int _overRepeat;

    /**
     * Denominator terms. Index 0 matches to {@code _over}'s index 1.
     * If this runs out before {@code _over}, a stream of 1s is assumed.
     * <p>
     * When both {@code _over} and {@code _under} are exhausted, we switch
     * to {@code _moreOver} and {@code _moreUnder} if they are supplied.
     */
    private final LargeIntVec _under;

    /**
     * Index {@code _under} returns to when repeating.
     * {@code -1} means no repeating.
     */
    private final int _underRepeat;

    /**
     * Continued numerator terms. These can be infinite and/or repeating.
     * The indexes should exactly match {@code _moreUnder}.
     * If {@code null}, then this is a finite fraction.
     */
    private final CFSimpleTermMultiExpansion _moreOver;

    /**
     * Continued denominator terms. These can be infinite and/or repeating.
     * The indexes should exactly match {@code _moveOver}.
     * If {@code null}, then this is a finite fraction.
     */
    private final CFSimpleTermMultiExpansion _moreUnder;

    protected ContinuedFraction_BITS() {
        _type = CfType.Zero;
        _over = null;
        _overRepeat = -1;
        _under = null;
        _underRepeat = -1;
        _moreOver = null;
        _moreUnder = null;
    }

    /**
     * return this + other
     */
    public ContinuedFraction_BITS add(ContinuedFraction_BITS other) {
        // TODO: implement
        return null;
    }

    private Fraction element(int i) {
        // TODO: implement
        return null;
    }


    // Bunch of helper functions

    /**
     * 2x2 rational matrix multiply
     */
    private static Fraction[] matMul(Fraction[] mat1, Fraction[] mat2) {
        Fraction[] result = new Fraction[4];

        result[0] = mat1[0].multiply(mat2[0]).add(mat1[1].multiply(mat2[2])).simplify();
        result[1] = mat1[0].multiply(mat2[1]).add(mat1[1].multiply(mat2[3])).simplify();
        result[2] = mat1[2].multiply(mat2[0]).add(mat1[3].multiply(mat2[2])).simplify();
        result[3] = mat1[2].multiply(mat2[1]).add(mat1[3].multiply(mat2[3])).simplify();

        return result;
    }

    private static Fraction fractionSimplify(Fraction frac, int ip) {
        LargeInt p = LargeInt.TEN.pow(ip);
        LargeInt pp = p.multiply(p);
        Fraction f = frac;

        boolean flip = false;
        if (!f.isPositive()) { // negative or zero
            f = f.negate();
            flip = true;
        }
        while (f.overMagnitude(pp)) f.reduce(p);
        while (f.overMagnitude(p)) f.reduce(LargeInt.TEN);

        if (flip) return f.negate();
        return f;
    }

    private static String toDecimalSpecialCase(ContinuedFraction_BITS cf, int precision) {
        LargeInt tenP = LargeInt.TEN.pow(precision + 1);
        Fraction cmp = Fraction.fromVulgarFraction(LargeInt.ONE, tenP);

        Fraction a = Fraction.fromVulgarFraction(1, 1);
        Fraction b = Fraction.fromVulgarFraction(0, 1);
        Fraction c = Fraction.fromVulgarFraction(0, 1);
        Fraction d = Fraction.fromVulgarFraction(1, 1);
        boolean inPrecision = false;
        for (int i = 0; !inPrecision; i++) {
            Fraction newA = a.multiply(cf.element(i)).add(b);
            Fraction newC = c.multiply(cf.element(i)).add(d);
            b = a;
            a = newA;
            d = c;
            c = newC;

            if (!c.isZero() && !d.isZero()) {
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
        /**
         * CF has no terms and is equal to zero
         */
        Zero,

        /**
         * A simple finite continued fraction.
         * CF has a fixed number of 'under' terms and a single 'over' term.
         * '_moreOver' and '_moreUnder' are not used.
         */
        Finite,

        /**
         * A simple repeating continued fraction.
         * CF has a fixed number of '_under' terms and a single '_over' term.
         * Once '_under' is exhausted, it restarts at '_underRepeat'.
         * '_moreOver' and '_moreUnder' are not used.
         */
        Periodic,

        /**
         * A continued fraction based on generators
         */
        Formula, FirstComposite,
        SecondComposite, SqrtComposite, Generalised
    }
}
