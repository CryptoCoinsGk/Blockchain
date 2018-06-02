import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WalletServer implements Runnable {

	protected ClientInteraceGraphique interfaceG;

	
	public WalletServer(){
		interfaceG = new ClientInteraceGraphique();

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		interfaceG.getBalanceButton.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent event){
					getBalanceCommuniction();
				}
			}
		);
		interfaceG.sendButton.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendButtonCommuniction();
				}
			}
		);
	}
	
	void getBalanceCommuniction() {


	}
	void sendButtonCommuniction() {

	
	}
}