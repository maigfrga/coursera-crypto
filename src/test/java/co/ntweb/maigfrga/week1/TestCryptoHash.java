package co.ntweb.maigfrga.week1;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import org.junit.Test;


/**
 * Cryptographic hash functions

- Mathematical functions
  - Have three attributes

    - Can take any string as input, absolutely any string of any size

    - Produce a fixed-size output

    - It has to be efficiently computable: given an input, compute the output
      in a razonable amount of time

  - Security properties

    - Collision Free:  H(x) never will be equal H(y) |  x != y , collisions exists, but
      is hard to anyone to find them. The mathematical operation that makes possible to find
      a collision takes several hundred million years

    - Hiding: Giving H(X) it is very hard to find the original value of X. If r is chosen from
      a probability distribution that has high min-entropy, then G(r|H) it is infeasible to find X.
      The concept is to find a random enough r value that will be concatenanted to the original X

    - Puzzle friendly: for every possible output value y, if k is chosen from a distribution with
      high min entropy, then is infeasible to find x such that H(k|x) = y.
      Puzzle friendly property implies that not solving strategy is better than trying random
      values of X.
 * @author manuel
 *
 */


public class TestCryptoHash {

	@Test
	public void testBasicHashing() throws NoSuchAlgorithmException {
		
		String s1 = "12345";
		String s2 = "12345";
		String s3 = "12346111111111afddfaktylm5j45mh42h123h432h5k5h65h6hwegwetrwe45454grwtwrtw      ertwetwtr wwt wt";
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
	    byte[] s1HashBytes = md.digest(s1.getBytes(StandardCharsets.UTF_8));
	    byte[] s2HashBytes = md.digest(s2.getBytes(StandardCharsets.UTF_8));
	    byte[] s3HashBytes = md.digest(s3.getBytes(StandardCharsets.UTF_8));

	    // bytes to hex
	    StringBuilder sb = new StringBuilder();
	    for (byte b : s1HashBytes) {
	        sb.append(String.format("%02x", b));
	    }    
	    String s1Hash = sb.toString();

	    sb = new StringBuilder();
	    for (byte b : s2HashBytes) {
	        sb.append(String.format("%02x", b));
	    }	    
	    String s2Hash = sb.toString();

	    sb = new StringBuilder();
	    for (byte b : s3HashBytes) {
	        sb.append(String.format("%02x", b));
	    }
	    String s3Hash = sb.toString();
	    
	    
	    assertEquals(s1Hash, s2Hash);
	    assertNotEquals(s1Hash, s3Hash);
	    assertEquals(s3Hash.length(), s1Hash.length());
	    assertNotEquals(s1, s1Hash);
	    assertEquals(256, s1HashBytes.length * 8);
	    assertEquals(s1Hash.length(), s3Hash.length());
	    System.out.print(s1Hash);
	}

	/**
	 *     Digital Signatures API
	        - (sk, pk) := generateKeys(keysize)
	            - sk: secret key
	            - pk: public key

	        - sig := sign(sk,message)

	        - isValid := verify(pk, message, sig)
	 * @author manuel
	 *
	 */
	@Test
	public void testSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		KeyPair aliceKeys = Crypto.generateKeys();
		KeyPair bobKeys = Crypto.generateKeys();
		
		byte[] aliceSays = "Hello, greetings from Alice".getBytes(StandardCharsets.UTF_8);
		
		byte[] aliceSign = Crypto.sign(aliceKeys.getPrivate(), aliceSays);
		
		assertTrue(Crypto.verifySignature(aliceKeys.getPublic(), aliceSays, aliceSign));
		assertFalse(Crypto.verifySignature(bobKeys.getPublic(), aliceSays, aliceSign));
	}

}
