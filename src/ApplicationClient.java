import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class ApplicationClient {
	
	
	private Socket clientSocket;
	
	private DataOutputStream outToServer;
	
	private String commande;
	
	private BufferedReader inFromServer;
	
	private String retourFromServer;
	
	
	
	public ApplicationClient(String hostName, int port) throws Exception
	{
		clientSocket = new Socket(hostName, port);
		
		
	}
	
	public void traiteCommande(Commandes uneCommande) throws IOException 
	{
		outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
		
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		commande = "serveur fonctionnel";
		
        outToServer.writeBytes(commande + '\n'); 
        
        retourFromServer = inFromServer.readLine(); 
        
        System.out.println("FROM SERVER: " + retourFromServer); 
        
        clientSocket.close();           
		
	}
	
	public static void main(String argv[]) throws Exception { 
		
          ApplicationClient client = new ApplicationClient("localhost", 6789);
          client.traiteCommande(null);
   
      } 

}
