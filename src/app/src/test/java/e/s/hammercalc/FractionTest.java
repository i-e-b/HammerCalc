package e.s.hammercalc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import e.s.hammercalc.core.Fraction;
import e.s.hammercalc.core.LargeInt;

public class FractionTest {

    @Test
    public void can_create_a_fraction_from_int_components(){
        Fraction x = Fraction.fromVulgarFraction(LargeInt.ONE,LargeInt.TWO);
        Fraction y = Fraction.fromVulgarFraction(3,4);

        assertEquals("x < y", -1, x.compareTo(y));
        assertEquals("y < x", 1, y.compareTo(x));
    }

    @Test
    public void can_create_integer_rationals(){
        Fraction x = Fraction.fromInteger(LargeInt.TWO);
        Fraction y = Fraction.fromVulgarFraction(5,2);
        Fraction z = Fraction.fromInteger(3);

        assertEquals("x < y", -1, x.compareTo(y));
        assertEquals("y < z", -1, y.compareTo(z));
    }

    @Test
    public void fractional_constants_are_correct(){
        assertEquals("(-1) < (-1/2)", -1, Fraction.NEG_ONE.compareTo(Fraction.NEG_HALF));
        assertEquals("(-1/2) < 0", -1, Fraction.NEG_HALF.compareTo(Fraction.ZERO));
        assertEquals("0 < 1/2", -1, Fraction.ZERO.compareTo(Fraction.HALF));
        assertEquals("1/2 < 1", -1, Fraction.HALF.compareTo(Fraction.ONE));
    }

    @Test
    public void equality_works_on_equivalent_value(){
        Fraction x = Fraction.fromVulgarFraction(1000,2001);
        Fraction a = Fraction.fromVulgarFraction(10,20);
        Fraction b = Fraction.fromVulgarFraction(1,2);

        assertTrue("a=b", a.equals(b));
        assertFalse("a=x", a.equals(x));
    }
}
