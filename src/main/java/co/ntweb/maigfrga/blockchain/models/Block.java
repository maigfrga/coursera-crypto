package co.ntweb.maigfrga.blockchain.models;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class Block implements IModel{
	private final byte[] prevHash;
	private ImmutableList<Transaction> transactions;

	public Block(byte[] prevHash) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		Preconditions.checkNotNull(prevHash);
        this.prevHash = Arrays.copyOf(prevHash, prevHash.length);
    }
		
	public void addTransaction(Transaction tx) {
		ArrayList<Transaction> l = new ArrayList<Transaction>();
		for (Transaction t: this.transactions) {
			l.add(t);
		}
		l.add(tx);
		this.transactions = ImmutableList.copyOf(l);
	}
	
	@Override
	public byte[] getRawData() {
	
		if (this.transactions == null)
			return null;
	
	    return null;
	}

}
