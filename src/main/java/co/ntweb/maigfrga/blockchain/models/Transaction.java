package co.ntweb.maigfrga.blockchain.models;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;

/**
 * A transaction consists of a list of inputs, a list of outputs and a unique ID(Hash)
 *
 */

public class Transaction implements IModel {

    /** hash of the transaction, its unique id */
    private byte[] hash;
    private   ImmutableList<Input> inputs;
    private   ImmutableList<Output> outputs;


    public Transaction(List<Input> inputs, List<Output> outputs) {
    	this.inputs = ImmutableList.copyOf(inputs);
    	this.outputs = ImmutableList.copyOf(outputs);
    	
    }
    
    
    /***
     * Sign a given input. The Transaction has to be built at this stage because
     * the signature is created by appending the Input data and all the outputs
     * @param sk
     * @param inputIndex
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     */
    public void signInput(PrivateKey sk, int inputIndex) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    	ArrayList<Byte> rawOutputData = new ArrayList<Byte>();
    	
    	// Collects the raw data for all the transaction outputs
    	for (Output out: this.outputs) {
			for(byte b: out.getRawData()) {
				rawOutputData.add(b);
			}
		}

		byte[] rawOutpus = new byte[rawOutputData.size()];
		int i = 0;
		for (Byte b : rawOutputData)
		    rawOutpus[i++] = b;

		Input inToSing = this.inputs.get(inputIndex);
		inToSing.sign(sk, rawOutpus);
		List<Input> newL = new ArrayList<Input>();
		
		for(int idx=0; idx < this.inputs.size(); idx++) {
			if(inputIndex == idx) {
				newL.add(inToSing);
			} else {
				newL.add(this.inputs.get(idx));
				
			}
		}
		
		this.inputs = ImmutableList.copyOf(newL);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(this.getRawData());
        this.hash = md.digest();
    	
    }
    
	@Override
	public byte[] getRawData() {
		ArrayList<Byte> rawData = new ArrayList<Byte>();
		
		
		for (Input in : this.inputs) {
			for(byte b: in.getRawData()) {
				rawData.add(b);
			}
		}
		
		for (Output out: this.outputs) {
			for(byte b: out.getRawData()) {
				rawData.add(b);
			}
		}
		
        byte[] raw = new byte[rawData.size()];
	    int i = 0;
	    for (Byte b : rawData)
	    	raw[i++] = b;
		 
		return raw;		
	}     

    public byte[] getHash() {
        return hash;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }


}
