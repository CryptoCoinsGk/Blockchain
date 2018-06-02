import javax.swing.JFrame;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Base64;


public class Server extends JFrame implements Runnable{
	 /* public Fenetre(){
	    this.setTitle("Ma première fenêtre Java");
	    this.setSize(400, 500);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             
	    this.setVisible(true);
	  
	  }*/
	  
	  private JTextField userText;
	  private JTextArea chatWindow;
	  private ObjectOutputStream output;
	  private ObjectInputStream input;
	  private ServerSocket server;
	  private Socket connection;
	  protected volatile int nombreDeConnections = 0;
	  
	  public Server(Socket socket) {
		  super("Comunication Serveur ");
		  this.connection = socket;
		  userText = new JTextField();
		  userText.setEditable(false);
		  userText.addActionListener(
				  new ActionListener() {
					  public void actionPerformed(ActionEvent event) {
						  //sendMessage(event.getActionCommand());
					  }
				  }
				  
				  );
		  add(userText, BorderLayout.NORTH);
		  chatWindow = new JTextArea();
		  add(new JScrollPane(chatWindow));
		  setSize(800,250);
		  setVisible(true);
	  }
	  






	// set up and run the server
	  public void run() {
		  try {
			  
			  //server = new ServerSocket(8080,100);
			  //while(true) {
				  try {
					  waitForConnection();
					  setupStreams();
					  whileChatting();
				  }catch(EOFException eofException) {
					  showMessage("\n Server enden The Connection ! ");
				  } catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				} catch (NoSuchProviderException e) {
					e.printStackTrace();
				}finally {
					  closeCrap();
				  }
			 //}
		  }catch(IOException ioException) {
			  ioException.printStackTrace();
		  }
		  
	  }
	// wait for connection, then display connection information
	  private void waitForConnection() throws IOException{
		  showMessage("Waiting for someone to connect...");
		  //connection = server.accept();
		  showMessage("Now connected to" + connection.getInetAddress().getHostName());
	  }
	  
	  // get stream to send and receive data
	  private void setupStreams() throws IOException{
		  
		  output = new ObjectOutputStream(connection.getOutputStream());
		  output.flush();
		  input = new ObjectInputStream(connection.getInputStream());
		  showMessage("\n streams are now setup");
	  }
	  
	  // during the chat conversation
	  private void whileChatting() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		  int counterID = 0;
		  String idClient;
		  String message = " You ara now connected ! ";
		  showMessage(message);
		  ableToType(false);
		  //do {
			  //have a conversation
			  try {
				  message = (String) input.readObject();
				  showMessage ("\n"+message);
				  int k = 1;
				  if(message.startsWith("GET") ) {
					  //counterID =1;
					  idClient = message;
					  //Reception de l'adresse :
					 message = (String) input.readObject();
					 System.out.println(" adresse publique demandeur :"+ message);

					  
					   
						  //counterID = 2;
						  	byte[] keyBytes = Base64.getDecoder().decode(message);
						  	//PublicKey key2 = StringUtil.getPublicKey(keyBytes);
							  PublicKey key2 = StringUtil.getPublicKey2(message);

						  	//PublicKey key2 = (PublicKey) new SecretKeySpec(keyBytes, 0, keyBytes.length, "ECDSA");
						  	//sendMessage(StringUtil.getStringFromKey(key2));
						  	sendMessage("Your Balance is : " + Float.toString(StringUtil.getBalance(key2)));
						 /*
						  	KeyFactory factory = KeyFactory.getInstance("ECDSA","BC");
						    X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(keyBytes);
						    PublicKey key= factory.generatePublic(encodedKeySpec);
						    sendMessage(StringUtil.getStringFromKey( key));
						    */
					  sendMessage("END");
					  
					  sendMessage("END");

						
				  }
				  else if (message.startsWith("SEND")){
						showMessage("SEND");

					  //showMessage("verification message : "+message);
					  String sender = (String) input.readObject();
						showMessage("\n sender : " +sender);

					  String recipient = (String) input.readObject();
						showMessage("\n+ resip :" + recipient);

					  String number = (String) input.readObject();
						showMessage("\n number : " + number);
						  int coins = Integer.parseInt(number);

					  byte[] signature =  (byte[]) input.readObject();
					  //byte[] by = StringUtil.getByteFromString(sender);
					  PublicKey pub = StringUtil.getPublicKey2(sender);
					  System.out.println("public key  : "+ pub);
					  System.out.println("sign         : "+signature);

					  System.out.println("signature  :  " + StringUtil.verifyECDSASig(pub, sender +recipient+number, signature));
					  showMessage("\n signature : "+ signature);
					  ArrayList<TransactionInput> inputs = new  ArrayList<TransactionInput>();
					  inputs = StringUtil.getInputs(pub);
					  Transaction transaction = new Transaction(pub,  StringUtil.getPublicKey2(recipient), coins, inputs);
					  transaction.signature = signature;
					  Block block = new Block(Blockchain.blockchain.get(Blockchain.blockchain.size()-1).hash);
					  block.addTransaction(transaction);
					  block.mineBlock(Blockchain.difficulty);
					  //transaction.generateSignature(Blockchain.walletA.privateKey);
					  //System.out.println(" resultat de transaction :    " + transaction.processTransaction());
				  
				  
				  }
			  }catch(ClassNotFoundException e) {
				  showMessage("\n idk wtf that user send!");
			  }
		 //}while(!message.equals("END"));
		
	  }
	  
	  //close streams and sockets after you are done chatting
	  private void closeCrap() {
		  showMessage("\n Closing Connections...\n");
		  ableToType(false);
		  try {
			  output.close();
			  input.close();
			  connection.close();
			  this.setVisible(false);
		  }catch(IOException e) {
			  e.printStackTrace();
		  }
	  }
	  
	  private void sendMessage(String message) {
		  try {
			  output.writeObject("SERVER -" + message);
			  output.flush();
			  showMessage("\n SERVER - "+message);
		  }catch(IOException e) {
			  chatWindow.append("\nErro : Message not send");
		  }
	  }
	  
	  // updates chatWindow
	  private void showMessage(final String text) {
		  
		  SwingUtilities.invokeLater(
				  new Runnable() {
					  public void run() {
						  chatWindow.append(text);
					  }
				  }
				  );
	  }
	  //let the user type stuff into their box
	  private void ableToType(final boolean tof) {
		  SwingUtilities.invokeLater(
				  new Runnable() {
					  public void run() {
						  userText.setEditable(tof);
						  
					  }
				  }
				  );
	  }
}

