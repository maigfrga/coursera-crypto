package co.ntweb.maigfrga.blockchain.models;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.ArrayList;




public class Output implements Imodel {
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
		ArrayList<Byte> rawData = new ArrayList<Byte>();
		
		ByteBuffer bf = ByteBuffer.allocate(Double.SIZE / 8);
        bf.putDouble(this.value);
        byte[] value = bf.array();
        for (int i = 0; i < value.length; i++) {
            rawData.add(value[i]);
        }
        
        byte[] addressBytes = this.address.getEncoded();
        for (int i = 0; i < addressBytes.length; i++) {
            rawData.add(addressBytes[i]);
        }

        byte[] raw = new byte[rawData.size()];
	    int i = 0;
	    for (Byte b : rawData)
	    	raw[i++] = b;
		 
		return raw;
	}

}
