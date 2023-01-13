package e.s.hammercalc;

import org.junit.Test;

import static org.junit.Assert.*;

import e.s.hammercalc.core.Decimal;

public class DecimalUnitTest {
    @Test
    public void digit_numeral_conversion(){
        assertEquals("0", 0, Decimal.Const.NUMERALS('0'));
        assertEquals("1", 1, Decimal.Const.NUMERALS('1'));
        assertEquals("2", 2, Decimal.Const.NUMERALS('2'));
        assertEquals("3", 3, Decimal.Const.NUMERALS('3'));
        assertEquals("4", 4, Decimal.Const.NUMERALS('4'));
        assertEquals("5", 5, Decimal.Const.NUMERALS('5'));
        assertEquals("6", 6, Decimal.Const.NUMERALS('6'));
        assertEquals("7", 7, Decimal.Const.NUMERALS('7'));
        assertEquals("8", 8, Decimal.Const.NUMERALS('8'));
        assertEquals("9", 9, Decimal.Const.NUMERALS('9'));

        assertEquals("a", 10, Decimal.Const.NUMERALS('a'));
        assertEquals("b", 11, Decimal.Const.NUMERALS('b'));
        assertEquals("c", 12, Decimal.Const.NUMERALS('c'));
        assertEquals("d", 13, Decimal.Const.NUMERALS('d'));
        assertEquals("e", 14, Decimal.Const.NUMERALS('e'));
        assertEquals("f", 15, Decimal.Const.NUMERALS('f'));

        assertEquals("A", 10, Decimal.Const.NUMERALS('A'));
        assertEquals("B", 11, Decimal.Const.NUMERALS('B'));
        assertEquals("C", 12, Decimal.Const.NUMERALS('C'));
        assertEquals("D", 13, Decimal.Const.NUMERALS('D'));
        assertEquals("E", 14, Decimal.Const.NUMERALS('E'));
        assertEquals("F", 15, Decimal.Const.NUMERALS('F'));
    }

    @Test
    public void decimal_finite_tests_and_nan_tests() {
        Decimal zero = new Decimal("0.0");
        Decimal otherZero = new Decimal();
        Decimal integral = new Decimal("543210");
        Decimal decimal = new Decimal("10.99e-3");
        Decimal decNaN = Decimal.decimalNaN();
        Decimal posInf = new Decimal("+Infinity");
        Decimal negInf = new Decimal("-Infinity");

        assertTrue("zero zero-check", zero.isZero());
        assertTrue("zero finite-check", zero.isFinite());
        assertFalse("zero nan-check", zero.isNaN());

        assertTrue("other zero zero-check", otherZero.isZero());
        assertTrue("other zero finite-check", otherZero.isFinite());
        assertFalse("other zero nan-check", otherZero.isNaN());

        assertTrue("nan nan-check", decNaN.isNaN());
        assertFalse("nan finite-check", decNaN.isFinite());
        assertFalse("nan zero-check", decNaN.isZero());

        assertFalse("int zero-check", integral.isZero());
        assertTrue("int finite-check", integral.isFinite());
        assertFalse("int nan-check", integral.isNaN());

        assertFalse("dec zero-check", decimal.isZero());
        assertTrue("dec finite-check", decimal.isFinite());
        assertFalse("dec nan-check", decimal.isNaN());

        assertFalse("posInf zero-check", posInf.isZero());
        assertFalse("posInf finite-check", posInf.isFinite());
        assertFalse("posInf nan-check", posInf.isNaN());

        assertFalse("negInf zero-check", negInf.isZero());
        assertFalse("negInf finite-check", negInf.isFinite());
        assertFalse("negInf nan-check", negInf.isNaN());
    }

    @Test
    public void can_create_decimal_with_double_value_and_compare(){
        Decimal d = new Decimal(123.4);

        Decimal l = new Decimal(120);
        Decimal h = new Decimal(130);
        Decimal eq = new Decimal(123.40);

        assertEquals("lower bound", -1, d.cmp(l));
        assertEquals("upper bound", 1, d.cmp(h));
        assertEquals("equality", 0, d.cmp(eq));
    }

    @Test
    public void can_create_decimal_from_a_dec_string(){
        Decimal zero = new Decimal("0.0");

        Decimal strPoint3 = new Decimal("0.3");
        Decimal constPoint3 = new Decimal(0.3);

        System.out.println(strPoint3.toRawString());
        System.out.println(constPoint3.toRawString());

        Decimal decimalBelow = new Decimal("543209.99");
        Decimal integral = new Decimal("543210");
        Decimal decimalAbove = new Decimal("543210.99");

        Decimal otherZero = new Decimal();

        assertEquals("zeros are equal", 0, zero.cmp(otherZero));
        assertEquals("lower", -1, integral.cmp(decimalAbove));
        assertEquals("higher", 1, integral.cmp(decimalBelow));
    }

    @Test
    public void can_create_decimal_from_a_huge_dec_string(){
        Decimal veryLargeNumber = new Decimal("302585092994045684017991454684364207601101488628772976033327900967572609677");

        System.out.println(veryLargeNumber.toRawString());

        // TODO: tests
        /*
        assertEquals("zeros are equal", 0, zero.cmp(otherZero));
        assertEquals("lower", -1, integral.cmp(decimalAbove));
        assertEquals("higher", 1, integral.cmp(decimalBelow));
         */
    }

    @Test
    public void can_create_decimal_from_a_hex_string(){
        Decimal zero = new Decimal("0x0");

        Decimal intHex = new Decimal("0xFACEfeed");
        Decimal fracHex = new Decimal("0x1.8");
        Decimal fracHexWithExponent = new Decimal("0x1.8p-5");

        System.out.println(zero.toRawString());
        System.out.println(intHex.toRawString());
        System.out.println(fracHex.toRawString());
        System.out.println(fracHexWithExponent.toRawString());
        /*
        assertEquals("zeros are equal", 0, zero.);
        assertEquals("lower", -1, integral.cmp(decimalAbove));
        assertEquals("higher", 1, integral.cmp(decimalBelow));*/
    }
}
