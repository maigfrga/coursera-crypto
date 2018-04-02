package co.ntweb.maigfrga.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import co.ntweb.maigfrga.week1.Transaction;
import co.ntweb.maigfrga.week1.UTXO;
import co.ntweb.maigfrga.week1.UTXOPool;

public class TestFactory {
	private final ThreadLocalRandom random;

	public TestFactory() {
		this.random = ThreadLocalRandom.current();
		
	}

	public KeyPair createAddress(int peopleSize) throws NoSuchAlgorithmException {
		return (KeyPairGenerator.getInstance("RSA").generateKeyPair());
		
	}
	
	
	
	public UTXOPool createUtxoPool(List<KeyPair> people, int utxoTxNumber, int maxUtxoTxOutput, double maxValue,
			Map<UTXO, KeyPair> utxoToKeyPair) {
		final UTXOPool utxoPool = new UTXOPool();
		Map<Integer, KeyPair> keyPairAtIndex = new HashMap<>();

		for (int i = 0; i < utxoTxNumber; i++) {
			int num = maxUtxoTxOutput;
			Transaction tx = createTxWithOutputs(people, maxValue, keyPairAtIndex, num);
			// add all tx outputs to utxo pool
			addTxOutputsToPool(utxoPool, keyPairAtIndex, utxoToKeyPair, num, tx);
		}
		return utxoPool;
	}
	
	
	public Transaction createTransaction(int maxInputs, int maxOutputs, boolean isValid) {
		final int nInputs = random.nextInt(maxInputs) + 1;
		final int nOutputs = random.nextInt(maxOutputs) + 1;
		Transaction tx = new Transaction();
		
		// create inputs
		double inputValue = 0;
		for (int j = 0; j < nInputs; j++) {
			UTXO utxo = null;
			if (isClaimingOutputsNotInPool && isRandomSelection()) {
				do {
					utxo = utxoExtraList.get(random.nextInt(utxoExtraList.size()));
				} while (!utxosSeen.add(utxo));
				inputValue += utxoExtraPool.getTxOutput(utxo).value;
				corrupted = true;
			} else {
				do {
					utxo = utxoList.get(random.nextInt(utxoList.size()));
				} while (!utxosSeen.add(utxo));
				inputValue += utxoPool.getTxOutput(utxo).value;
	            if (isClaimingUtxoSeveralTimes && isRandomSelection()) {
	                utxosToRepeat.add(utxo);
	                corrupted = true;
	            }
			}
			tx.addInput(utxo.getTxHash(), utxo.getIndex());
			utxoAtIndex.put(j, utxo);
		}
		
		
		
		return tx;
	}

}
