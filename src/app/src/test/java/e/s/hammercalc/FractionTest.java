package e.s.hammercalc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
    public void can_create_a_fraction_from_a_float(){
        // Carefully made to be exact doubles. With thanks to float.exposed
        double a = 21870794818600898560.0;
        String A = "21870794818600898560";

        double b = 0.0000361821949101437501215;
        String B = "5339549516259985/147573952589676412928";// "361821949101437501215/100000000000000000000000000", but reduced

        double c = -19595179502422982656.0;
        String C = "-19595179502422982656";

        assertEquals("a", A, Fraction.fromFloat(a).toString());
        assertEquals("b", B, Fraction.fromFloat(b).toString());
        assertEquals("c", C, Fraction.fromFloat(c).toString());

        assertTrue("NaN", Fraction.fromFloat(Double.NaN).isNaN());
        assertFalse("not NaN", Fraction.fromFloat(b).isNaN());
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
    public void modulo_of_rationals(){
        Fraction a = Fraction.fromVulgarFraction(24,2);
        Fraction b = Fraction.fromVulgarFraction(9,10);
        Fraction c = Fraction.fromVulgarFraction(1,1);

        assertEquals("a % b", "3/10", a.modulo(b).toString());
        assertEquals("a % c", "0", a.modulo(c).toString());
        assertEquals("c % b", "1/10", c.modulo(b).toString());
        assertEquals("b % c", "9/10", b.modulo(c).toString());
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

    @Test
    public void powers_of_rationals(){
        Fraction a = Fraction.fromVulgarFraction(3,5);
        Fraction expected1 = Fraction.fromVulgarFraction(27,125);
        Fraction expected2 = Fraction.fromVulgarFraction(125,27);
        Fraction expected3 = Fraction.fromVulgarFraction(3125,243);

        assertEquals("(3/5)**0", Fraction.ONE, a.pow(0));  // x -> 1
        assertEquals("(3/5)**3", expected1, a.pow(3));     // 0.6 -> 0.2
        assertEquals("(3/5)**(-3)", expected2, a.pow(-3)); // 0.6 -> 2.7
        assertEquals("(3/5)**(-5)", expected3, a.pow(-5)); // 0.6 -> 13
    }

    @Test
    public void mantissa_gives_the_fractional_part_of_a_vulgar_fraction(){
        Fraction a = Fraction.fromVulgarFraction(11, 63); // all fractional part
        Fraction b = Fraction.fromVulgarFraction(128, 64); // no fractional part
        Fraction c = Fraction.fromVulgarFraction(128, 65); // mixed- a true 'vulgar' fraction
        Fraction d = Fraction.fromVulgarFraction(-25, 11);

        assertEquals("m(11/63)", "11/63", a.mantissa().toString());
        assertEquals("m(128/64)", "0", b.mantissa().toString());
        assertEquals("m(128/65)", "63/65", c.mantissa().toString());
        assertEquals("m(-25/11)", "-3/11", d.mantissa().toString());
    }

    @Test
    public void truncate_gives_the_integer_part_of_a_vulgar_fraction(){
        Fraction a = Fraction.fromVulgarFraction(11, 63); // all fractional part
        Fraction b = Fraction.fromVulgarFraction(128, 64); // no fractional part
        Fraction c = Fraction.fromVulgarFraction(128, 65); // mixed- a true 'vulgar' fraction
        Fraction d = Fraction.fromVulgarFraction(-25, 11);

        assertEquals("t(11/63)", Fraction.ZERO, a.truncate());
        assertEquals("t(128/64)", "2", b.truncate().toString());
        assertEquals("t(128/65)", Fraction.ONE, c.truncate());
        assertEquals("t(-25/11)", "-2", d.truncate().toString());
    }

    @Test
    public void truncateToInt_gives_the_integer_part_of_a_vulgar_fraction(){
        Fraction a = Fraction.fromVulgarFraction(11, 63); // all fractional part
        Fraction b = Fraction.fromVulgarFraction(128, 64); // no fractional part
        Fraction c = Fraction.fromVulgarFraction(128, 65); // mixed- a true 'vulgar' fraction
        Fraction d = Fraction.fromVulgarFraction(-25, 11);

        assertEquals("t(11/63)", LargeInt.ZERO, a.truncateToInt());
        assertEquals("t(128/64)", LargeInt.TWO, b.truncateToInt());
        assertEquals("t(128/65)", LargeInt.ONE, c.truncateToInt());
        assertEquals("t(-25/11)", LargeInt.fromInt(-2), d.truncateToInt());
    }

    @Test
    public void simplify_reduces_fractions_by_their_greatest_common_denominator(){
        Fraction a = Fraction.fromVulgarFraction(55, 315); // 11/63
        Fraction b = Fraction.fromVulgarFraction(32, 128); // 1/4
        Fraction c = Fraction.fromVulgarFraction("15360", "7800"); // 128/65
        Fraction d = Fraction.fromVulgarFraction(-675, 297); // -25/11

        assertEquals("55/315", "11/63", a.simplify().toString()); // 'toString' does a simplify anyway
        assertEquals("32/128", "1/4", b.simplify().toString());
        assertEquals("15360/7800", "128/65", c.simplify().toString());
        assertEquals("-675/297", "-25/11", d.simplify().toString());
    }

    @Test
    public void can_reduce_rational_by_integer(){
        Fraction a = Fraction.fromVulgarFraction(11,21);
        Fraction aDiv3 = Fraction.fromVulgarFraction(11,63);
        Fraction aReduce3 = Fraction.fromVulgarFraction(3, 7);

        assertEquals("(33/63) / 3 (divide)", aDiv3, a.divide(Fraction.fromInteger(3)));
        assertEquals("(floor(33/3))/(floor(63/3)) (reduce)", aReduce3, a.reduce(LargeInt.fromInt(3)));
    }

    @Test
    public void rationals_can_be_approximated_to_decimal_strings_with_truncation_to_a_specified_precision(){
        Fraction a = Fraction.fromVulgarFraction(1,3);           // decimal's nemesis 0.3 ̅
        Fraction b = Fraction.fromVulgarFraction(456456,987987); // 152/329 -> 0.46200607902735562...
        Fraction c = Fraction.fromVulgarFraction(5,8);           // exactly 0.625
        Fraction d = Fraction.fromVulgarFraction(29,8);          // exactly 3.625
        Fraction e = Fraction.fromVulgarFraction("859659751656285302520311", "3"); // 2.865532505520951e23

        assertEquals("1/3: 4", "0.3333", a.toDecimalString(4));
        assertEquals("152/329: 3", "0.462", b.toDecimalString(3));
        assertEquals("152/329: 8", "0.46200607", b.toDecimalString(8)); // output is truncated, not rounded
        assertEquals("5/8: 10", "0.625", c.toDecimalString(10)); // stop outputting digits if we get an exact value
        assertEquals("29/8: 10", "3.625", d.toDecimalString(10));
        assertEquals("859659751656285302520311/3: 30", "286553250552095100840103.66", e.toDecimalString(2));
    }

    @Test
    public void rationals_can_be_approximated_to_floating_rationals(){
        Fraction a = Fraction.fromVulgarFraction("859659751656285302520311", "3");
        Fraction b = Fraction.fromVulgarFraction("859659751656285302520311", "618687072559368554063371");
        Fraction c = Fraction.fromVulgarFraction("5", "483702679370754995920693");

        assertEquals("a", "8596e20/3", a.toFloatString(4));
        assertEquals("b", "859659e18/618687e18", b.toFloatString(6));
        assertEquals("c", "5/4837026793e14", c.toFloatString(10));
    }

    @Test
    public void can_express_a_rational_fraction_as_a_continued_fraction(){
        Fraction f1 = Fraction.fromVulgarFraction(5,7); // should be [0;1,2,2] (or [0;1,2,1,1])
        Fraction f2 = Fraction.fromVulgarFraction(6,8); // should be [0;1,3] (or [0;1,2,1])
        Fraction f3 = Fraction.fromVulgarFraction(47,17); // should be [2;1,3,4]
        Fraction f4 = Fraction.fromVulgarFraction(-17,12); // should be [-2;1,1,2,2]

        LargeInt[] expectedF1 = LargeInt.arrayFromInts(0,1,2,2);
        LargeInt[] expectedF2 = LargeInt.arrayFromInts(0,1,3);
        LargeInt[] expectedF3 = LargeInt.arrayFromInts(2,1,3,4);
        LargeInt[] expectedF4 = LargeInt.arrayFromInts(-2,1,1,2,2);

        assertArrayEquals("f1",expectedF1, f1.toContinuedFractionArray());
        assertArrayEquals("f2",expectedF2, f2.toContinuedFractionArray());
        assertArrayEquals("f3",expectedF3, f3.toContinuedFractionArray());
        assertArrayEquals("f4",expectedF4, f4.toContinuedFractionArray());
    }

    @Test
    public void can_restore_a_rational_fraction_from_a_rational_continued_fraction(){
        LargeInt[] cf1 = LargeInt.arrayFromInts(0,1,2,2);
        LargeInt[] cf2 = LargeInt.arrayFromInts(0,1,3);
        LargeInt[] cf3 = LargeInt.arrayFromInts(2,1,3,4);
        LargeInt[] cf4 = LargeInt.arrayFromInts(-2,1,1,2,2);

        Fraction expectedF1 = Fraction.fromVulgarFraction(5,7);
        Fraction expectedF2 = Fraction.fromVulgarFraction(6,8);
        Fraction expectedF3 = Fraction.fromVulgarFraction(47,17);
        Fraction expectedF4 = Fraction.fromVulgarFraction(-17,12);

        assertEquals("cf1",expectedF1, Fraction.continuedFractionToFraction(cf1));
        assertEquals("cf2",expectedF2, Fraction.continuedFractionToFraction(cf2));
        assertEquals("cf3",expectedF3, Fraction.continuedFractionToFraction(cf3));
        assertEquals("cf4",expectedF4, Fraction.continuedFractionToFraction(cf4));
    }

    @Test
    public void can_convert_a_floating_point_to_a_continued_fraction(){
        LargeInt[] cf1 = Fraction.floatToContinuedFraction(0.1875, 3); // 3/16
        Fraction f1 = Fraction.continuedFractionToFraction(cf1);
        assertEquals("1", "3/16", f1.toString());
        assertEquals("2", "0.1875", f1.toDecimalString(4));
    }

    @Test
    public void converting_floating_points_to_continued_fraction_exposes_floating_point_problems(){
        LargeInt[] cf1 = Fraction.floatToContinuedFraction(0.1875, 5); // 0.1875 stores as approx. 0.18750000000000001
        Fraction f1 = Fraction.continuedFractionToFraction(cf1);

        assertNotEquals("1", "3/16", f1.toString());
        assertEquals("2", "0.1875", f1.toDecimalString(4));
    }

}
