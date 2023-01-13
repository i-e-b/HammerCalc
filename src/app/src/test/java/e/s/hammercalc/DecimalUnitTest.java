package e.s.hammercalc;

import org.junit.Test;

import static org.junit.Assert.*;

import e.s.hammercalc.core.Decimal;

public class DecimalUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
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
    public void can_create_decimal_from_a_string(){
        Decimal zero = new Decimal("0.0");

        Decimal decimalBelow = new Decimal("543209.99");
        Decimal integral = new Decimal("543210");
        Decimal decimalAbove = new Decimal("543210.99");

        Decimal otherZero = new Decimal();

        assertEquals("zeros are equal", 0, zero.cmp(otherZero));
        assertEquals("lower", -1, integral.cmp(decimalAbove));
        assertEquals("higher", 1, integral.cmp(decimalBelow));
    }
}
