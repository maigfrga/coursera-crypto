package co.ntweb.maigfrga.blockchain;

// Copyright (C) 2016-2017 Enrique Albertos
// Distributed under the GNU GPL v2 software license


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.After;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import co.ntweb.maigfrga.blockchain.handlers.TxHandler;
import co.ntweb.maigfrga.blockchain.models.Transaction;
import co.ntweb.maigfrga.blockchain.models.UTXOPool;
import co.ntweb.maigfrga.blockchian.utils.TestFactory;

import org.junit.Test;
/**
 * Unit tests for {@link TxHandler#isValidTx(Transaction)}
 * <p>
 * Test Strategy:
 * Test 1: test isValidTx() with valid transactions
 * Test 2: test isValidTx() with transactions containing signatures of incorrect data
 * Test 3: test isValidTx() with transactions containing signatures using incorrect private keys
 * Test 4: test isValidTx() with transactions whose total output value exceeds total input value
 * Test 5: test isValidTx() with transactions that claim outputs not in the current utxoPool
 * Test 6: test isValidTx() with transactions that claim the same UTXO multiple times
 * Test 7: test isValidTx() with transactions that contain a negative output value
 * 
 * @author ealbertos
 *
 */

public class IsValidTest {

	private TestFactory factory;

	@Before
	public void initialize() {
		factory = new TestFactory();
	}

	@After
	public void destroy() {
		factory = null;
	}

	private static void assertTestSetIsValid(final UtxoTestSet utxoTestSet) {
		final ValidationLists<Transaction> trxsValidation = utxoTestSet.getValidationLists();
		
		// Instantiate student solution
		final TxHandler txHandler = new TxHandler(utxoTestSet.getUtxoPool());
		
		// Check validation of all the transactions in the set
		for (Transaction tx: trxsValidation.allElements()) {
			assertEquals(txHandler.isValidTx(tx), trxsValidation.isValid(tx) );
		}
	}

	// Test that that all outputs claimed by a given transaction are in the current UTXO pool
	@Test
	public void testUTXOExistsInPool() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {

		KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();
        KeyPair pk_bob = factory.createAddress();

		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction        
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);
        
