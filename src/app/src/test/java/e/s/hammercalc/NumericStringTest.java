package e.s.hammercalc;

import org.junit.Test;

import static org.junit.Assert.*;

import e.s.hammercalc.core.Decimal;

public class NumericStringTest {
    @Test
    public void null_strings_are_invalid() {
        Decimal.NumericString ns = new Decimal.NumericString(null);

        assertFalse("invalid", ns.valid);
        assertEquals("default base", 10, ns.baseSize);
        assertEquals("default dp", -1, ns.decimalPosition);
    }

    @Test
    public void empty_strings_are_invalid() {
        Decimal.NumericString ns = new Decimal.NumericString("");

        assertFalse("invalid", ns.valid);
        assertEquals("default base", 10, ns.baseSize);
        assertEquals("default dp", -1, ns.decimalPosition);
    }

    @Test
    public void integer_base_2_one_char() {
        Decimal.NumericString ns = new Decimal.NumericString("0b1");

        assertTrue("valid", ns.valid);
        assertEquals("base", 2, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void integer_base_2_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("0b110001010010010");

        assertTrue("valid", ns.valid);
        assertEquals("base", 2, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "110001010010010", ns.mantissa);
    }
    @Test
    public void positive_integer_base_2_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("+0b110001010010010");

        assertTrue("valid", ns.valid);
        assertEquals("base", 2, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "110001010010010", ns.mantissa);
    }
    @Test
    public void negative_integer_base_2_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("-0b110001010010010");

