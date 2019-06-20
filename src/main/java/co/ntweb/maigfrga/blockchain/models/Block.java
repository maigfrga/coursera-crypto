package co.ntweb.maigfrga.blockchain.models;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class Block implements IModel{
	private final byte[] prevBlockHash;
	private ImmutableList<Transaction> transactions;

	public Block(byte[] prevHash) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		Preconditions.checkNotNull(prevHash);
        this.prevBlockHash = Arrays.copyOf(prevHash, prevHash.length);
        this.transactions = ImmutableList.copyOf(new ArrayList<Transaction>());
    }
		
	public void addTransaction(Transaction tx) {
		Preconditions.checkArgument(this.transactions.size() < this.BLOCK_SIZE);
		ArrayList<Transaction> l = new ArrayList<Transaction>();
		for (Transaction t: this.transactions) {
			l.add(t);
		}
		l.add(tx);
		this.transactions = ImmutableList.copyOf(l);
	}
	
	@Override
	public byte[] getRawData() {
		ArrayList<Byte> rawData = new ArrayList<Byte>();
		
		for (int i = 0; i < this.prevBlockHash.length; i++)
			rawData.add(this.prevBlockHash[i]);

		for (Transaction tx: this.transactions) {
			for(byte b: tx.getRawData()) {
				rawData.add(b);
			}
		}		
		
	    byte[] raw = new byte[rawData.size()];
		int i = 0;
		for (Byte b : rawData)
		    raw[i++] = b;
			 
	    return raw;		
	    
	}

}
