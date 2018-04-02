package co.ntweb.maigfrga.week1;

public class TxHandler {

	private UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
    	this.utxoPool = utxoPool;
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    	boolean isValid = false;
    	
    	// Total outputs by transaction
    	int totalOutpus = tx.getOutputs().size();
    	int validTransactionCounter = 0;
    	// True if all outputs claimed by {@code tx} are in the current UTXO pool,
    	boolean outputsExists = false;
    	
    	
    	for (UXTO uxto: this.utxoPool.getAllUTXO()) {
    		
    	}
    	
    	
    	for(Transaction.Output o: tx.getOutputs()) {
    		if (this.utxoPool.contains(o.value)) {
    			validTransactionCounter++;
    		}
    		
    		
    	}
    	return isValid;        
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	return null;
    }

}
