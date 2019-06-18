package co.ntweb.maigfrga.blockchain.models;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.common.collect.ImmutableList;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * A transaction consists of a list of inputs, a list of outputs and a unique ID (see the ​ getRawTx()
 * method). The class also contains methods to add and remove an input, add an output,
 * compute digests to sign/hash, add a signature to an input, and compute and store the hash of the
 * transaction once all inputs/outputs/signatures have been added.
 * @author manuel
 *
 */

public class Transaction implements Imodel {

    /** hash of the transaction, its unique id */
    private byte[] hash;
    private  List<Input> inputs;
    private  List<Output> outputs;


    public Transaction(List<Input> inputs, List<Output> outpus) {
    	this.inputs = ImmutableList.copyOf(inputs);
    	this.outputs = ImmutableList.copyOf(outpus);
    	
    }
    


    public byte[] getRawDataToSign(int index) {
        // ith input and all outputs
        ArrayList<Byte> sigData = new ArrayList<Byte>();
        /*if (index > inputs.size())
            return null;
        Input in = inputs.get(index);
        byte[] prevTxHash = in.prevTxHash;
        ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
        b.putInt(in.outputIndex);
        byte[] outputIndex = b.array();
        if (prevTxHash != null)
            for (int i = 0; i < prevTxHash.length; i++)
                sigData.add(prevTxHash[i]);
        for (int i = 0; i < outputIndex.length; i++)
            sigData.add(outputIndex[i]);
        for (Output op : outputs) {
            ByteBuffer bo = ByteBuffer.allocate(Double.SIZE / 8);
            bo.putDouble(op.value);
            byte[] value = bo.array();
            byte[] addressBytes = op.address.getEncoded();
            for (int i = 0; i < value.length; i++)
                sigData.add(value[i]);

            for (int i = 0; i < addressBytes.length; i++)
                sigData.add(addressBytes[i]);
        }
        byte[] sigD = new byte[sigData.size()];
        int i = 0;
        for (Byte sb : sigData)
            sigD[i++] = sb;
        return sigD;
        */
        return null;
    }


    public byte[] getRawTx() {
        ArrayList<Byte> rawTx = new ArrayList<Byte>();
        /*
        for (Input in : inputs) {
            byte[] prevTxHash = in.prevTxHash;
            ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
            b.putInt(in.outputIndex);
            byte[] outputIndex = b.array();
            byte[] signature = in.signature;
            if (prevTxHash != null)
                for (int i = 0; i < prevTxHash.length; i++)
                    rawTx.add(prevTxHash[i]);
            for (int i = 0; i < outputIndex.length; i++)
                rawTx.add(outputIndex[i]);
            if (signature != null)
                for (int i = 0; i < signature.length; i++)
                    rawTx.add(signature[i]);
        }
        for (Output op : outputs) {
            ByteBuffer b = ByteBuffer.allocate(Double.SIZE / 8);
            b.putDouble(op.value);
            byte[] value = b.array();
            byte[] addressBytes = op.address.getEncoded();
            for (int i = 0; i < value.length; i++) {
                rawTx.add(value[i]);
            }
            for (int i = 0; i < addressBytes.length; i++) {
                rawTx.add(addressBytes[i]);
            }

        }
        byte[] tx = new byte[rawTx.size()];
        int i = 0;
        for (Byte b : rawTx)
            tx[i++] = b;
             return tx;
        */
       return null;
    }

    public void sign() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(getRawTx());
            this.hash = md.digest();
        } catch (NoSuchAlgorithmException x) {
            x.printStackTrace(System.err);
        }
    }

    
    public void signTx(PrivateKey sk, int input) throws SignatureException {
        Signature sig = null;
        try {
            sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(sk);
            sig.update(this.getRawDataToSign(input));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        //this.addSignature(sig.sign(),input);
        // Note that this method is incorrectly named, and should not in fact override the Java
        // object finalize garbage collection related method.
        this.sign();
    }
    
    public void signInput(PrivateKey sk, int index) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    	ArrayList<Byte> rawOutputData = new ArrayList<Byte>();
    	
		for (Output out: this.outputs) {
			for(byte b: out.getRawData()) {
				rawOutputData.add(b);
			}
		}

		byte[] rawOutpus = new byte[rawOutputData.size()];
		int i = 0;
		for (Byte b : rawOutputData)
		    rawOutpus[i++] = b;

		Input in = this.inputs.get(index);
		in.sign(sk, rawOutpus);
		
		
    	
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
