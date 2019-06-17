package co.ntweb.maigfrga.blockchain.handlers;

import java.util.*;

import co.ntweb.maigfrga.blockchain.Crypto;
import co.ntweb.maigfrga.blockchain.models.Input;
import co.ntweb.maigfrga.blockchain.models.Output;
import co.ntweb.maigfrga.blockchain.models.Transaction;
import co.ntweb.maigfrga.blockchain.models.UTXO;
import co.ntweb.maigfrga.blockchain.models.UTXOPool;


public class MaxFeeTxHandler {

    private UTXOPool utxoPool;
    public MaxFeeTxHandler(UTXOPool utxoPool) {
    	this.utxoPool = new UTXOPool(utxoPool);
    }

    public boolean containsUTXO(UTXO utxo) {
       return this.utxoPool.contains(utxo);
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
    	UTXO uxto = null;
    	int idx = 0;
    	double totalInputs = 0d;
    	double totalOutputs = 0d;
    	List<UTXO> utxoList = new ArrayList<UTXO>();

        if(null == tx) return false;

    	for(Input i: tx.getInputs()) {
    		uxto = new UTXO(i.getPrevTxHash(), i.getOutputIndex());

            // Check if all outpus are in the unspent transactions pool
            if (!this.utxoPool.contains(uxto)) {
                return false;
            }

            // Getting the output associated to the current input
            Output out = this.utxoPool.getTxOutput(uxto);
            if (out == null) return false;
            totalInputs += out.getValue();


            // check that the signatures on each input of {@code tx} are valid
            if (!Crypto.verifySignature(out.getAddress(), tx.getRawDataToSign(idx), i.getSignature())) {
                return false;
            }

            // check no UTXO is claimed multiple times by {@code tx}
            if(utxoList.contains(uxto)) {
                return false;
            } else {
                utxoList.add(uxto);
            }

            idx++;

    	}

    	// checks {@code tx}s output values are non-negative
    	for(Output o: tx.getOutputs()) {
    	    if (o.getValue() < 0d) {
    	        return false;
            }
            totalOutputs += o.getValue();
    	}

        // check if the sum of {@code tx}s input values is greater than or equal to the sum of its output
    	if (totalOutputs > totalInputs) return false;

    	return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleMaxTxs(Transaction[] possibleTxs) {
        if (possibleTxs == null || possibleTxs.length == 0) return null;

        Transaction t = null;
        List<Transaction> tList = new ArrayList<Transaction>();
        UTXO uxto = null;
        Transaction[] tArray = null;

        for(int i=0; i< possibleTxs.length; i++) {
            t = possibleTxs[i];
            if (this.isValidTx(t)) {
                double totalInputs = 0d;
                double totalOutpus = 0d;

                for(Input input: t.getInputs()) {
                    uxto = new UTXO(input.getPrevTxHash(), input.getOutputIndex());

                    // Getting the output associated to the current input
                    Output out = this.utxoPool.getTxOutput(uxto);
                    if (out == null) continue;
                    totalInputs += out.getValue();
                    this.utxoPool.removeUTXO(uxto);
                }

                int outIndex = 0;
                for(Output output: t.getOutputs()) {
                    uxto = new UTXO(t.getHash(), outIndex);
                    outIndex++;
                    this.utxoPool.addUTXO(uxto, output);
                    totalOutpus += output.getValue();
                }
                uxto = null;

                tList.add(t);
            }
            t = null;
        }

        if(tList.size() > 0) {
            int idx = 0;
            tArray = new Transaction[tList.size()];
            for(Transaction transaction: tList) {
                tArray[idx] = transaction;
                idx++;
            }
        }

        return tArray;
    }
    /**
     * Choose the most profitable transaction in a list of conflicting transactions
     * @param transactionList
     * @return
     */

    private Double computeFees(Transaction t) {
        
        double fee = 0d;
        UTXO utxo = null;    

        for(Input input: t.getInputs()) {
            utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());

            // Getting the output associated to the current input
            Output out = this.utxoPool.getTxOutput(utxo);
            if (out == null) continue;
            fee += out.getValue();
        }

            
        for(Output output: t.getOutputs()) {
            fee -= output.getValue();
        }
        
        return fee;
    }


    /**
     * Finds a set of transactions with maximum total transaction fees -- i.e. maximize the sum over all
     * transactions in the set of (sum of input values - sum of output values)).
     * @param possibleTxs
     * @return
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        if (possibleTxs == null || possibleTxs.length == 0) return null;
        
        Set<Transaction> sortedTransactions = new TreeSet<>((t1, t2) -> {
            double t1Fees = computeFees(t1);
            double t2Fees = computeFees(t2);
            return Double.valueOf(t2Fees).compareTo(t1Fees);
        });
        
        Collections.addAll(sortedTransactions, possibleTxs);

        UTXO utxo = null;
        List<Transaction> tList = new ArrayList<>();
        for(Transaction t: sortedTransactions) {            
            if (this.isValidTx(t)) {

                for(Input input: t.getInputs()) {
                    utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
                    // Getting the output associated to the current input
                    Output out = this.utxoPool.getTxOutput(utxo);
                    if (out == null) continue;
                    
                    this.utxoPool.removeUTXO(utxo);
                }

                int outIndex = 0;
                for(Output output: t.getOutputs()) {
                    utxo = new UTXO(t.getHash(), outIndex);
                    outIndex++;
                    this.utxoPool.addUTXO(utxo, output);
                    
                }
                utxo = null;

                tList.add(t);
            }
            
        }
        Transaction[] tArray = new Transaction[tList.size()];
        return tList.toArray(tArray);        
        
    }
}
