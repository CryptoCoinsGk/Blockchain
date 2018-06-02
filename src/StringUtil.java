import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

public class StringUtil {
	
	//Applies Sha256 to a string and returns the result. 
	public static String applySha256(String input){
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	//Verifies a String signature 
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Short hand helper to turn Object into a json string
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	//Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"  
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		
		List<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}
		List<String> treeLayer = previousTreeLayer;
		
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i+=2) {
				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
	
	static float getBalance(PublicKey publicKey) {
		float total = 0;	
        for (Map.Entry<String, TransactionOutput> item: Blockchain.UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
        	//System.out.println("\n"+UTXO.reciepient);
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
            	total += UTXO.value ; 
            	//System.out.println("\nthe mine");
            }

        }  
		return total;
	}
	static PublicKey getPublicKey(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException
	{
	    KeyFactory factory = KeyFactory.getInstance("ECDSA","BC");
	    X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encodedKey);
	    return factory.generatePublic(encodedKeySpec);
	}
	static byte[] getByteFromString(String string) {
		// Base64.getDecoder().decode(string);
		byte[] dataBytes = Base64.getEncoder().encode(string.getBytes());
	    dataBytes = Base64.getDecoder().decode(dataBytes);
	    return dataBytes;
	}
	static byte[] getByteFromString2 (String string) {
		byte[] res = null;
		try {
			res =    string.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	static PublicKey getPublicKey2(String string) {
	  	byte[] keyBytes = Base64.getDecoder().decode(string);
	  	PublicKey key2 = null;
	  	try {
			  key2 = StringUtil.getPublicKey(keyBytes);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	//PublicKey key2 = (PublicKey) new SecretKeySpec(keyBytes, 0, keyBytes.length, "ECDSA");
		return key2;
	}
	static ArrayList<TransactionInput> getInputs (PublicKey publicKey){
		ArrayList<TransactionInput> arrayInputs = new ArrayList<TransactionInput>();
        for (Map.Entry<String, TransactionOutput> item: Blockchain.UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
            	TransactionInput input = new TransactionInput(UTXO.id);
            	arrayInputs.add(input);
            }
        }
        return arrayInputs;
	}

}