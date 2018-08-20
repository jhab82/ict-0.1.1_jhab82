// 
// Decompiled by Procyon v0.5.30
// 

package cfb.ict;

import cfb.ict.Converter;
import cfb.ict.Neighbor;
import cfb.ict.Transaction;

import java.util.HashMap;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.security.SecureRandom;
import java.util.Queue;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Map;

public class Ict
{
    static boolean isShuttingDown;
    static final Map<InetAddress, Neighbor> neighbors;
    static final Map<BigInteger, Transaction> transactions;
    static final Queue<BigInteger> missingTransactionsHashes;
    
    public static void main(final String[] array) {
        try {
            if (array.length == 0) {
                final int[] array2 = new int[243];
                final SecureRandom secureRandom = new SecureRandom();
                int length = array2.length;
                while (length-- > 0) {
                    array2[length] = secureRandom.nextInt(3) - 1;
                }
                System.out.println(Converter.trytes(array2));
            }
            else {
                log("Ict 0.1.1");
                final Properties properties = new Properties();
                try (final FileInputStream fileInputStream = new FileInputStream(array[0])) {
                    properties.load(fileInputStream);
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                final DatagramSocket datagramSocket = new DatagramSocket(Integer.parseInt(properties.getProperty("port", "11111"), 10));
		datagramSocket.setReuseAddress(true);
                final DatagramPacket datagramPacket = new DatagramPacket(new byte[1650], 1650);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> Ict.isShuttingDown = true));
                long n = 0L;
                while (!Ict.isShuttingDown) {
                    try {
                        if (System.currentTimeMillis() >= n) {
                            n = System.currentTimeMillis() + 60000L;
                            System.out.println(LocalDateTime.now());
                            for (final Neighbor neighbor : Ict.neighbors.values()) {
                                System.out.println(neighbor);
                                neighbor.numberOfAllTransactions = 0;
                                neighbor.numberOfNewTransactions = 0;
                                neighbor.numberOfInvalidTransactions = 0;
                                neighbor.numberOfRequestedTransactions = 0;
                                neighbor.numberOfSharedTransactions = 0;
                            }
                            System.out.println("Number of transactions = " + Ict.transactions.size());
                            System.out.println("Number of missing transactions = " + Ict.missingTransactionsHashes.size());
                            Ict.neighbors.clear();
                            Ict.transactions.clear();
                            Ict.missingTransactionsHashes.clear();
                            System.out.println();
                        }
                        datagramSocket.receive(datagramPacket);

			byte[] data = datagramPacket.getData();
			String string = new String(data, 0, datagramPacket.getLength());
			System.out.println(string);
			System.out.println("----");

                        if (datagramPacket.getLength() != 1650) {
                            datagramPacket.setLength(1650);
                        }
                        else {
                            final SocketAddress socketAddress = datagramPacket.getSocketAddress();
			    System.out.println("SocketAddress =" + socketAddress);
                            final Neighbor neighbor2 = Ict.neighbors.computeIfAbsent(((InetSocketAddress)socketAddress).getAddress(), p0 -> new Neighbor());
                            neighbor2.address = socketAddress;
                            final Neighbor neighbor3 = neighbor2;
                            ++neighbor3.numberOfAllTransactions;

                            if (neighbor2.numberOfInvalidTransactions != 0) {
                                continue;
                            }
                            Transaction transaction;
                            try {
                                transaction = new Transaction(datagramPacket.getData());
                            }
                            catch (RuntimeException ex4) {
				    System.out.println("Error: "+ex4);
                                final Neighbor neighbor4 = neighbor2;
                                ++neighbor4.numberOfInvalidTransactions;
                                continue;
                            }
                            final BigInteger bigInteger = new BigInteger(Arrays.copyOfRange(datagramPacket.getData(), 1604, 1650));
                            if (Ict.transactions.putIfAbsent(transaction.hash, transaction) == null) {
                                final Neighbor neighbor5 = neighbor2;
                                ++neighbor5.numberOfNewTransactions;
                                Ict.missingTransactionsHashes.remove(transaction.hash);
                                final LinkedList<Neighbor> list = new LinkedList<Neighbor>();
                                for (final Neighbor neighbor6 : Ict.neighbors.values()) {
                                    if (neighbor6 != neighbor2 && neighbor6.numberOfInvalidTransactions == 0 && (list.isEmpty() || neighbor6.numberOfNewTransactions >= list.get(list.size() - 1).numberOfNewTransactions)) {
                                        list.add(neighbor6);
                                    }
                                }
                                list.add(neighbor2);
                                for (final Neighbor neighbor8 : list) {
                                    final Neighbor neighbor7 = neighbor8;
                                    ++neighbor8.numberOfSharedTransactions;
                                    datagramPacket.setSocketAddress(neighbor7.address);
                                    final BigInteger bigInteger2 = Ict.missingTransactionsHashes.poll();
                                    if (bigInteger2 == null) {
                                        for (int i = 1604; i < 1650; ++i) {
                                            datagramPacket.getData()[i] = 0;
                                        }
                                    }
                                    else {
                                        final byte[] copy = Arrays.copyOf(bigInteger2.toByteArray(), 46);
                                        System.arraycopy(copy, 0, datagramPacket.getData(), 1604, copy.length);
                                    }
                                    datagramSocket.send(datagramPacket);
                                }
                            }
                            Transaction transaction2;
                            if (transaction.hash.equals(bigInteger)) {
                                transaction2 = null;
                            }
                            else {
                                transaction2 = Ict.transactions.get(bigInteger);
                                if (transaction2 == null && !Ict.missingTransactionsHashes.contains(bigInteger)) {
                                    Ict.missingTransactionsHashes.offer(bigInteger);
                                }
                            }
                            if (transaction2 == null) {
                                continue;
                            }
                            final Neighbor neighbor9 = neighbor2;
                            ++neighbor9.numberOfRequestedTransactions;
                            System.arraycopy(transaction2.bytes, 0, datagramPacket.getData(), 0, transaction2.bytes.length);
                            final BigInteger bigInteger3 = Ict.missingTransactionsHashes.poll();
                            if (bigInteger3 == null) {
                                for (int j = 1604; j < 1650; ++j) {
                                    datagramPacket.getData()[j] = 0;
                                }
                            }
                            else {
                                final byte[] copy2 = Arrays.copyOf(bigInteger3.toByteArray(), 46);
                                System.arraycopy(copy2, 0, datagramPacket.getData(), 1604, copy2.length);
                            }
                            datagramSocket.send(datagramPacket);
                        }
                    }
                    catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        }
        catch (Exception ex3) {
            ex3.printStackTrace();
        }
    }
    
    static void log(final String s) {
        System.out.println("[" + LocalDateTime.now() + "] " + s);
    }
    
    static {
        neighbors = new HashMap<InetAddress, Neighbor>();
        transactions = new HashMap<BigInteger, Transaction>();
        missingTransactionsHashes = new LinkedList<BigInteger>();
    }
}
