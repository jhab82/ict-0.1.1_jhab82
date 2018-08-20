// 
// Decompiled by Procyon v0.5.30
// 

package cfb.ict;

import java.net.SocketAddress;

public class Neighbor
{
    SocketAddress address;
    int numberOfAllTransactions;
    int numberOfNewTransactions;
    int numberOfInvalidTransactions;
    int numberOfRequestedTransactions;
    int numberOfSharedTransactions;
    
    @Override
    public String toString() {
        return this.address.toString() + ": " + this.numberOfAllTransactions + " / " + this.numberOfNewTransactions + " / " + this.numberOfInvalidTransactions + " / " + this.numberOfRequestedTransactions + " / " + this.numberOfSharedTransactions;
    }
}
