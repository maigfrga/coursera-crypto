package co.ntweb.maigfrga.blockchain;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import co.ntweb.maigfrga.blockchain.models.Input;
import co.ntweb.maigfrga.blockchain.models.Output;
import co.ntweb.maigfrga.blockchain.models.Transaction;
import com.google.common.collect.ImmutableList;

public class TestTransaction {
	
	public void testGenesisBlock() throws NoSuchAlgorithmException, SignatureException {
		KeyPair scroogeKeys = Crypto.generateKeys();
		final byte[] initialHash = BigInteger.valueOf(0).toByteArray();
		final Input input = new Input(initialHash, 0);
		final Output output = new Output(10, scroogeKeys.getPublic());
		
		Transaction tx = new Transaction(ImmutableList.of(input), ImmutableList.of(output));
        

        // This value has no meaning, but tx.getRawDataToSign(0) will access it in prevTxHash;
       
      
        //tx.signTx(scroogeKeys.getPrivate(), 0);
	}

}
