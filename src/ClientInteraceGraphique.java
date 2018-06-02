import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.swing.*;


public class ClientInteraceGraphique extends JFrame implements Runnable {
		JPanel thePanel, panelKeys, panelPublicKey,panelPrivateKey, panelSend, panelGetBalance;
		JLabel  sendAdresseLabel, sendNumberCoinsLabel;
		protected JButton getBalanceButton,sendButton;
		protected JTextField adresseClient,numberOfCoins;
		protected JLabel getBalanceLabel, sendRemarque, publicKeyLabel, privateKeyLabel;
		JFrame fenetreKeys;
		
		public ClientInteraceGraphique() {
			this.setSize(1200,400);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle("Wallet");
			
			panelPublicKey = new JPanel();
			panelPrivateKey = new JPanel();
			panelKeys = new JPanel();
			panelSend = new JPanel();
			panelGetBalance = new JPanel();
			
			publicKeyLabel = new JLabel("publikKey  is : ");
			privateKeyLabel = new JLabel("privateKey is :");
			
			getBalanceLabel = new JLabel("Balance is : ");
			getBalanceButton = new JButton("Get Balance");
			
			adresseClient = new JTextField(5);
			numberOfCoins = new JTextField(5);
			sendAdresseLabel = new JLabel("Adresse recipient : ");
			sendNumberCoinsLabel = new JLabel("Nombre de coins");
			sendRemarque = new JLabel("");
			sendButton = new JButton("Send");
			
			getBalanceLabel.setSize(800, 300);
			panelPublicKey.add(publicKeyLabel);
			panelPrivateKey.add(privateKeyLabel);
			
			panelKeys.setLayout(new GridLayout( 2,0,2, 0));
			panelKeys.add(panelPublicKey);
			panelKeys.add(panelPrivateKey);
			
			//panelGetBalance.setLayout(new GridLayout(2, 0, 2, 0));
			panelGetBalance.add(getBalanceButton, BorderLayout.CENTER);
			panelGetBalance.add(getBalanceLabel, BorderLayout.NORTH);

			panelSend.setLayout(new GridLayout(3, 2 ,2,2));
			panelSend.add(sendAdresseLabel);
			panelSend.add(adresseClient);
			panelSend.add(sendNumberCoinsLabel);
			panelSend.add(numberOfCoins);
			panelSend.add(sendButton);
			panelSend.add(sendRemarque);
			
			
			this.add(panelKeys,BorderLayout.NORTH);
			this.add(panelGetBalance,BorderLayout.CENTER);
			this.add(panelSend, BorderLayout.SOUTH);
			System.out.println(" hellloooooosdflsdjf");



			this.setVisible(true);
		}
		
		public void run() {
			publicKeyLabel.setText("public Key" +StringUtil.getStringFromKey(Blockchain.walletA.publicKey));
			privateKeyLabel.setText("privateKey " +StringUtil.getStringFromKey(Blockchain.walletA.privateKey));
			getBalanceButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							getBalanceCommunication();
						}
					}
					);
			sendButton.addActionListener(
					new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendButtonCommuniction();
					}
				}
			);
		
		}
		public void getBalanceCommunication(){
			getBalanceLabel.setText(Float.toString(StringUtil.getBalance(Blockchain.walletA.publicKey)));
		}
		public void sendButtonCommuniction() {
			PublicKey recipient = StringUtil.getPublicKey2(adresseClient.getText());
			String coins = numberOfCoins.getText();
			
			  ArrayList<TransactionInput> inputs = new  ArrayList<TransactionInput>();
			  inputs = StringUtil.getInputs(Blockchain.walletA.publicKey);
			  Transaction transaction = new Transaction(Blockchain.walletA.publicKey,  recipient, Float.parseFloat(coins), inputs);
			  transaction.generateSignature( Blockchain.walletA.privateKey);

				  Block block = new Block(Blockchain.blockchain.get(Blockchain.blockchain.size()-1).hash);
				  block.addTransaction(transaction);
				  block.mineBlock(Blockchain.difficulty);
			  
			
		}
	
}

