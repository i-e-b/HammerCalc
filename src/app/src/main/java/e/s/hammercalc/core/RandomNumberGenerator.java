package e.s.hammercalc.core;

import java.util.Random;

public class RandomNumberGenerator {
    private static Random rand = new Random();

    public static void Fill(byte[] target) {
        rand.nextBytes(target);
    }
}
