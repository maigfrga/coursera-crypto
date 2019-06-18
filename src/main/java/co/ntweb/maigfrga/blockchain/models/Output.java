package co.ntweb.maigfrga.blockchain.models;

import java.security.PublicKey;




public class Output  implements Imodel {
    /** value in bitcoins of the output */
    private final double value;
    /** the address or public key of the recipient */
    private final PublicKey address;

    public Output(double v, PublicKey addr) {
        value = v;
        address = addr;
    }

	public double getValue() {
		return value;
	}

	public PublicKey getAddress() {
		return address;
	}

	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

}
