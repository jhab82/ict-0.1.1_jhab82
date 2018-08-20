// 
// Decompiled by Procyon v0.5.30
// 

package cfb.ict;

import java.util.Arrays;

public class Curl
{
    static final int HASH_LENGTH = 243;
    private static final int STATE_LENGTH = 729;
    private static final int[] TRUTH_TABLE;
    private final int[] state;
    private final int[] scratchpad;
    
    Curl() {
        this.state = new int[729];
        this.scratchpad = new int[729];
    }
    
    Curl(final int[] array) {
        this.state = new int[729];
        this.scratchpad = new int[729];
        if (array.length != 486) {
            throw new RuntimeException("Illegal inner state length: " + array.length);
        }
        System.arraycopy(array, 0, this.state, 243, array.length);
    }
    
    void reset() {
        for (int i = 0; i < 729; ++i) {
            this.state[i] = 0;
        }
    }
    
    void absorb(final int[] array, int n, int i) {
        do {
            System.arraycopy(array, n, this.state, 0, (i < 243) ? i : 243);
            this.transform();
            n += 243;
            i -= 243;
        } while (i > 0);
    }
    
    void squeeze(final int[] array, int n, int i) {
        do {
            System.arraycopy(this.state, 0, array, n, (i < 243) ? i : 243);
            this.transform();
            n += 243;
            i -= 243;
        } while (i > 0);
    }
    
    int[] innerState() {
        return Arrays.copyOfRange(this.state, 243, 729);
    }
    
    private void transform() {
        int n = 0;
        int n2 = 81;
        while (n2-- > 0) {
            System.arraycopy(this.state, 0, this.scratchpad, 0, 729);
            for (int i = 0; i < 729; ++i) {
                this.state[i] = Curl.TRUTH_TABLE[this.scratchpad[n] + this.scratchpad[n += ((n < 365) ? 364 : -365)] * 3 + 4];
            }
        }
    }
    
    static {
        TRUTH_TABLE = new int[] { 1, 0, -1, 1, -1, 0, -1, 1, 0 };
    }
}
