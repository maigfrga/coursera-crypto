package co.ntweb.maigfrga.blockchian.utils;

import java.math.BigInteger;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import co.ntweb.maigfrga.blockchain.models.Transaction;


public class TestFactory {
	private final ThreadLocalRandom random;

    public Transaction getTransaction(byte[] hash) {
        return transactions.get(hash);
    }

    private  Map<byte[], Transaction> transactions;

	public TestFactory() {
		this.random = ThreadLocalRandom.current();
	    this.transactions = new HashMap<byte[], Transaction>();
	}

     /*
     * Set up the root transaction:
     */
	/*public Transaction createRootTransaction(KeyPair owner, double value)  throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		final Transaction tx = new Transaction();
        tx.addOutput(value, owner.getPublic());

        // This value has no meaning, but tx.getRawDataToSign(0) will access it in prevTxHash;
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        byte[] rawData = tx.getRawDataToSign(0);
        tx.addSignature(sign(owner.getPrivate(), rawData), 0);
        tx.sign();
        this.transactions.put(tx.getHash(), tx);
        return tx;
	}*/


	/*

	public Transaction createTransaction(KeyPair sender, Map<byte[], Integer> inputs, Map<PublicKey, Double> outputs, Map<byte[], Integer> extraInputs )  throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

		final Transaction tx = new Transaction();

		        // Every output is a value and public key of the receiver
        if (outputs != null) {
            for (Map.Entry<PublicKey, Double> entry : outputs.entrySet()) {
                tx.addOutput(entry.getValue(), entry.getKey());
            }
        }

        int inputIndex = 0;
        // Every input should correspond to an existing transaction
		if (inputs != null) {

		    for (Map.Entry<byte [], Integer> entry: inputs.entrySet()) {

                Transaction previousTransaction = this.transactions.get(entry.getKey());
                Integer previousTransactionOutputIndex =  entry.getValue();
                tx.addInput(previousTransaction.getHash(), previousTransactionOutputIndex);
                byte[] rawData = tx.getRawDataToSign(inputIndex);
                tx.addSignature(sign(sender.getPrivate(), rawData), inputIndex);
                inputIndex++;
            }
        }

        if (extraInputs != null){
		    for (Map.Entry<byte [], Integer> entry: extraInputs.entrySet()) {

                Transaction previousTransaction = this.transactions.get(entry.getKey());
                Integer previousTransactionOutputIndex =  entry.getValue();
                tx.addInput(previousTransaction.getHash(), previousTransactionOutputIndex);
                byte[] rawData = tx.getRawDataToSign(inputIndex);
                tx.addSignature(sign(sender.getPrivate(), rawData), inputIndex);
                inputIndex++;
            }
        }

		tx.sign();
		this.transactions.put(tx.getHash(), tx);
		return tx;
	}*/

    /**
     * Creates a UTXOPool with an initial unspent transaction
     * @param owner
     * @param initialRootValue
     * @return Map with the hash of the root transaction and the UTXOPool
     */
	/*
	public  Map<byte[], UTXOPool> createUtxoPool(KeyPair owner, double initialRootValue) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // The transaction output of the root transaction is the initial unspent output.
        UTXOPool utxoPool = new UTXOPool();
        Map m = new HashMap<byte[], UTXOPool>();
        
        if(initialRootValue > 0d && owner != null) {
            Transaction rootTransaction = createRootTransaction(owner, initialRootValue);
            UTXO utxo = new UTXO(rootTransaction.getHash(), 0);
            utxoPool.addUTXO(utxo, rootTransaction.getOutput(0));
            m.put(rootTransaction.getHash(), utxoPool);
        }
        
		return m;
	}*/
	

	public static byte[] createRandomByteArray(int length) {
        byte[] b = new byte[length];
        new Random().nextBytes(b);
        return b;
    }

}
