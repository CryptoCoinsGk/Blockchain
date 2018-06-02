import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

import com.google.gson.GsonBuilder;

public class Blockchain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	public static ArrayList<Transaction> poolTransactions = new ArrayList<Transaction>();
	
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA = new Wallet();
	public static Wallet walletB = new Wallet();
	public static Wallet walletC = new Wallet();

	public static Transaction genesisTransaction;

	public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {	
		//add our blocks to the blockchain ArrayList:
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
		
		//Create wallets:
		//walletA = new Wallet();
		//Wallet walletB = new Wallet();		
		Wallet coinbase = new Wallet();
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
	
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
		System.out.println("Creating and Mining Genesis block... ");
		System.out.println("public Key genesis : "+StringUtil.getStringFromKey(walletA.publicKey));
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		Block block1 = new Block(genesis.hash);

		//testing
		/*
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		System.out.println(block1.transactions.get(0).toString());
		*/

		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		
		String UTXOsJson = new GsonBuilder().setPrettyPrinting().create().toJson(UTXOs);
		System.out.println("\nThe block chain: ");
		System.out.println(UTXOsJson);
		
		
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
		}
		System.out.println(total);
		Serveur serveur1 = new Serveur();
		System.out.println("hello");
		//serveur1.start();
		//serveur1.join();
		
		//
		try{
			 ServerSocket serverSocket = new ServerSocket(15123);
			 Socket socket = serverSocket.accept(); 
			 System.out.println("heeeeeeeeeeeeeeeeelooooooooooooooooooo");
			 System.out.println("Accepted connection : " + socket); 
			 File transferFile = new File ("Document.doc"); 
			 byte [] bytearray = new byte [(int)transferFile.length()]; 
			 FileInputStream fin = new FileInputStream(transferFile); 
			 BufferedInputStream bin = new BufferedInputStream(fin); 
			 bin.read(bytearray,0,bytearray.length); 
			 OutputStream os = socket.getOutputStream(); 
			 System.out.println("Sending Files..."); 
			 os.write(bytearray,0,bytearray.length); 
			 os.flush();
			 socket.close();
			 serverSocket.close();
			 System.out.println("File transfer complete");
		 }
		 	catch (IOException e) {
		 		System.out.println("Division par zéro !");
		 	}
			//Server sally = new Server();
			//sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//sally.startRunning();
			//new Thread(sally).start();
		//

		//Thread serveur = new Thread(serverThread);
		//serveur.start();
		ServerThread serverThread = new ServerThread();
		new Thread(serverThread).start();
		isChainValid();
		
		ClientInteraceGraphique interfaceG = new ClientInteraceGraphique();
		new Thread(interfaceG).start();
		
		
	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("#Current Hashes not equal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifiySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				
				boolean val = (float)currentTransaction.getInputsValue() !=(float) currentTransaction.getOutputsValue();
				if(val) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	public static String TransactionServer(int coins) {
	int longueur = Blockchain.blockchain.size();
	Block block = new Block(Blockchain.blockchain.get(longueur -1).hash);
	//("\nWalletB is Attempting to send funds (20) to WalletA...");
	Transaction tr = walletA.sendFunds( Blockchain.walletB.publicKey, (float)coins);
	if(tr == null) {
		return "#Not Enough funds to send transaction. Transaction Discarded."+
				"\nWalletA's balance is: " + walletA.getBalance()+"\nWalletB's balance is: " + walletB.getBalance();
	}
	block.addTransaction(tr);
	addBlock(block);
	return "\nWalletA's balance is: " + walletA.getBalance()+"\nWalletB's balance is: " + walletB.getBalance();
	}
}