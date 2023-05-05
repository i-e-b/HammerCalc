package e.s.hammercalc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import e.s.hammercalc.core.ContinuedFraction;
import e.s.hammercalc.core.Fraction;
import e.s.hammercalc.core.LargeInt;
import e.s.hammercalc.core.LargeIntVec;

public class ContinuedFractionTest {

    @Test
    public void trimming_zero_pairs(){
        LargeIntVec src = LargeIntVec.fromInts(0,0,0,1,0,0,0,0,1,0,0,1,0,0,0);
        LargeIntVec expected = LargeIntVec.fromInts(0,1,1,1,0);

        LargeIntVec result = src.trimZeroPairs();
        System.out.println(result.toString());
        assertArrayEquals("A",expected.toArray(), result.toArray());


        src = LargeIntVec.fromInts(0,0,1,0,0,0,1,0,1,0,0,0,0);
        expected = LargeIntVec.fromInts(1,0,1,0,1);

        result = src.trimZeroPairs();
        System.out.println(result.toString());
        assertArrayEquals("B",expected.toArray(), result.toArray());

        src = LargeIntVec.fromInts(1,2,3,4,5,6);
        expected = LargeIntVec.fromInts(1,2,3,4,5,6);

        result = src.trimZeroPairs();
        System.out.println(result.toString());
        assertArrayEquals("C",expected.toArray(), result.toArray());

        src = LargeIntVec.fromInts(1,2,3,4,5);
        expected = LargeIntVec.fromInts(1,2,3,4,5);

        result = src.trimZeroPairs();
        System.out.println(result.toString());
        assertArrayEquals("D",expected.toArray(), result.toArray());

        src = LargeIntVec.fromInts(1);
        expected = LargeIntVec.fromInts(1);

        result = src.trimZeroPairs();
        System.out.println(result.toString());
        assertArrayEquals("E",expected.toArray(), result.toArray());
    }

    @Test
    public void trimming_leading_zero_pairs(){
        LargeIntVec result = LargeIntVec.fromInts(0,0,0,1,0,0,0,0,1,0,0,1,0,0,0);
        LargeIntVec expected = LargeIntVec.fromInts(0,1,0,0,0,0,1,0,0,1,0,0,0);

        result.trimLeadingZeroPairs();
        System.out.println(result);
        assertArrayEquals("A",expected.toArray(), result.toArray());

        result = LargeIntVec.fromInts(0,0,1,0,0,0,1,0,1,0,0,0,0);
        expected = LargeIntVec.fromInts(1,0,0,0,1,0,1,0,0,0,0);

        result.trimLeadingZeroPairs();
        System.out.println(result);
        assertArrayEquals("B",expected.toArray(), result.toArray());

        result = LargeIntVec.fromInts(1,2,3,4,5,6);
        expected = LargeIntVec.fromInts(1,2,3,4,5,6);

        result.trimLeadingZeroPairs();
        System.out.println(result);
        assertArrayEquals("C",expected.toArray(), result.toArray());

        result = LargeIntVec.fromInts(1);
        expected = LargeIntVec.fromInts(1);

        result.trimLeadingZeroPairs();
        System.out.println(result);
        assertArrayEquals("D",expected.toArray(), result.toArray());
    }

    @Test
    public void can_create_a_cf_from_int_components_and_recover_fraction(){
        LargeInt[] components = LargeInt.arrayFromInts(0,1,2,2);
        ContinuedFraction cf = new ContinuedFraction(components);

        Fraction f = cf.toFraction();
        assertEquals("cf->f", "5/7", f.toString());
    }

    @Test
    public void can_create_a_cf_from_a_rational_and_recover_fraction(){
        ContinuedFraction cf = new ContinuedFraction(Fraction.fromVulgarFraction(47,17));

        Fraction f = cf.toFraction();
        assertEquals("cf->f", "47/17", f.toString());
    }

    @Test
    public void can_invert_a_continued_fraction(){
        ContinuedFraction cf0 = new ContinuedFraction(Fraction.fromVulgarFraction(47,17));

        ContinuedFraction cf1 = cf0.invert();
        Fraction f1 = cf1.toFraction();
        assertEquals("(1/cf)->f", "17/47", f1.toString());

        ContinuedFraction cf2 = cf1.invert();
        Fraction f2 = cf2.toFraction();
        assertEquals("(1/1/cf)->f", "47/17", f2.toString());
    }
}
