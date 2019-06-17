package co.ntweb.maigfrga.blockchain;
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
import co.ntweb.maigfrga.blockchain.handlers.MaxFeeTxHandler;
import co.ntweb.maigfrga.blockchain.handlers.TxHandler;
import co.ntweb.maigfrga.blockchain.models.Transaction;
import co.ntweb.maigfrga.blockchain.models.UTXO;
import co.ntweb.maigfrga.blockchain.models.UTXOPool;
import co.ntweb.maigfrga.blockchian.utils.TestFactory;

import org.junit.Test;

public class TestHandleTx {
	private TestFactory factory;
	@Before
	public void initialize() {
		factory = new TestFactory();
	}

	@After
	public void destroy() {
		factory = null;
	}


	// Test if a valid transaction is handled properly
	@Test
	public void testHandleTxValidTransaction() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();

   		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // placing an input to scrooge with the original initial value of the chain
        Map<byte[], Integer> inputs = new HashMap<>();
        inputs.put(rootTransaction.getHash(), 0);

		// placing an output from scrooge to alice
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 5d);

        Transaction scroogeToAlicePayment = factory.createTransaction(pk_scrooge, inputs, outputs, null);

		final TxHandler txHandler = new TxHandler(pool);
        // check if the transaction is valid
		assertTrue(txHandler.isValidTx(scroogeToAlicePayment));

        // Unspent transaction that represents money entitled to scrooge
		UTXO scroogeUtxo = new UTXO(
		        scroogeToAlicePayment.getInput(0).prevTxHash, scroogeToAlicePayment.getInput(0).outputIndex);
		assertTrue(txHandler.containsUTXO(scroogeUtxo));

		// Unspent transaction that represents money transfer from scrooge to alice
        // should not exists in the pool until transaccion processed
        UTXO aliceUtxo = new UTXO(scroogeToAlicePayment.getHash(), 0);
		assertFalse(txHandler.containsUTXO(aliceUtxo));

	    Transaction[] transactionList = new Transaction[1];
	    transactionList[0] = scroogeToAlicePayment;
		Transaction[] validTransactions = txHandler.handleTxs(transactionList);

		// set of valid transcation must be 2: payment from scrooge to alice, and
		// remaining money going back to scrooge
		assertTrue(validTransactions.length == 1);
        assertTrue(validTransactions[0].equals(scroogeToAlicePayment));
        // unspent transaction entitled to scrooge should not exists
        assertFalse(txHandler.containsUTXO(scroogeUtxo));

        // unspent transaction entitled to alice should exists
        assertTrue(txHandler.containsUTXO(aliceUtxo));

	}

	// test a transaction with a double spend attempt won't be considered as valid
	@Test
    public void testDoubleSpend() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();
        KeyPair pk_bob = factory.createAddress();

   		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // placing an input to scrooge with the original initial value of the chain
        Map<byte[], Integer> inputs = new HashMap<>();
        inputs.put(rootTransaction.getHash(), 0);

		// placing an output from scrooge to alice
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 10d);

        Transaction scroogeToAlicePayment = factory.createTransaction(pk_scrooge, inputs, outputs, null);


        // Double spend attempt, scrooge tries to issue already spent money to bob
        outputs = new HashMap<>();
        outputs.put(pk_bob.getPublic(), 10d);
        Transaction scroogeToBobPayment = factory.createTransaction(pk_scrooge, inputs, outputs, null);


		final TxHandler txHandler = new TxHandler(pool);
        // check if the transactions are valid
		assertTrue(txHandler.isValidTx(scroogeToAlicePayment));
        assertTrue(txHandler.isValidTx(scroogeToBobPayment));

        // Unspent transaction that represents money entitled to scrooge
		UTXO scroogeUtxo = new UTXO(
		        scroogeToAlicePayment.getInput(0).prevTxHash, scroogeToAlicePayment.getInput(0).outputIndex);
		assertTrue(txHandler.containsUTXO(scroogeUtxo));

		// Unspent transaction that represents money transfer from scrooge to alice
        // should not exists in the pool until transaccion processed
        UTXO aliceUtxo = new UTXO(scroogeToAlicePayment.getHash(), 0);
		assertFalse(txHandler.containsUTXO(aliceUtxo));

	    Transaction[] transactionList = new Transaction[2];
	    transactionList[0] = scroogeToAlicePayment;
	    transactionList[1] = scroogeToBobPayment;

	    // 2 transaction, only 1 is valid
	    assertTrue(transactionList.length == 2);
		Transaction[] validTransactions = txHandler.handleTxs(transactionList);

		// set of valid transcation must be 2: payment from scrooge to alice, and
		// remaining money going back to scrooge
		assertTrue(validTransactions.length == 1);
        assertTrue(validTransactions[0].equals(scroogeToAlicePayment));
        // unspent transaction entitled to scrooge should not exists
        assertFalse(txHandler.containsUTXO(scroogeUtxo));

        // unspent transaction entitled to alice should exists
        assertTrue(txHandler.containsUTXO(aliceUtxo));
    }


    /**
     * Choose most profitable transaction among conflicting transactions
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     */
    @Test
    public void testMaxFeeTxHandler() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		KeyPair pk_scrooge = factory.createAddress();
        KeyPair pk_alice = factory.createAddress();
        KeyPair pk_bob = factory.createAddress();

   		// Create a UTXO pool that has an initial root transaction with a valid
        // unspent trasaction
        Map<byte[], UTXOPool> m = factory.createUtxoPool(pk_scrooge, 10);
        byte[] rootHash = (byte[]) (m.keySet().toArray())[0];
		UTXOPool pool = (UTXOPool) m.values().toArray()[0];
        Transaction rootTransaction = factory.getTransaction(rootHash);

        // placing an input to scrooge with the original initial value of the chain
        Map<byte[], Integer> inputs = new HashMap<>();
        inputs.put(rootTransaction.getHash(), 0);

		// placing an output from scrooge to alice
        Map<PublicKey, Double> outputs = new HashMap<>();
        outputs.put(pk_alice.getPublic(), 10d);

        Transaction scroogeToAlicePayment = factory.createTransaction(pk_scrooge, inputs, outputs, null);


        // Double spend attempt, scrooge tries to issue money bob that refers same unspent output issued to alice
        outputs = new HashMap<>();
        outputs.put(pk_bob.getPublic(), 5d);
        Transaction scroogeToBobPayment = factory.createTransaction(pk_scrooge, inputs, outputs, null);


		final MaxFeeTxHandler txHandler = new MaxFeeTxHandler(pool);
        // check if the transactions are valid
		assertTrue(txHandler.isValidTx(scroogeToAlicePayment));
        assertTrue(txHandler.isValidTx(scroogeToBobPayment));

        // Unspent transaction that represents money entitled to scrooge
		UTXO scroogeUtxo = new UTXO(
		        scroogeToAlicePayment.getInput(0).prevTxHash, scroogeToAlicePayment.getInput(0).outputIndex);
		assertTrue(txHandler.containsUTXO(scroogeUtxo));

		// Unspent transaction that represents money transfer from scrooge to alice
        // should not exists in the pool until transaccion processed
        UTXO bobUtxo = new UTXO(scroogeToBobPayment.getHash(), 0);
		assertFalse(txHandler.containsUTXO(bobUtxo));

	    Transaction[] transactionList = new Transaction[2];
	    transactionList[0] = scroogeToAlicePayment;
	    transactionList[1] = scroogeToBobPayment;

	    // 2 transaction, only 1 is valid
	    assertTrue(transactionList.length == 2);
		Transaction[] validTransactions = txHandler.handleTxs(transactionList);

		// set of valid transcation must be 2: payment from scrooge to alice, and
		// remaining money going back to scrooge
		assertTrue(validTransactions.length == 1);
        assertTrue(validTransactions[0].equals(scroogeToBobPayment));
        // unspent transaction entitled to scrooge should not exists
        assertFalse(txHandler.containsUTXO(scroogeUtxo));

        // unspent transaction entitled to alice should exists
        assertTrue(txHandler.containsUTXO(bobUtxo));
    }


}
