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
    public void can_create_a_cf_from_int_components(){
        //ContinuedFraction cf = new ContinuedFraction();
        fail("Not yet implemented");
    }

    @Test
    public void general_to_simple(){
        ContinuedFraction cf1 = ContinuedFraction.Constants.C_PiUnder4();
        ContinuedFraction.CfSimplifier pU4 = cf1.Simplify();

        // 4/pi => 1.2732395447351626863 => 6741806123/5421409605
        // pi/4 -> 5421409605/6741806123
        // pi -> 21685638420/6741806123 (approx. 3.216591818921993)
        for (int i = 0; i < 100; i++) {
            LargeInt x = pU4.getLeft();
            System.out.print(x);
            System.out.print(", ");
            pU4.next();
        }

        // better would be [3; 7, 15, 1, 292, 1 ...]
    }

    @Test
    public void rational_to_simple(){
        ContinuedFraction cf1 = ContinuedFraction.fromRational(Fraction.fromVulgarFraction(97, 7));

        assertEquals("97/7->", "13; 1, 6", cf1.toString());
    }
}
