package e.s.hammercalc;

import org.junit.Test;

import static org.junit.Assert.*;

import e.s.hammercalc.core.DdVec;

public class DdVecTest {
    private static final double epsilon = 0.0001;
    @Test
    public void can_create_empty_vec() {
        DdVec v = new DdVec();

        assertEquals("initial size", 0, v.length());
        assertTrue("empty flag", v.isEmpty());
    }

    @Test
    public void can_create_vec_by_adding_to_start() {
        DdVec v = new DdVec();
        v.insert(1.0);
        v.insert(2.0);
        v.insert(3.0);

        // Conceptually, [1,2,3]

        assertEquals("size", 3, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 3.0, v.get(0), epsilon);
        assertEquals("idx 2", 1.0, v.get(2), epsilon);
    }

    @Test
    public void can_create_vec_by_adding_to_end() {
        DdVec v = new DdVec();
        v.push(1.0);
        v.push(2.0);
        v.push(3.0);

        // Conceptually, [3,2,1]

        assertEquals("size", 3, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 1.0, v.get(0), epsilon);
        assertEquals("idx 2", 3.0, v.get(2), epsilon);
    }


    @Test
    public void can_create_vec_by_adding_to_both_sides() {
        DdVec v = new DdVec();
        v.insert(1.0);
        v.insert(2.0);
        v.push(3.0);
        v.push(4.0);

        // Conceptually, [4,3,1,2]

        assertEquals("size", 3, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 1.0, v.get(0), epsilon);
        assertEquals("idx 2", 3.0, v.get(2), epsilon);
    }

    @Test
    public void can_create_vec_from_array() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        assertEquals("initial size", 6, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 0.1, v.get(0), epsilon);
        assertEquals("idx 5", 5.6, v.get(5), epsilon);
    }
}
