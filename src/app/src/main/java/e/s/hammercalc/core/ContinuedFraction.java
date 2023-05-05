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

    private LargeInt[] _components;

    public ContinuedFraction(LargeInt[] components) {
        this._components = components;
    }

    public ContinuedFraction(LargeInt prefix, LargeInt[] components) {
        this._components = new LargeInt[components.length + 1];
        _components[0] = prefix;
        for (int i = 1; i <= components.length; i++) {
            _components[i] = components[i-1];
        }
    }

    public ContinuedFraction(LargeInt[] components, int offset) {
        this._components = new LargeInt[components.length - offset];
        for (int i = 0; i < _components.length; i++) {
            _components[i] = components[i+offset];
        }
    }

    public ContinuedFraction(Fraction f) {
        this(f.toContinuedFractionArray());
    }

    /** Convert continued fraction to a rational */
    public Fraction toFraction() {
        return Fraction.continuedFractionToFraction(_components);
    }

    /** Create a new CF, with reciprocal of the value of this */
    public ContinuedFraction invert() {
        if (_components[0].isZero()){
            return new ContinuedFraction(_components, 1);
        } else {
            return new ContinuedFraction(LargeInt.ZERO, _components);
        }
    }
}