        assertTrue("valid", ns.valid);
        assertEquals("base", 2, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "110001010010010", ns.mantissa);
    }
    @Test
    public void base_2_fractions_allowed() {
        Decimal.NumericString ns = new Decimal.NumericString("0b1100010.10010010");

        assertTrue("valid", ns.valid);
        assertEquals("base", 2, ns.baseSize);
        assertEquals("dp", 7, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "110001010010010", ns.mantissa);
    }
    @Test
    public void base_2_exponent_with__p__character() {
        Decimal.NumericString ns = new Decimal.NumericString("0b110.0010p10010010");

        assertTrue("valid", ns.valid);
        assertEquals("base", 2, ns.baseSize);
        assertEquals("dp", 3, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "10010010", ns.exponent);
        assertEquals("mantissa", "1100010", ns.mantissa);
    }

    @Test
    public void integer_base_8_one_char() {
        Decimal.NumericString ns = new Decimal.NumericString("0o3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 8, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "3", ns.mantissa);
    }
    @Test
    public void integer_base_8_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("0o12475301");

        assertTrue("valid", ns.valid);
        assertEquals("base", 8, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12475301", ns.mantissa);
    }
    @Test
    public void positive_integer_base_8_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("+0o12475301");

        assertTrue("valid", ns.valid);
        assertEquals("base", 8, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12475301", ns.mantissa);
    }
    @Test
    public void negative_integer_base_8_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("-0o12475301");

        assertTrue("valid", ns.valid);
        assertEquals("base", 8, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12475301", ns.mantissa);
    }
    @Test
    public void base_8_fractions_allowed() {
        Decimal.NumericString ns = new Decimal.NumericString("0o12475.301");

        assertTrue("valid", ns.valid);
        assertEquals("base", 8, ns.baseSize);
        assertEquals("dp", 5, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12475301", ns.mantissa);
    }
    @Test
    public void base_8_exponent_with__p__character() {
        Decimal.NumericString ns = new Decimal.NumericString("0o12.47p-5301");

        assertTrue("valid", ns.valid);
        assertEquals("base", 8, ns.baseSize);
        assertEquals("dp", 2, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "-5301", ns.exponent);
        assertEquals("mantissa", "1247", ns.mantissa);
    }

    @Test
    public void integer_base_16_one_char() {
        Decimal.NumericString ns = new Decimal.NumericString("0xA");

        assertTrue("valid", ns.valid);
        assertEquals("base", 16, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "A", ns.mantissa);
    }
    @Test
    public void integer_base_16_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("0xc0654bd013");

        assertTrue("valid", ns.valid);
        assertEquals("base", 16, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "C0654BD013", ns.mantissa);
    }
    @Test
    public void positive_integer_base_16_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("+0xc0654bd013");

        assertTrue("valid", ns.valid);
        assertEquals("base", 16, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "C0654BD013", ns.mantissa);
    }
    @Test
    public void negative_integer_base_16_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("-0xc0654bd013");

        assertTrue("valid", ns.valid);
        assertEquals("base", 16, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "C0654BD013", ns.mantissa);
    }
    @Test
    public void base_16_fractions_allowed() {
        Decimal.NumericString ns = new Decimal.NumericString("0xc0654b.d013");

        assertTrue("valid", ns.valid);
        assertEquals("base", 16, ns.baseSize);
        assertEquals("dp", 6, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "C0654BD013", ns.mantissa);
    }
    @Test
    public void base_16_exponent_done_with__p__character() {
        Decimal.NumericString ns = new Decimal.NumericString("0xc0654p+bd013");

        assertTrue("valid", ns.valid);
        assertEquals("base", 16, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "+BD013", ns.exponent);
        assertEquals("mantissa", "C0654", ns.mantissa);
    }

    @Test
    public void integer_base_10_one_char() {
        Decimal.NumericString ns = new Decimal.NumericString("1");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void integer_base_10_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("123456");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "123456", ns.mantissa);
    }
    @Test
    public void integer_base_10_longer_than_int64() {
        Decimal.NumericString ns = new Decimal.NumericString("9223372036854775807999999");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }

    @Test
    public void positive_integer_base_10_one_char() {
        Decimal.NumericString ns = new Decimal.NumericString("+1");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_integer_base_10_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("+123456");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "123456", ns.mantissa);
    }
    @Test
    public void positive_integer_base_10_longer_than_int64() {
        Decimal.NumericString ns = new Decimal.NumericString("+9223372036854775807999999");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }

    @Test
    public void negative_integer_base_10_one_char() {
        Decimal.NumericString ns = new Decimal.NumericString("-1");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_integer_base_10_multi_char() {
        Decimal.NumericString ns = new Decimal.NumericString("-123456");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "123456", ns.mantissa);
    }
    @Test
    public void negative_integer_base_10_longer_than_int64() {
        Decimal.NumericString ns = new Decimal.NumericString("-9223372036854775807999999");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }


    @Test
    public void fractional_base_10_short() {
        Decimal.NumericString ns = new Decimal.NumericString("1.2");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12", ns.mantissa);
    }
    @Test
    public void fractional_base_10_no_prefix() {
        Decimal.NumericString ns = new Decimal.NumericString(".2");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 0, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "2", ns.mantissa);
    }
    @Test
    public void fractional_base_10_no_postfix() {
        Decimal.NumericString ns = new Decimal.NumericString("1.");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void fractional_base_10_long() {
        Decimal.NumericString ns = new Decimal.NumericString("9223372036854775807999999.9223372036854775807999999");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 25, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "92233720368547758079999999223372036854775807999999", ns.mantissa);
    }

    @Test
    public void positive_fractional_base_10_short() {
        Decimal.NumericString ns = new Decimal.NumericString("+1.2");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12", ns.mantissa);
    }
    @Test
    public void positive_fractional_base_10_no_prefix() {
        Decimal.NumericString ns = new Decimal.NumericString("+.2");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 0, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "2", ns.mantissa);
    }
    @Test
    public void positive_fractional_base_10_no_postfix() {
        Decimal.NumericString ns = new Decimal.NumericString("+1.");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_fractional_base_10_long() {
        Decimal.NumericString ns = new Decimal.NumericString("+9223372036854775807999999.9223372036854775807999999");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 25, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "92233720368547758079999999223372036854775807999999", ns.mantissa);
    }

    @Test
    public void negative_fractional_base_10_short() {
        Decimal.NumericString ns = new Decimal.NumericString("-1.2");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "12", ns.mantissa);
    }
    @Test
    public void negative_fractional_base_10_no_prefix() {
        Decimal.NumericString ns = new Decimal.NumericString("-.2");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 0, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "2", ns.mantissa);
    }
    @Test
    public void negative_fractional_base_10_no_postfix() {
        Decimal.NumericString ns = new Decimal.NumericString("-1.");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_fractional_base_10_long() {
        Decimal.NumericString ns = new Decimal.NumericString("-9223372036854775807999999.9223372036854775807999999");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 25, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "", ns.exponent);
        assertEquals("mantissa", "92233720368547758079999999223372036854775807999999", ns.mantissa);
    }


    @Test
    public void integer_exponent_short_short(){
        Decimal.NumericString ns = new Decimal.NumericString("1e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void integer_exponent_short_long(){
        Decimal.NumericString ns = new Decimal.NumericString("1e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void integer_exponent_long_short(){
        Decimal.NumericString ns = new Decimal.NumericString("9223372036854775807999999e3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "3", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void integer_exponent_long_long(){
        Decimal.NumericString ns = new Decimal.NumericString("9223372036854775807999999e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void integer_exponent_positive_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("1e+5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "+5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void integer_exponent_negative_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("1e-5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "-5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }

    @Test
    public void positive_integer_exponent_short_short(){
        Decimal.NumericString ns = new Decimal.NumericString("+1e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_integer_exponent_short_long(){
        Decimal.NumericString ns = new Decimal.NumericString("+1e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_integer_exponent_long_short(){
        Decimal.NumericString ns = new Decimal.NumericString("+9223372036854775807999999e3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "3", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void positive_integer_exponent_long_long(){
        Decimal.NumericString ns = new Decimal.NumericString("+9223372036854775807999999e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void positive_integer_exponent_positive_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("+1e+5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "+5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_integer_exponent_negative_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("+1e-5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "-5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }

    @Test
    public void negative_integer_exponent_short_short(){
        Decimal.NumericString ns = new Decimal.NumericString("-1e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_integer_exponent_short_long(){
        Decimal.NumericString ns = new Decimal.NumericString("-1e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_integer_exponent_long_short(){
        Decimal.NumericString ns = new Decimal.NumericString("-9223372036854775807999999e3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "3", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void negative_integer_exponent_long_long(){
        Decimal.NumericString ns = new Decimal.NumericString("-9223372036854775807999999e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void negative_integer_exponent_positive_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("-1e+5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "+5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_integer_exponent_negative_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("-1e-5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", -1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "-5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }


    @Test
    public void fractional_exponent_short_short(){
        Decimal.NumericString ns = new Decimal.NumericString("1.0e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "10", ns.mantissa);
    }
    @Test
    public void fractional_exponent_no_prefix_short(){
        Decimal.NumericString ns = new Decimal.NumericString(".1e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 0, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void fractional_exponent_no_postfix_short(){
        Decimal.NumericString ns = new Decimal.NumericString("1.e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void fractional_exponent_short_long(){
        Decimal.NumericString ns = new Decimal.NumericString("1.0e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "10", ns.mantissa);
    }
    @Test
    public void fractional_exponent_long_short(){
        Decimal.NumericString ns = new Decimal.NumericString("9223372036854.775807999999e3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 13, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "3", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void fractional_exponent_long_long(){
        Decimal.NumericString ns = new Decimal.NumericString("9223372036854.775807999999e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 13, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void fractional_exponent_positive_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("1.5e+5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "+5", ns.exponent);
        assertEquals("mantissa", "15", ns.mantissa);
    }
    @Test
    public void fractional_exponent_negative_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("1.8e-5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "-5", ns.exponent);
        assertEquals("mantissa", "18", ns.mantissa);
    }

    @Test
    public void positive_fractional_exponent_short_short(){
        Decimal.NumericString ns = new Decimal.NumericString("+1.0e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "10", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_no_prefix_short(){
        Decimal.NumericString ns = new Decimal.NumericString("+.1e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 0, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_no_postfix_short(){
        Decimal.NumericString ns = new Decimal.NumericString("+1.e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_short_long(){
        Decimal.NumericString ns = new Decimal.NumericString("+1.0e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "10", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_long_short(){
        Decimal.NumericString ns = new Decimal.NumericString("+9223372036854.775807999999e3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 13, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "3", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_long_long(){
        Decimal.NumericString ns = new Decimal.NumericString("+9223372036854.775807999999e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 13, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_positive_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("+1.5e+5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "+5", ns.exponent);
        assertEquals("mantissa", "15", ns.mantissa);
    }
    @Test
    public void positive_fractional_exponent_negative_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("+1.8e-5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", 1, ns.sign);
        assertEquals("exponent", "-5", ns.exponent);
        assertEquals("mantissa", "18", ns.mantissa);
    }

    @Test
    public void negative_fractional_exponent_short_short(){
        Decimal.NumericString ns = new Decimal.NumericString("-1.0e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "10", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_no_prefix_short(){
        Decimal.NumericString ns = new Decimal.NumericString("-.1e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 0, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_no_postfix_short(){
        Decimal.NumericString ns = new Decimal.NumericString("-1.e5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5", ns.exponent);
        assertEquals("mantissa", "1", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_short_long(){
        Decimal.NumericString ns = new Decimal.NumericString("-1.0e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "10", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_long_short(){
        Decimal.NumericString ns = new Decimal.NumericString("-9223372036854.775807999999e3");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 13, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "3", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_long_long(){
        Decimal.NumericString ns = new Decimal.NumericString("-9223372036854.775807999999e5000000000");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 13, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "5000000000", ns.exponent);
        assertEquals("mantissa", "9223372036854775807999999", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_positive_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("-1.5e+5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "+5", ns.exponent);
        assertEquals("mantissa", "15", ns.mantissa);
    }
    @Test
    public void negative_fractional_exponent_negative_sign(){
        Decimal.NumericString ns = new Decimal.NumericString("-1.8e-5");

        assertTrue("valid", ns.valid);
        assertEquals("base", 10, ns.baseSize);
        assertEquals("dp", 1, ns.decimalPosition);
        assertEquals("sign", -1, ns.sign);
        assertEquals("exponent", "-5", ns.exponent);
        assertEquals("mantissa", "18", ns.mantissa);
    }

}
