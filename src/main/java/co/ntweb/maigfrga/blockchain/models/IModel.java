package co.ntweb.maigfrga.blockchain.models;

public interface IModel {
	public  byte[] getRawData();
	public int BYTE_SIZE = 8;
	public int BLOCK_SIZE = 256;
}
