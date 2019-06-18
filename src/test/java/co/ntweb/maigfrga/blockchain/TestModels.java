package co.ntweb.maigfrga.blockchain;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.junit.Test;

import co.ntweb.maigfrga.blockchain.models.Input;
import co.ntweb.maigfrga.blockchain.models.Output;

public class TestModels {
	@Test
	public void testOutput() throws NoSuchAlgorithmException {
		KeyPair aliceKeys = Crypto.generateKeys();
		Output out = new Output(10d, aliceKeys.getPublic());
		assertNotNull(out.getRawData()); 
	}
	
	@Test
	public void testInput() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		KeyPair aliceKeys = Crypto.generateKeys();
		byte[] initialHash = BigInteger.valueOf(0).toByteArray();
		Input in = new Input(initialHash, 0);
		assertNotNull(in.getRawData());
	}

}
