package co.ntweb.maigfrga.blockchain;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;

import co.ntweb.maigfrga.blockchain.models.Input;
import co.ntweb.maigfrga.blockchain.models.Output;
import co.ntweb.maigfrga.blockchain.models.Transaction;
import co.ntweb.maigfrga.blockchian.utils.TestFactory;

public class TestModels {
	@Test
	public void testOutput() throws NoSuchAlgorithmException {
		KeyPair aliceKeys = Crypto.generateKeys();
		Output out = new Output(10d, aliceKeys.getPublic());
		assertNotNull(out.getRawData()); 
	}
	
	@Test
	public void testInput() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException { 
		Input in = new Input(TestFactory.createRandomByteArray(32), 0);
		assertNotNull(in.getRawData());
		assertEquals(null, in.getSignature());	
	}
	
	@Test
	public void testTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		KeyPair aliceKeys = Crypto.generateKeys();
		
		List<Output> outputs = ImmutableList.of(new Output(10d, aliceKeys.getPublic()));		
		List<Input> inputs = ImmutableList.of(new Input(TestFactory.createRandomByteArray(32), 0));
		Transaction tx = new Transaction(inputs, outputs);
		assertEquals(1, tx.getInputs().size());
		assertEquals(1, tx.getOutputs().size());

	}
	
	@Test
	public void testSignatures() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		KeyPair aliceKeys = Crypto.generateKeys();
		
		List<Output> outputs = ImmutableList.of(new Output(10d, aliceKeys.getPublic()));		
		List<Input> inputs = ImmutableList.of(new Input(TestFactory.createRandomByteArray(32), 0));
		Transaction tx = new Transaction(inputs, outputs);
		
		assertEquals(null, tx.getInputs().get(0).getSignature());
		byte[] oldTxHash = tx.getHash();
		
		tx.signInput(aliceKeys.getPrivate(), 0);
		assertNotEquals(null, tx.getInputs().get(0).getSignature());
		assertNotEquals(oldTxHash, tx.getHash());
	}
	
	
	

}