        // hash of the Transaction whose output is being used
        Map<byte[], Integer> inputs = new HashMap<>();
        // placing and input that refers to transaction hash and output index that originates the input
        inputs.put(rootTransaction.getHash(), 0);
     
        
        // List of inputs to create a transaction, every input is a hash of the transaction 
        // and the output index
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 5d);
        
        Transaction validTransaction = factory.createTransaction(pk_scrooge, inputs, outputs, null);

		final TxHandler txHandler = new TxHandler(pool);
        // check if the transaction is valid
		assertTrue(txHandler.isValidTx(validTransaction));


		// Create a second UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m2 = factory.createUtxoPool(pk_alice, 30);
        byte[] rootHash2 = (byte[]) m2.keySet().toArray()[0];
		UTXOPool pool2 = (UTXOPool) m2.values().toArray()[0];
        Transaction rootTransaction2 = factory.getTransaction(rootHash2);


		// hash of the Transaction whose output is being used
        Map<byte[], Integer> inputs2 = new HashMap<>();
        // placing and input that refers to transaction hash and output index that originates the input
        // hash of the Transaction whose output is being used
        // placing and input that refers to transaction hash and output index that originates the input
        inputs2.put(rootTransaction2.getHash(), 0);


        // List of inputs to create a transaction, every input is a hash of the transaction
        // and the output index
        Map<PublicKey, Double> outputs2 = new HashMap<>();
        outputs2.put(pk_bob.getPublic(), 5d);

        Transaction notInPoolTransaction = factory.createTransaction(pk_scrooge, inputs, outputs, null);

		final TxHandler txHandler2 = new TxHandler(pool2);
        assertFalse(txHandler2.isValidTx(notInPoolTransaction));
	}



	// Test if the signatures on each input of {@code tx} are valid
    @Test
    public void testInvalidSignatures() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
 		KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();

		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // hash of the Transaction whose output is being used
        Map<byte[], Integer> inputs = new HashMap<>();
        // placing and input that refers to transaction hash and output index that originates the input
        inputs.put(rootTransaction.getHash(), 0);


        // List of inputs to create a transaction, every input is a hash of the transaction
        // and the output index
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 5d);

        Transaction validTransaction = factory.createTransaction(pk_scrooge, inputs, outputs, null);

		final TxHandler txHandler = new TxHandler(pool);
		// assigning an invalid signature to the transaction
		byte[] fakeSignature = TestFactory.createRandomByteArray(256);
		validTransaction.addSignature(fakeSignature, 0);
    	assertFalse(txHandler.isValidTx(validTransaction));

    }

    // test that no UTXO is claimed multiple times.
    @Test
    public void testRepeatedUTXO()  throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
 		KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();
        KeyPair pk_bob = factory.createAddress();

		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // hash of the Transaction whose output is being used
        Map<byte[], Integer> inputs = new HashMap<>();
        // placing and input that refers to transaction hash and output index that originates the input
        inputs.put(rootTransaction.getHash(), 0);

        // input duplication
        Map<byte[], Integer> extraInputs = new HashMap<>();
        extraInputs.put(rootTransaction.getHash(), 0);
        // List of inputs to create a transaction, every input is a hash of the transaction
        // and the output index
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 5d);
        outputs.put(pk_bob.getPublic(), 5d);
        Transaction validTransaction = factory.createTransaction(pk_scrooge, inputs, outputs, extraInputs);

		final TxHandler txHandler = new TxHandler(pool);
        // check if the transaction is valid
		assertFalse(txHandler.isValidTx(validTransaction));

    }

    // test that all of {@code tx}s output values are non-negative
    @Test
    public void testNegativeValues() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    	KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();

		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // hash of the Transaction whose output is being used
        Map<byte[], Integer> inputs = new HashMap<>();
        // placing and input that refers to transaction hash and output index that originates the input
        inputs.put(rootTransaction.getHash(), 0);


        // List of inputs to create a transaction, every input is a hash of the transaction
        // and the output index
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), -5d);

        Transaction validTransaction = factory.createTransaction(pk_scrooge, inputs, outputs, null);

		final TxHandler txHandler = new TxHandler(pool);
        // check if the transaction is valid
		assertFalse(txHandler.isValidTx(validTransaction));
    }

    // test that the sum of {@code tx}s input values is greater than or equal to the sum of its output
    @Test
    public void testOverdraft() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    	KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();

		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // hash of the Transaction whose output is being used
        Map<byte[], Integer> inputs = new HashMap<>();
        // placing and input that refers to transaction hash and output index that originates the input
        inputs.put(rootTransaction.getHash(), 0);


        // List of inputs to create a transaction, every input is a hash of the transaction
        // and the output index
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 15d);

        Transaction validTransaction = factory.createTransaction(pk_scrooge, inputs, outputs, null);

		final TxHandler txHandler = new TxHandler(pool);
        // check if the transaction is valid
		assertFalse(txHandler.isValidTx(validTransaction));
    }
	// Test 1: test isValidTx() with valid transactions
	@Test
	public void testIsValidWithValidTransactions()
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing		
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setCorruptedPercentage(0) // All valid transactions
				.build();
		// check against student solution
		assertTestSetIsValid(utxoTestSet);
		
	}

	
	// Test 2: test isValidTx() with transactions containing signatures of incorrect data
	@Test
	public void testIsValidWithInvalidSignatures() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing				
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setForceCorruptedSignature(true)
				.setCorruptedPercentage(.20) // probability of 20% of invalid transactions
				.build();
		
		// check against student solution
		assertTestSetIsValid(utxoTestSet);
		
	}
	
	// Test 3: test isValidTx() with transactions containing signatures using incorrect private keys
	@Test
	public void testIsValidSignaturesWithInvalidPrivateKeys() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing				
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setInvalidPrivateKeys(true) // corrupt the private key that signs
				.setCorruptedPercentage(.20) // probability of 20% of invalid transactions
				.build();
		
		// check against student solution
		assertTestSetIsValid(utxoTestSet);
		
	}

	// Test 4: test isValidTx() with transactions whose total output value exceeds total input value
	@Test
	public void testIsValidTotalOutputExceedsTotalInput() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing				
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setInvalidTotals(true)  // create transactions with invalid total value
				.setCorruptedPercentage(.20) // probability of 20% of invalid transactions
				.build();
		
		// check against student solution
		assertTestSetIsValid(utxoTestSet);
		
	}

	
	// Test 5: test isValidTx() with transactions that claim outputs not in the current utxoPool
	@Test
	public void testIsValidTransactionsClamingOuputsNotInThePool() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing				
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setClaimingOutputsNotInPool(true)  // create transactions claiming outputs not in the pool
				.setCorruptedPercentage(.20) // probability of 20% of invalid transactions
				.build();
		
		// check against student solution
		assertTestSetIsValid(utxoTestSet);
		
	}
	
    // Test 6: test isValidTx() with transactions that claim the same UTXO multiple times
	@Test
	public void testIsValidTransactionsClaimingTheSameUTXOSeveralTimes() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing				
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setClaimingUtxoSeveralTimes(true)  // create transactions claiming the same output several times
				.setCorruptedPercentage(.20) // probability of 20% of invalid transactions
				.build();

		assertTestSetIsValid(utxoTestSet);
		
	}
	
    // Test 7: test isValidTx() with transactions that contain a negative output value
	@Test
	public void testIsValidTransactionsWithNegativeOutput() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// Create a new set of transactions for testing				
		final UtxoTestSet utxoTestSet = UtxoTestSet.builder()
				.setPeopleSize(10)
				.setUtxoTxNumber(10)
				.setMaxUtxoTxOutput(10)
				.setMaxValue(200)
				.setTxPerTest(10)
				.setMaxInput(10)
				.setMaxOutput(10)
				.setNegativeOutputs(true)  // create transactions with negative values
				.setCorruptedPercentage(.20) // probability of 20% of invalid transactions
				.build();

		assertTestSetIsValid(utxoTestSet);
		
	}
	



}
