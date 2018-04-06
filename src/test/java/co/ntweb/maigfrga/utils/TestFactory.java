package co.ntweb.maigfrga.utils;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import co.ntweb.maigfrga.week1.Main;
import co.ntweb.maigfrga.week1.Transaction;
import co.ntweb.maigfrga.week1.UTXO;
import co.ntweb.maigfrga.week1.UTXOPool;

public class TestFactory {
	private final ThreadLocalRandom random;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    private  List<Transaction> transactions;

	public TestFactory() {
		this.random = ThreadLocalRandom.current();
	    this.transactions = new ArrayList<Transaction>();
	}

	public KeyPair createAddress() throws NoSuchAlgorithmException {
		return KeyPairGenerator.getInstance("RSA").generateKeyPair();
		
	}

     /*
     * Set up the root transaction:
     */
	public Transaction createRootTransaction(KeyPair owner, double value) {
		final Transaction tx = new Transaction();
        tx.addOutput(value, owner.getPublic());

        // This value has no meaning, but tx.getRawDataToSign(0) will access it in prevTxHash;
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);

        tx.finalize();
        this.transactions.add(tx);
        return tx;
	}

	public Transaction createTransaction(KeyPair sender, Map<PublicKey, Double> outputs) {

		final Transaction tx = new Transaction();

        if (outputs != null) {
            for (Map.Entry<PublicKey, Double> entry : outputs.entrySet()) {
                tx.addOutput(entry.getValue(), entry.getKey());
            }
        }

		//if ( inputs != null ) {
		//	for (Transaction.Input input: inputs) {
		//		tx.addInput(input.prevTxHash, input.);
		//	}
		//}

		tx.finalize();
		return tx;
	}

    /**
     * Creates a UTXOPool with an initial unspent transaction
     * @param owner
     * @param initialRootValue
     * @return UTXOPool
     */
	public UTXOPool createUtxoPool(KeyPair owner, double initialRootValue) {
        // The transaction output of the root transaction is the initial unspent output.
        UTXOPool utxoPool = new UTXOPool();
        if(initialRootValue > 0d && owner != null) {
            Transaction rootTransaction = createRootTransaction(owner, initialRootValue);
            UTXO utxo = new UTXO(rootTransaction.getHash(), 0);
            utxoPool.addUTXO(utxo, rootTransaction.getOutput(0));
        }
		return utxoPool;
	}
	
	
	public Transaction createTransaction(int maxInputs, int maxOutputs, boolean isValid) {
		final int nInputs = random.nextInt(maxInputs) + 1;
		final int nOutputs = random.nextInt(maxOutputs) + 1;
		Transaction tx = new Transaction();
		
		// create inputs
		//double inputValue = 0;
		//for (int j = 0; j < nInputs; j++) {
		//	UTXO utxo = null;
		//	if (isClaimingOutputsNotInPool && isRandomSelection()) {
		//		do {
		//			utxo = utxoExtraList.get(random.nextInt(utxoExtraList.size()));
		//		} while (!utxosSeen.add(utxo));
		//		inputValue += utxoExtraPool.getTxOutput(utxo).value;
		//		corrupted = true;
		//	} else {
		//		do {
		//			utxo = utxoList.get(random.nextInt(utxoList.size()));
		//		} while (!utxosSeen.add(utxo));
		//		inputValue += utxoPool.getTxOutput(utxo).value;
	    //        if (isClaimingUtxoSeveralTimes && isRandomSelection()) {
	    //            utxosToRepeat.add(utxo);
	    //            corrupted = true;
	    //        }
		//	}
		//	tx.addInput(utxo.getTxHash(), utxo.getIndex());
		//	utxoAtIndex.put(j, utxo);
		//}
		
		
		
		return tx;
	}

}
