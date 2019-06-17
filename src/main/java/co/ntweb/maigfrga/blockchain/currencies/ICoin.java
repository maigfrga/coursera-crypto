package co.ntweb.maigfrga.blockchain.currencies;

public interface ICoin {

	default String turnAlarmOn() {
        return "Turning the vehicle alarm on.";
    }
	
	boolean isValid();
	
	void initialize();
	
}
