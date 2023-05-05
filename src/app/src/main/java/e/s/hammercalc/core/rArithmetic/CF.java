package e.s.hammercalc.core.rArithmetic;

import e.s.hammercalc.core.Fraction;
import e.s.hammercalc.core.LargeInt;

/**
 * Public interface to continued fractions.
 * There are various different concretes, and they will convert between themselves.
 */
public abstract class CF {
    public static CF make_cf_from_integer_continued_fraction_array(LargeInt[] cfList){
        // Example: 10/7 = 1 + 1/(2 + 1/3):: make_cf_from_integer_continued_fraction_array([1,2,3])  ->  FiniteCF { frac: Fraction { num: 10n, den: 7n } }
        // If you wish to make a continued fraction from an infinitely repeating array, use make_cf_from_repeating_pattern instead.
        return  new FiniteCF(Fraction.continuedFractionToFraction(cfList));
    }

    public static CF make_cf_from_Fraction(Fraction frac){
        return new FiniteCF(frac);
    }

}
