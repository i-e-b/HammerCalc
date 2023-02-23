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

    @Test
    public void positivity_is_correct_with_respect_to_both_num_and_den(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(3,-5);
        Fraction c = Fraction.fromVulgarFraction(-3,5);
        Fraction d = Fraction.fromVulgarFraction(-3,-5);

        assertTrue("a > 0", a.isPositive());
        assertFalse("b < 0", b.isPositive());
        assertFalse("c < 0", c.isPositive());
        assertTrue("d < 0", d.isPositive());
    }

    @Test
    public void absolute_value_gives_positive_num_and_den(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(3,-5);
        Fraction c = Fraction.fromVulgarFraction(-3,5);
        Fraction d = Fraction.fromVulgarFraction(-3,-5);

        assertTrue("a", a.abs().equals(a));
        assertTrue("b", b.abs().equals(a));
        assertTrue("c", c.abs().equals(a));
        assertTrue("d", d.abs().equals(a));
    }

    @Test
    public void zero_values_ignore_denominator(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(0,5);
        Fraction c = Fraction.fromVulgarFraction(0,-5);
        Fraction d = Fraction.fromVulgarFraction(0,-12);

        assertFalse("a", a.isZero());
        assertTrue("b", b.isZero());
        assertTrue("c", c.isZero());
        assertTrue("d", d.isZero());
    }

    @Test
    public void rationals_are_integers_if_the_simplified_denominator_is_1(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(5,5);
        Fraction c = Fraction.fromVulgarFraction(7,8);
        Fraction d = Fraction.fromVulgarFraction(-12, 2);

        assertFalse("a", a.isInteger());
        assertTrue("b", b.isInteger());
        assertFalse("c", c.isInteger());
        assertTrue("d", d.isInteger());
    }

    @Test
    public void adding_rationals(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(-3,5);
        Fraction c = Fraction.fromVulgarFraction(1,10);

        assertEquals("a+b=0", "0", a.add(b).toString());
        assertEquals("a+c=7/10", "7/10", a.add(c).toString());
        assertEquals("c+b=-5/10", "-1/2", c.add(b).toString());
        assertEquals("b+c=-5/10", "-1/2", b.add(c).toString());
    }

    @Test
    public void subtracting_rationals(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(-3,5);
        Fraction c = Fraction.fromVulgarFraction(1,10);

        assertEquals("a-b=6/5", "6/5", a.subtract(b).toString());
        assertEquals("a-c=1/2", "1/2", a.subtract(c).toString());
        assertEquals("c-b=7/10", "7/10", c.subtract(b).toString());
        assertEquals("b-c=-7/10", "-7/10", b.subtract(c).toString());
    }

    @Test
    public void multiplying_rationals(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(-3,5);
        Fraction c = Fraction.fromVulgarFraction(1,10);

        assertEquals("a*b", "-9/25", a.multiply(b).toString());
        assertEquals("a*c", "3/50", a.multiply(c).toString());
        assertEquals("c*b", "-3/50", c.multiply(b).toString());
        assertEquals("b*c", "-3/50", b.multiply(c).toString());
    }

    @Test
    public void dividing_rationals(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction b = Fraction.fromVulgarFraction(-3,5);
        Fraction c = Fraction.fromVulgarFraction(1,10);

        assertEquals("a/b", "-1", a.divide(b).toString());
        assertEquals("a/c", "6", a.divide(c).toString());
        assertEquals("c/b", "-1/6", c.divide(b).toString());
        assertEquals("b/c", "-6", b.divide(c).toString());
    }

    @Test
    public void can_handle_very_large_and_very_small_rationals(){
        Fraction a = Fraction.fromVulgarFraction(new LargeInt("724675358467671744377633"), new LargeInt("5"));
        Fraction b = Fraction.fromVulgarFraction(new LargeInt("11"), new LargeInt("859659751656285302520311"));

        assertEquals("a*b", "7971428943144389188153963/4298298758281426512601555", a.multiply(b).toString());
        assertEquals("a/b", "622974238691748220182382403184797428439036603863/55", a.divide(b).toString());
        assertEquals("b/a", "55/622974238691748220182382403184797428439036603863", b.divide(a).toString());
    }

    @Test
    public void inverse_of_rationals(){
        Fraction a = Fraction.fromVulgarFraction(new LargeInt("1"), new LargeInt("50"));
        Fraction b = Fraction.fromVulgarFraction(new LargeInt("7246753584"), new LargeInt("27"));

        assertEquals("inv(a) = 1/a", Fraction.ONE.divide(a), a.inverse());
        assertEquals("inv(a) = 50", "50", a.inverse().toString());
        assertEquals("inv(b) = 1/b", Fraction.ONE.divide(b), b.inverse());
        assertEquals("inv(b) = 9/2415584528", "9/2415584528", b.inverse().toString());
        assertEquals("inv(b) = 9/24e8", "9/24e8", b.inverse().toFloatString(2));
    }
}
