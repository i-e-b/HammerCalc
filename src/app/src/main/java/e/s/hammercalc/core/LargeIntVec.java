package e.s.hammercalc.core;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class LargeIntVec {

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
    transient LargeInt[] elements; // non-private to simplify nested class access

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

    /** return a vector of large ints from a set of ints */
    public static LargeIntVec fromInts(long... ints) {
        LargeIntVec result = new LargeIntVec(ints.length);
        for (long i : ints) {
            result.addLast(LargeInt.fromLong(i));
        }
        return result;
    }

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
        elements = new LargeInt[initialCapacity];
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
        LargeInt[] a = new LargeInt[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        // Android-added: Clear old array instance that's about to become eligible for GC.
        // This ensures that array elements can be eligible for garbage collection even
        // before the array itself is recognized as being eligible; the latter might
        // take a while in some GC implementations, if the array instance is longer lived
        // (its live-ness rarely checked) than some of its contents.
        Arrays.fill(elements, null);
        elements = a;
        head = 0;
        tail = n;
    }

    public static LargeIntVec FromLargeInt(LargeInt v){
        LargeIntVec result = new LargeIntVec();
        result.addLast(v);
        return result;
    }

    /**
     * Constructs an empty array deque
     */
    public LargeIntVec() {
        elements = new LargeInt[8];
    }

    /**
     * Constructs an empty array deque with an initial capacity
     * sufficient to hold the specified number of elements.
     *
     * @param numElements lower bound on initial capacity of the deque
     */
    public LargeIntVec(int numElements) {
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
    public LargeIntVec(LargeInt[] c) {
        allocateElements(c.length);
        for (LargeInt d : c) addLast(d);
    }

    /**
     * Create a copy of 'other'. No data is shared.
     */
    public LargeIntVec(LargeIntVec other){
        elements = new LargeInt[other.elements.length];
        this.head = other.head;
        this.tail = other.tail;
        System.arraycopy(other.elements, 0, this.elements, 0, elements.length);
    }

    // The main insertion and extraction methods are addFirst,
    // addLast, pollFirst, pollLast. The other methods are defined in
    // terms of these.

    /**
     * Inserts the specified element at the front of this deque.
     *
     * @param e the element to add
     * @throws NullPointerException if the specified element is null
     */
    public void addFirst(LargeInt e) {
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail) doubleCapacity();
    }

    /**
     * Inserts the specified element at the end of this deque.
     */
    public void addLast(LargeInt e) {
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public LargeInt removeFirst() {
        LargeInt x = pollFirst();
        if (x == null) throw new NoSuchElementException();
        return x;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public LargeInt removeLast() {
        LargeInt x = pollLast();
        if (x == null) throw new NoSuchElementException();
        return x;
    }

    private LargeInt pollFirst() {
        final LargeInt[] elements = this.elements;
        final int h = head;
        LargeInt result = elements[h];
        // Element is null if deque empty
        if (result != null) {
            elements[h] = null; // Must null out slot
            head = (h + 1) & (elements.length - 1);
        }
        return result;
    }

    private LargeInt pollLast() {
        final LargeInt[] elements = this.elements;
        final int t = (tail - 1) & (elements.length - 1);
        LargeInt result = elements[t];
        if (result != null) {
            elements[t] = null;
            tail = t;
        }
        return result;
    }

    /**
     * Read but don't remove first item
     * @throws NoSuchElementException {@inheritDoc}
     */
    public LargeInt getFirst() {
        LargeInt result = elements[head];
        if (result == null) throw new NoSuchElementException();
        return result;
    }

    /**
     * Read but don't remove last item
     * @throws NoSuchElementException {@inheritDoc}
     */
    public LargeInt getLast() {
        LargeInt result = elements[(tail - 1) & (elements.length - 1)];
        if (result == null) throw new NoSuchElementException();
        return result;
    }


    /**
     * Removes the element at the specified position in the elements array,
     * adjusting head and tail as necessary.  This can result in motion of
     * elements backwards or forwards in the array.
     */
    public void delete(int i) {
        final LargeInt[] elements = this.elements;
        final int mask = elements.length - 1;
        final int h = head;
        final int t = tail;
        final int front = (i - h) & mask;
        final int back  = (t - i) & mask;

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
            elements[h] = null;
            head = (h + 1) & mask;
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
        }
    }

    // *** Collection Methods ***

    /**
     * Returns the number of elements in this deque.
     *
     * @return the number of elements in this deque
     */
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * Returns {@code true} if this deque contains no elements.
     *
     * @return {@code true} if this deque contains no elements
     */
    public boolean isEmpty() {
        return head == tail;
    }

    /**
     * Returns {@code false} if this deque contains no elements.
     */
    public boolean notEmpty() {
        return head != tail;
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
                elements[i] = null;
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
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this deque
     */
    public LargeInt[] toArray() {
        final int head = this.head;
        final int tail = this.tail;
        boolean wrap = (tail < head);
        int end = wrap ? tail + elements.length : tail;
        LargeInt[] a = Arrays.copyOfRange(elements, head, end);
        if (wrap) System.arraycopy(elements, 0, a, elements.length - head, tail);

        LargeInt[] result = new LargeInt[a.length];
        for (int i = 0; i < a.length; i++) result[i] = a[i];
        return result;
    }

    // *** Array-like Methods ***

    /**
     * Returns the number of elements in this deque.
     */
    public int length() {
        return (tail - head) & (elements.length - 1);
    }

    /** set the value at the given index */
    public void set(int index, LargeInt value) {
        if (index >= length()) return;
        if (index < 0) return;

        final int head = this.head;
        final int tail = this.tail;

        if (head < tail) {
            elements[index + head] = value;
            return;
        }

        int rIdx = (elements.length - 1) - head; // 'real' index at end of array
        if (index <= rIdx) elements[index + head] = value; // it's on the 'right' side of array
        else elements[index - (rIdx + 1)] = value;// index is wrapped
    }

    /** update value at index to equal (value + v) */
    public void increment(int index, LargeInt v) {
        if (index >= length()) return;
        if (index < 0) return;

        final int head = this.head;
        final int tail = this.tail;

        if (head < tail) {
            elements[index + head] = elements[index + head].add(v);
            return;
        }

        int rIdx = (elements.length - 1) - head; // 'real' index at end of array
        if (index <= rIdx) elements[index + head] = elements[index + head].add(v); // it's on the 'right' side of array
        else elements[index - (rIdx + 1)] = elements[index - (rIdx + 1)] .add(v);// index is wrapped
    }

    /** update value at index to equal (value * v) */
    public void multiply(int index, LargeInt v) {
        if (index >= length()) return;
        if (index < 0) return;

        final int head = this.head;
        final int tail = this.tail;

        if (head < tail) {
            elements[index + head] = elements[index + head].multiply(v);
            return;
        }

        int rIdx = (elements.length - 1) - head; // 'real' index at end of array
        if (index <= rIdx) elements[index + head] = elements[index + head].multiply(v); // it's on the 'right' side of array
        else elements[index - (rIdx + 1)] = elements[index - (rIdx + 1)].multiply(v);// index is wrapped
    }

    /** update value at index to equal (value % mod) */
    public void modulo(int index, LargeInt mod) {
        if (index >= length()) return;
        if (index < 0) return;

        final int head = this.head;
        final int tail = this.tail;

        if (head < tail) {
            elements[index + head] = elements[index + head].mod(mod);
            return;
        }

        int rIdx = (elements.length - 1) - head; // 'real' index at end of array
        if (index <= rIdx) elements[index + head] = elements[index + head].mod(mod); // it's on the 'right' side of array
        else elements[index - (rIdx + 1)] = elements[index - (rIdx + 1)].mod(mod);// index is wrapped
    }

    /** return the value at the given index. Returns NaN if out of range */
    public LargeInt get(int index) {
        if (index >= length()) return LargeInt.LARGE_NAN;
        if (index < 0) return LargeInt.LARGE_NAN;

        // Just addFirst looks like ; addFirst(0),addFirst(1),addFirst(2)
        // conceptually, this is the array [0,1,2]
        // [<tail> _, ... _, <head>3, 2, 1]

        // Just addLast looks like ; addLast(0),addLast(1),addLast(2)
        // conceptually, this is the array [2,1,0]
        // [<tail> 0, 1, 2 _, ... _, <head>_]

        final int head = this.head;
        final int tail = this.tail;

        if (head < tail) return elements[index + head];

        int rIdx = (elements.length - 1) - head; // 'real' index at end of array
        if (index <= rIdx) return elements[index + head]; // it's on the 'right' side of array
        return elements[index - (rIdx + 1)];// index is wrapped
    }

    /** return the value at the given index. Returns defaultValue if out of range */
    public LargeInt get(int index, LargeInt defaultValue) {
        if (index >= length()) return defaultValue;
        if (index < 0) return defaultValue;

        final int head = this.head;
        final int tail = this.tail;

        if (head < tail) return elements[index + head];

        int rIdx = (elements.length - 1) - head; // 'real' index at end of array
        if (index <= rIdx) return elements[index + head]; // it's on the 'right' side of array
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

    /** remove zero-valued items from start */
    public void trimLeadingZero(){
        while (length() > 0){
            if (this.getFirst().compareTo(LargeInt.ZERO) == 0) return;
            this.removeFirst();
        }
    }

    /** remove pairs of zero-valued items from start, in place */
    public void trimLeadingZeroPairs(){
        while (length() > 1){
            if (   (this.get(0).isZero())
                && (this.get(1).isZero())) {
                this.removeFirst();
                this.removeFirst();
            } else return;
        }
    }

    /** return a new vector with all pairs of zeros removed */
    public LargeIntVec trimZeroPairs(){
        int len = length();
        if (len < 2) return this;
        LargeIntVec result = new LargeIntVec(this.length());

        int i;
        for (i = 1; i < len; i++) {
            if (get(i).isZero() && get(i-1).isZero()){
                i++; // skip this and prev
                continue;
            }
            result.addLast(get(i-1));
        }
        if (i == len) result.addLast(getLast());

        return result;
    }

    /** reverse the order of items in this vector, without moving head or tail pointers */
    public void reverse() {
        if (length() < 2) return;

        int h = head;
        int t = tail;
        int m = elements.length - 1;
        int c = length() / 2;

        t = (t - 1) & m;
        for (int i = 0; i < c; i++) {
            LargeInt tmp = elements[h];
            elements[h] = elements[t];
            elements[t] = tmp;
            h = (h + 1) & m;
            t = (t - 1) & m;
        }

    }

    /**
     * Create a copy from [start..end)
     * @param start inclusive start index
     * @param end exclusive end index
     */
    public LargeIntVec slice(int start, int end) {
        if (start < 0) start += length();
        if (end < 0) end += length();
        if (start < 0 || start >= end) return new LargeIntVec();

        LargeIntVec result = new LargeIntVec(end - start);
        for (int i = start; i < end; i++){
            result.addLast(get(i));
        }
        return result;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        int len = length();
        StringBuilder sb = new StringBuilder(len * 5); // random guess

        sb.append('[');

        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(',');
            sb.append(get(i).toString());
        }
        sb.append(']');

        return sb.toString();
    }
}
