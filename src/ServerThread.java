import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

public class ServerThread  implements  Runnable{
	private ServerSocket socketServeur;
	public void run() {
		try {
			socketServeur = new ServerSocket(8080,100);
			System.out.println("\n\n\nDemarrage de serveur");

		while (true) {
			System.out.println("this is the thread server");
			Socket socketClient = socketServeur.accept();
			//TestServeurThreadTCP t = new TestServeurThreadTCP(socketClient);
			//t.start();
			Server sally = new Server(socketClient);
			sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//sally.startRunning();
			new Thread(sally).start();
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

}
