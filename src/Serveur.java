import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import  java.net.*;

import javax.swing.JFrame;

import java.io.OutputStream;


public class Serveur extends Thread{
	
	
	 @Override
	public void run() {

	
	try{
		 ServerSocket serverSocket = new ServerSocket(15123);
		 Socket socket = serverSocket.accept(); 
	
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
	 }

	
}
