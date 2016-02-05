import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	private BufferedReader read;
	
	private FileWriter write;
	
	
	
	public ApplicationClient(String hostName, int port) throws Exception
	{
		clientSocket = new Socket(hostName, port);
	}
	
	public void traiteCommande(Commande uneCommande) throws IOException 
	{
		outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
		
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		commande = "serveur fonctionnel";
		
        outToServer.writeBytes(commande + '\n'); 
        
        retourFromServer = inFromServer.readLine(); 
        
        System.out.println("FROM SERVER: " + retourFromServer); 
        
        clientSocket.close();           
		
	}
	
	/**
	* prend le fichier contenant la liste des commandes, et le charge dans une
	* variable du type Commande qui est retournée
	 * @throws IOException 
	*/
	public Commande saisisCommande(BufferedReader fichier) throws IOException{
		String cmdLine = new String();
			cmdLine = fichier.readLine();

		Commande cmd = new Commande(cmdLine);
		return cmd;
	}
	
	/**
	* initialise : ouvre les différents fichiers de lecture et écriture
	 * @throws IOException 
	*/
	
	public void initialise(String fichCommandes, String fichSortie) throws IOException
	{
		BufferedReader read = new BufferedReader( new FileReader(fichCommandes));
		
		FileWriter write = new FileWriter (fichSortie);
	}
	
	
	public static void main(String argv[]) throws Exception { 
		
          ApplicationClient client = new ApplicationClient("localhost", 6789);
         // client.traiteCommande(null);
          client.initialise("commandes.txt", "resultats.txt");
   
      } 

}
