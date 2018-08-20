// 
// Decompiled by Procyon v0.5.30
// 

package cfb.ict;

import cfb.ict.Converter;
import cfb.ict.Curl;
import java.util.Arrays;
import java.math.BigInteger;

class Transaction
{
    static final int SIZE_IN_BYTES = 1604;
    static final int SIZE_IN_TRITS = 8019;
    static final int HASH_SIZE_IN_BYTES = 46;
    static final int TRANSACTION_PACKET_SIZE = 1650;
    static final int SIGNATURE_MESSAGE_FRAGMENT_TRINARY_OFFSET = 0;
    static final int SIGNATURE_MESSAGE_FRAGMENT_TRINARY_SIZE = 6561;
    static final int ADDRESS_TRINARY_OFFSET = 6561;
    static final int ADDRESS_TRINARY_SIZE = 243;
    static final int VALUE_TRINARY_OFFSET = 6804;
    static final int VALUE_TRINARY_SIZE = 81;
    static final int VALUE_USABLE_TRINARY_SIZE = 33;
    static final int TAG_TRINARY_OFFSET = 6885;
    static final int TAG_TRINARY_SIZE = 81;
    static final int TIMESTAMP_TRINARY_OFFSET = 6966;
    static final int TIMESTAMP_TRINARY_SIZE = 27;
    static final int MIN_WEIGHT_MAGNITUDE = 14;
    final byte[] bytes;
    final BigInteger hash;
    final long value;
    
    Transaction(final byte[] array) {
        this.bytes = Arrays.copyOf(array, 1604);
        final int[] trits = Converter.trits(this.bytes, 8019);
	//System.out.println(Converter.trytes(trits,0,8019));
	//System.out.println("---");
	//System.out.println(Converter.longValue(trits,0,8019));
        for (int i = 6837; i < 6885; ++i) {
            if (trits[i] != 0) { 
		System.out.println(Converter.trytes(trits,0,8019));
		System.out.println("Invalid transaction value");
                throw new RuntimeException("Invalid transaction value");
            }
        }
        this.value = Converter.longValue(trits, 6804, 33);
        if (this.value != 0L && trits[6803] != 0) {

		System.out.println(Converter.trytes(trits,0,8019));
		System.out.println("Invalid address");
            throw new RuntimeException("Invalid address");
        }
        if (Converter.longValue(trits, 6966, 27) <1508760000L) {
		System.out.println(Converter.trytes(trits,0,8019));
		System.out.println("Invalid timestamp");
            throw new RuntimeException("Invalid timestamp");
        }
        final Curl curl = new Curl();
        curl.absorb(trits, 0, 8019);
        final int[] array2 = new int[243];
        curl.squeeze(array2, 0, array2.length);
        int n = 14;
        while (n-- > 0) {
            if (array2[242 - n] != 0) {
		System.out.println(Converter.trytes(trits,0,8019));
		System.out.println("Invalid transaction hash");
                throw new RuntimeException("Invalid transaction hash: " + Converter.trytes(array2));
            }
        }
        this.hash = new BigInteger(Arrays.copyOf(Converter.bytes(array2, 0, array2.length), 46));
    }
}
