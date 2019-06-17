package co.ntweb.maigfrga.blockchain;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;





public class Crypto {

    /**
     * @return true is {@code signature} is a valid digital signature of {@code message} under the
     *         key {@code pubKey}. Internally, this uses RSA signature, but the student does not
     *         have to deal with any of the implementation details of the specific signature
     *         algorithm
     */
    public static boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature) {
        Signature sig = null;
        try {
            sig = Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sig.initVerify(pubKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            sig.update(message);
            return sig.verify(signature);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;

    }
    
    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
    	return KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }
    
    public static byte[] sign(PrivateKey sk, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    	Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(sk);
        sig.update(message);
    	return sig.sign();
    }
    
 
}
