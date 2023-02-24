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

}
