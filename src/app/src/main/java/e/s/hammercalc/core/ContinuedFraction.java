package e.s.hammercalc.core;

/**
 * Continued fraction numbers.
 *
 * See <a href="https://r-knott.surrey.ac.uk/Fibonacci/cfINTRO.html">Continued Fraction</a>
 * */
public abstract class ContinuedFraction {
    // These two are used differently depending on what mode we're in.
    /*private final LargeIntVec _over = new LargeIntVec();
    private final LargeIntVec _under = new LargeIntVec();

    private final CfType _type;*/

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
        Finite, Periodic, Formula, FirstComposite, SecondComposite,
        SqrtComposite, Generalised
    }
}
