package co.ntweb.maigfrga.blockchain.models;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.common.base.Preconditions;



import co.ntweb.maigfrga.blockchain.Crypto;


/**
 * A transaction input consists of the hash of the transaction that contains the corresponding
 * output, the index of this output in that transaction (indices are simply integers starting from 0),
 * and a digital signature. For the input to be valid, the signature it contains must be a valid signature
 * over the current transaction with the public key in the spent output.
 * @author manuel
 *
 */


public class Input implements Imodel {
    /** hash of the Transaction whose output is being used */
    private final byte[] prevTxHash;
    /** used output's index in the previous transaction */
    private final int outputIndex;
    /** the signature produced to check validity */
    private byte[] signature;

    public byte[] getPrevTxHash() {
		return prevTxHash;
	}

	public int getOutputIndex() {
		return outputIndex;
	}

	public byte[] getSignature() {
		return signature;
	}

	public Input(byte[] prevHash, int outputIndex) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		Preconditions.checkNotNull(prevHash);
        this.prevTxHash = Arrays.copyOf(prevHash, prevHash.length);
        this.outputIndex = outputIndex;
    }

	public void sign(PrivateKey sk, byte[] rawOutputs)  throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		ArrayList<Byte> rawData = new ArrayList<Byte>();
		
		for (int i = 0; i < this.prevTxHash.length; i++)
			rawData.add(this.prevTxHash[i]);
			    	    
	    ByteBuffer bf = ByteBuffer.allocate(Integer.SIZE / BYTE_SIZE);
        bf.putInt(this.outputIndex);
        byte[] outputIndex = bf.array();
        
        for (int i = 0; i < outputIndex.length; i++)
        	rawData.add(outputIndex[i]);

        for (int i = 0; i < rawOutputs.length; i++) {
        	rawData.add(rawOutputs[i]);
        }
        
		byte[] raw = new byte[rawData.size()];
	    int i = 0;
	    for (Byte b : rawData)
	    	raw[i++] = b;
		this.signature = Crypto.sign(sk, raw);
		
		
	}
	
	@Override
	public byte[] getRawData() {		
		
		ArrayList<Byte> rawData = new ArrayList<Byte>();
				
		for (int i = 0; i < this.prevTxHash.length; i++)
			rawData.add(this.prevTxHash[i]);
			    	    
	    ByteBuffer bf = ByteBuffer.allocate(Integer.SIZE / BYTE_SIZE);
        bf.putInt(this.outputIndex);
        byte[] outputIndex = bf.array();        
        for (int i = 0; i < outputIndex.length; i++)
        	rawData.add(outputIndex[i]);
        
        if (null != this.signature)
        	for (int i = 0; i < signature.length; i++)
        		rawData.add(signature[i]);
        

        byte[] raw = new byte[rawData.size()];
	    int i = 0;
	    for (Byte b : rawData)
	    	raw[i++] = b;
		 
		return raw;
	}
	
}
