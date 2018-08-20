// 
// Decompiled by Procyon v0.5.30
// 

package cfb.ict;

import java.util.Arrays;

public class Converter
{
    private static final int RADIX = 3;
    private static final int MAX_TRIT_VALUE = 1;
    private static final int MIN_TRIT_VALUE = -1;
    private static final int NUMBER_OF_TRITS_IN_A_TRYTE = 3;
    private static final int[][] TRYTE_TO_TRITS_MAPPINGS;
    private static final String TRYTE_ALPHABET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int NUMBER_OF_TRITS_IN_A_BYTE = 5;
    private static final int[][] BYTE_TO_TRITS_MAPPINGS;
    
    static byte[] bytes(final int[] array, final int n, final int n2) {
        final byte[] array2 = new byte[(n2 + 5 - 1) / 5];
        for (int i = 0; i < array2.length; ++i) {
            int n3 = 0;
            int n4 = (n2 - i * 5 < 5) ? (n2 - i * 5) : 5;
            while (n4-- > 0) {
                n3 = n3 * 3 + array[n + i * 5 + n4];
            }
            array2[i] = (byte)n3;
        }
        return array2;
    }
    
    static int[] trits(final byte[] array, final int n) {
        final int[] array2 = new int[n];
        for (int n2 = 0, n3 = 0; n2 < array.length && n3 < array2.length; ++n2, n3 += 5) {
            System.arraycopy(Converter.BYTE_TO_TRITS_MAPPINGS[(array[n2] < 0) ? (array[n2] + Converter.BYTE_TO_TRITS_MAPPINGS.length) : array[n2]], 0, array2, n3, (array2.length - n3 < 5) ? (array2.length - n3) : 5);
        }
        return array2;
    }
    
    static String trytes(final int[] array, final int n, final int n2) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (n2 + 3 - 1) / 3; ++i) {
            int n3 = array[n + i * 3] + array[n + i * 3 + 1] * 3 + array[n + i * 3 + 2] * 9;
            if (n3 < 0) {
                n3 += "9ABCDEFGHIJKLMNOPQRSTUVWXYZ".length();
            }
            sb.append("9ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(n3));
        }
        return sb.toString();
    }
    
    static String trytes(final int[] array) {
        return trytes(array, 0, array.length);
    }
    
    static long longValue(final int[] array, final int n, final int n2) {
        long n3 = 0L;
        int n4 = n2;
        while (n4-- > 0) {
            n3 = n3 * 3L + array[n + n4];
        }
        return n3;
    }
    
    private static void increment(final int[] array, final int n) {
        for (int n2 = 0; n2 < n && ++array[n2] > 1; ++n2) {
            array[n2] = -1;
        }
    }
    
    static {
        TRYTE_TO_TRITS_MAPPINGS = new int[27][];
        BYTE_TO_TRITS_MAPPINGS = new int[243][];
        final int[] array = new int[3];
        for (int i = 0; i < 27; ++i) {
            Converter.TRYTE_TO_TRITS_MAPPINGS[i] = Arrays.copyOf(array, 3);
            increment(array, 3);
        }
        final int[] array2 = new int[5];
        for (int j = 0; j < 243; ++j) {
            Converter.BYTE_TO_TRITS_MAPPINGS[j] = Arrays.copyOf(array2, 5);
            increment(array2, 5);
        }
    }
}
