package e.s.hammercalc.core;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

// IEB: Rewrite this to be less jumbled


/**
 * Resizable-array dequeue for double valued items.
 * This is based on java.util.ArrayDeque, and attempts
 * to replicate JavaScript array semantics
 */
public class DdVec {
    /**
     * The array in which the elements of the deque are stored.
     * The capacity of the deque is the length of this array, which is
     * always a power of two. The array is never allowed to become
     * full, except transiently within an addX method where it is
     * resized (see doubleCapacity) immediately upon becoming full,
     * thus avoiding head and tail wrapping around to equal each
     * other.  We also guarantee that all array cells not holding
     * deque elements are always null.
     */
    transient double[] elements; // non-private to simplify nested class access

    /**
     * The index of the element at the head of the deque (which is the
     * element that would be removed by remove() or pop()); or an
     * arbitrary number equal to tail if the deque is empty.
     */
    transient int head;

    /**
     * The index at which the next element would be added to the tail
     * of the deque (via addLast(E), add(E), or push(E)).
     */
    transient int tail;

    /**
     * The minimum capacity that we'll use for a newly created deque.
     * Must be a power of 2.
     */
    private static final int MIN_INITIAL_CAPACITY = 8;

    // ******  Array allocation and resizing utilities ******

    /**
     * Allocates empty array to hold the given number of elements.
     *
     * @param numElements the number of elements to hold
     */
    private void allocateElements(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>> 1);
            initialCapacity |= (initialCapacity >>> 2);
            initialCapacity |= (initialCapacity >>> 4);
            initialCapacity |= (initialCapacity >>> 8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0)    // Too many elements, must back off
                initialCapacity >>>= 1; // Good luck allocating 2^30 elements
        }
        elements = new double[initialCapacity];
    }

    /**
     * Doubles the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     */
    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("Sorry, deque too big");
        double[] a = new double[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        // Android-added: Clear old array instance that's about to become eligible for GC.
        // This ensures that array elements can be eligible for garbage collection even
        // before the array itself is recognized as being eligible; the latter might
        // take a while in some GC implementations, if the array instance is longer lived
        // (its liveness rarely checked) than some of its contents.
        Arrays.fill(elements, 0);
        elements = a;
        head = 0;
        tail = n;
    }

    /**
     * Constructs an empty array deque with an initial capacity
     * sufficient to hold 16 elements.
     */
    public DdVec() {
        elements = new double[16];
    }

    /**
     * Constructs an empty array deque with an initial capacity
     * sufficient to hold the specified number of elements.
     *
     * @param numElements lower bound on initial capacity of the deque
     */
    public DdVec(int numElements) {
        allocateElements(numElements);
    }

    /**
     * Constructs a deque containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.  (The first element returned by the collection's
     * iterator becomes the first element, or <i>front</i> of the
     * deque.)
     *
     * @param c the collection whose elements are to be placed into the deque
     * @throws NullPointerException if the specified collection is null
     */
    public DdVec(double[] c) {
        allocateElements(c.length);
        for (double d : c) push(d);
    }

    // The main insertion and extraction methods are addFirst,
    // addLast, pollFirst, pollLast. The other methods are defined in
    // terms of these.

    /**
     * Inserts the specified element at the front of this deque.
     * Same as 'unshift' in JS
     */
    public void insert(double e) {
        // Note, this starts at the highest index. So `new(); insert(1); insert(2)` would look like [0,...,0, 2.0, 1.0]
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail)
            doubleCapacity();
    }

    /**
     * Inserts the specified element at the end of this deque.
     */
    public void push(double e) {
        // Note, this starts at the lowest index. So `new(); push(1); push(2)` would look like [1.0, 2.0, 0,...,0]
        elements[tail] = e;
        if ((tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }

    /**
     * Read, return and remove the element at the head. Returns NaN if empty
     */
    public double pollFirst() {
        if (head == tail) return Double.NaN;
        final double[] elements = this.elements;
        final int h = head;
        double result = elements[h];
        elements[h] = 0; // Must null out slot
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    /**
     * Read, return and remove the element at the tail. Returns NaN if empty
     */
    public double pollLast() {
        if (head == tail) return Double.NaN;
        final double[] elements = this.elements;
        final int t = (tail - 1) & (elements.length - 1);
        double result = elements[t];
        elements[t] = 0;
        tail = t;
        return result;
    }

    /**
     * return but do not remove element at the head
     */
    public double peekFirst() {
        return elements[head];
    }

    /**
     * return but do not remove element at the head
     */
    public double peekLast() {
        return elements[(tail - 1) & (elements.length - 1)];
    }

    /**
     * Pops an element from the the start of the Vector
     */
    public double pop() {
        return pollLast();
    }

    /**
     * Removes the element at the specified position in the elements array,
     * adjusting head and tail as necessary.  This can result in motion of
     * elements backwards or forwards in the array.
     *
     * <p>This method is called delete rather than remove to emphasize
     * that its semantics differ from those of {@link List#remove(int)}.
     *
     * @return true if elements moved backwards
     */
    boolean delete(int i) {
        final double[] elements = this.elements;
        final int mask = elements.length - 1;
        final int h = head;
        final int t = tail;
        final int front = (i - h) & mask;
        final int back = (t - i) & mask;

        // Invariant: head <= i < tail mod circularity
        if (front >= ((t - h) & mask))
            throw new ConcurrentModificationException();

        // Optimize for least element motion
        if (front < back) {
            if (h <= i) {
                System.arraycopy(elements, h, elements, h + 1, front);
            } else { // Wrap around
                System.arraycopy(elements, 0, elements, 1, i);
                elements[0] = elements[mask];
                System.arraycopy(elements, h, elements, h + 1, mask - h);
            }
            elements[h] = 0;
            head = (h + 1) & mask;
            return false;
        } else {
            if (i < t) { // Copy the null tail as well
                System.arraycopy(elements, i + 1, elements, i, back);
                tail = t - 1;
            } else { // Wrap around
                System.arraycopy(elements, i + 1, elements, i, mask - i);
                elements[mask] = elements[0];
                System.arraycopy(elements, 1, elements, 0, t);
                tail = (t - 1) & mask;
            }
            return true;
        }
    }

    // *** Collection Methods ***

    /**
     * Returns the number of elements in this deque.
     */
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * Returns the number of elements in this deque.
     */
    public int length() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * Returns {@code true} if this deque contains no elements.
     */
    public boolean isEmpty() {
        return head == tail;
    }


    /**
     * Removes all of the elements from this deque.
     * The deque will be empty after this call returns.
     */
    public void clear() {
        int h = head;
        int t = tail;
        if (h != t) { // clear all cells
            head = tail = 0;
            int i = h;
            int mask = elements.length - 1;
            do {
                elements[i] = 0;
                i = (i + 1) & mask;
            } while (i != t);
        }
    }

    /**
     * Returns an array containing all of the elements in this deque
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this deque.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     */
    public double[] toArray() {
        final int head = this.head;
        final int tail = this.tail;
        boolean wrap = (tail < head);
        int end = wrap ? tail + elements.length : tail;
        double[] a = Arrays.copyOfRange(elements, head, end);
        if (wrap) System.arraycopy(elements, 0, a, elements.length - head, tail);
        return a;
    }


    /** set the value at the given index */
    public void set(int index, double value) {
        if (index >= length()) return;
        if (index < 0) return;

        final int head = this.head;
        final int tail = this.tail;

        if (head > tail) { // elements are all in order
            elements[index + tail] = value;
            return;
        }

        int rIdx = (elements.length - 1) - tail; // 'real' index at end of array
        if (index <= rIdx) elements[index + tail] = value; // it's on the 'right' side of array
        else elements[index - (rIdx + 1)] = value;// index is wrapped
    }

    /** Add v to the value at given index */
    public void increment(int index, double v) {
        if (index >= length()) return;
        if (index < 0) return;

        final int head = this.head;
        final int tail = this.tail;

        if (head > tail) { // elements are all in order
            elements[index + tail] += v;
            return;
        }

        int rIdx = (elements.length - 1) - tail; // 'real' index at end of array
        if (index <= rIdx) elements[index + tail] += v; // it's on the 'right' side of array
        else elements[index - (rIdx + 1)] += v;// index is wrapped
    }

    /** return the value at the given index. Returns NaN if out of range */
    public double get(int index) {
        if (index >= length()) return Double.NaN;
        if (index < 0) return Double.NaN;

        // Just addFirst looks like ; addFirst(0),addFirst(1),addFirst(2)
        // conceptually, this is the array [0,1,2]
        // [<tail> _, ... _, <head>3, 2, 1]

        // Just addLast looks like ; addLast(0),addLast(1),addLast(2)
        // conceptually, this is the array [2,1,0]
        // [<tail> 0, 1, 2 _, ... _, <head>_]

        final int head = this.head;
        final int tail = this.tail;

        if (head > tail) return elements[index + tail];

        int rIdx = (elements.length - 1) - tail; // 'real' index at end of array
        if (index <= rIdx) return elements[index + tail]; // it's on the 'right' side of array
        return elements[index - (rIdx + 1)];// index is wrapped
    }

    /** returns true if the index is valid in the vector */
    public boolean hasIndex(int idx) {
        return idx >= 0 && idx < length();
    }

    /** remove items from end until length is <= newLength*/
    public void truncateTo(int newLength) {
        if (newLength <=0) {
            clear();
            return;
        }
        while (length() > newLength){
            this.pollLast();
        }
    }
}
