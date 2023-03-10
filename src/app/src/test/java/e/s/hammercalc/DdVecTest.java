package e.s.hammercalc;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;

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
        v.addFirst(1.0);
        v.addFirst(2.0);
        v.addFirst(3.0);

        // Conceptually, [3,2,1]

        assertEquals("size", 3, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 3.0, v.get(0), epsilon);
        assertEquals("idx 2", 1.0, v.get(2), epsilon);
    }

    @Test
    public void can_create_vec_by_adding_to_end() {
        DdVec v = new DdVec();
        v.addLast(1.0);
        v.addLast(2.0);
        v.addLast(3.0);

        // Conceptually, [1,2,3]

        assertEquals("size", 3, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 1.0, v.get(0), epsilon);
        assertEquals("idx 2", 3.0, v.get(2), epsilon);
    }

    @Test
    public void can_create_vec_by_adding_to_both_sides() {
        DdVec v = new DdVec();
        v.addLast(1.0);
        v.addLast(2.0);
        v.addFirst(3.0);
        v.addFirst(4.0);

        // Conceptually, [4,3,1,2]

        assertEquals("size", 4, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 4.0, v.get(0), epsilon);
        assertEquals("idx 1", 3.0, v.get(1), epsilon);
        assertEquals("idx 2", 1.0, v.get(2), epsilon);
        assertEquals("idx 3", 2.0, v.get(3), epsilon);
    }

    @Test
    public void can_peek_at_vector_ends_without_removing_items() {
        DdVec v = new DdVec();
        v.addLast(1.0);
        v.addLast(2.0);
        v.addFirst(3.0);
        v.addFirst(4.0);

        // Conceptually, [4,3,1,2]

        assertEquals("peek start 1", 4.0, v.getFirst(), epsilon);
        assertEquals("peek start 2", 4.0, v.getFirst(), epsilon);
        assertEquals("peek end 1", 2.0, v.getLast(), epsilon);
        assertEquals("peek end 2", 2.0, v.getLast(), epsilon);

        assertEquals("size", 4, v.length());
        assertFalse("empty flag", v.isEmpty());

        // Check get by index
        assertEquals("idx 0", 4.0, v.get(0), epsilon);
        assertEquals("idx 1", 3.0, v.get(1), epsilon);
        assertEquals("idx 2", 1.0, v.get(2), epsilon);
        assertEquals("idx 3", 2.0, v.get(3), epsilon);
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

    @Test
    public void can_restore_array_from_vec() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        double[] result = v.toArray();

        assertEquals("vector length", 6, v.length());
        assertEquals("result length", 6, result.length);

        for (int i = 0; i < src.length; i++) {
            assertEquals("index "+i, src[i], result[i], epsilon);
        }
    }

    @Test
    public void can_check_if_indexes_are_in_bounds_of_vec() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        assertFalse("too low", v.hasIndex(-1));
        assertTrue("first item", v.hasIndex(0));
        assertTrue("middle item", v.hasIndex(3));
        assertTrue("last item", v.hasIndex(5));
        assertFalse("too high", v.hasIndex(6));
        assertFalse("way too high", v.hasIndex(6000));
    }

    @Test
    public void can_restore_array_from_vec_after_removing_items() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        assertEquals("removed first", 0.1, v.removeFirst(), epsilon);
        assertEquals("removed last", 5.6, v.removeLast(), epsilon);

        double[] result = v.toArray();

        assertEquals("vector length", 4, v.length());
        assertEquals("result length", 4, result.length);

        for (int i = 0; i < result.length; i++) {
            assertEquals("index "+i, src[i+1], result[i], epsilon);
        }
    }

    @Test
    public void can_restore_array_from_vec_after_adding_items() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        v.addFirst(-1);
        v.addLast(-2);

        double[] expected = {-1.0, 0.1, 1.2, 2.3, 3.4, 4.5, 5.6, -2.0};
        double[] result = v.toArray();

        assertEquals("vector length", 8, v.length());
        assertEquals("result length", 8, result.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("index "+i, expected[i], result[i], epsilon);
        }
    }

    @Test
    public void can_remove_vector_items_by_index() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        v.delete(1);
        v.delete(4); // index 5 in src

        double[] expected = {0.1, 2.3, 3.4, 5.6};
        double[] result = v.toArray();

        assertEquals("vector length", 4, v.length());
        assertEquals("result length", 4, result.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("index "+i, expected[i], result[i], epsilon);
        }
    }

    @Test
    public void can_clear_all_items_from_vector() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        assertEquals("vector length before clear", 6, v.length());

        v.clear();
        assertEquals("vector length after clear", 0, v.length());

        double[] result = v.toArray();
        assertEquals("array length after clear", 0, result.length);

        // can start adding things again
        v.addLast(1);
        v.addLast(2);
        v.addLast(3);

        double[] expected = {1,2,3};
        result = v.toArray();

        assertEquals("vector length", 3, v.length());
        assertEquals("result length", 3, result.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("index "+i, expected[i], result[i], epsilon);
        }
    }

    @Test
    public void can_modify_items_in_vec_in_place_by_index() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 123};
        DdVec v = new DdVec(src);

        v.increment(1, 10.0);
        v.multiply(2, 10.0);
        v.set(4, -4.4);
        v.modulo(5, 10);

        double[] expected = {0.1, 11.2, 23.0, 3.4, -4.4, 3};
        double[] result = v.toArray();

        assertEquals("vector length", 6, v.length());
        assertEquals("result length", 6, result.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("index "+i, expected[i], result[i], epsilon);
        }
    }

    @Test
    public void vectors_can_scale_beyond_initial_bounds() {
        double[] initial = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        DdVec src = new DdVec(initial);
        DdVec dst = new DdVec(8);

        while (src.notEmpty()){
            assertFalse(src.isEmpty());

            dst.addFirst(src.removeLast());
            dst.addLast(src.removeFirst());
        }

        double[] expected = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] result = dst.toArray();

        assertEquals("dest length", 20, dst.length());
        assertEquals("result length", 20, result.length);
        assertEquals("source length", 0, src.length());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("index "+i, expected[i], result[i], epsilon);
        }
    }

    @Test
    public void vectors_can_be_truncated_to_a_given_length(){
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec v = new DdVec(src);

        v.truncateTo(3);

        double[] expected = {0.1, 1.2, 2.3};
        double[] result = v.toArray();

        assertEquals("vector length", 3, v.length());
        assertEquals("result length", 3, result.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("index "+i, expected[i], result[i], epsilon);
        }
    }

    @Test
    public void vectors_can_have_leading_zeros_truncated(){
        double[] src = {0.0, 0.0, 0.0000001, 1, 2, 0, 0, 0};
        DdVec v = new DdVec(src);

        assertEquals("length before", 8, v.length());
        v.trimLeadingZero();
        assertEquals("length after", 6, v.length());
        v.trimLeadingZero(); // no-op if no leading zeros
        assertEquals("length after", 6, v.length());

        assertArrayEquals("values", new double[]{0.0000001, 1, 2, 0, 0, 0}, v.toArray(), epsilon);
    }

    @Test
    public void copied_vectors_do_not_share_data(){
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec a = new DdVec(src);
        DdVec b = new DdVec(a);

        a.set(1, 1000.2);

        assertEquals("A1", 1000.2, a.get(1), epsilon);
        assertEquals("B1", 1.2, b.get(1), epsilon);

        b.reverse();
        assertArrayEquals("A2", new double[]{0.1, 1000.2, 2.3, 3.4, 4.5, 5.6}, a.toArray(), epsilon);
        assertArrayEquals("B2", new double[]{5.6, 4.5, 3.4, 2.3, 1.2, 0.1}, b.toArray(), epsilon);
    }

    @Test
    public void can_create_vec_as_subset_of_another() {
        double[] src = {0.1, 1.2, 2.3, 3.4, 4.5, 5.6};
        DdVec a = new DdVec(src);
        DdVec b = a.slice(1,3);

        assertEquals("a size", 6, a.length());
        assertEquals("b size", 2, b.length());

        // values not shared
        a.set(1, 100);
        a.set(2, 200);

        // Check get by index
        assertArrayEquals("A", new double[]{0.1, 100, 200, 3.4, 4.5, 5.6}, a.toArray(), epsilon);
        assertArrayEquals("B", new double[]{1.2, 2.3}, b.toArray(), epsilon);
    }

    @Test
    public void can_reverse_vector_in_place_after_various_operations_1(){
        DdVec v = new DdVec();

        v.reverse(); // should be a no-op, cause no errors
        assertEquals("vector length", 0, v.length());

        v.addLast(5);  // 5
        v.addFirst(4); // 4 5

        System.out.println(Arrays.toString(v.toArray()));
        assertArrayEquals("a", new double[]{4,5}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("b", new double[]{5,4}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("c", new double[]{4,5}, v.toArray(), epsilon);

        v.addLast(6);  // 4 5 6
        v.addFirst(3); // 3 4 5 6
        v.addLast(7);  // 3 4 5 6 7
        v.addFirst(2); // 2 3 4 5 6 7

        assertArrayEquals("d", new double[]{2,3,4,5,6,7}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("e", new double[]{7,6,5,4,3,2}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("f", new double[]{2,3,4,5,6,7}, v.toArray(), epsilon);

        v.addLast(8);  // 2 3 4 5 6 7 8
        v.addFirst(1); // 1 2 3 4 5 6 7 8
        v.addLast(9);  // 1 2 3 4 5 6 7 8 9
        v.addFirst(0); // 0 1 2 3 4 5 6 7 8 9

        assertArrayEquals("g", new double[]{0,1,2,3,4,5,6,7,8,9}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("h", new double[]{9,8,7,6,5,4,3,2,1,0}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("i", new double[]{0,1,2,3,4,5,6,7,8,9}, v.toArray(), epsilon);
    }

    @Test
    public void can_reverse_vector_in_place_after_various_operations_2(){
        DdVec v = new DdVec();

        v.addLast(-1);
        v.addLast(-1);
        v.addLast(0);
        v.addLast(1);
        v.addLast(2);
        v.addLast(3);
        v.removeFirst();
        v.removeFirst();

        assertArrayEquals("a", new double[]{0,1,2,3}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("b", new double[]{3,2,1,0}, v.toArray(), epsilon);
        v.reverse();
        assertArrayEquals("c", new double[]{0,1,2,3}, v.toArray(), epsilon);
    }
}
