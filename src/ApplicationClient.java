import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		cmdLine=fichier.readLine();
		if(cmdLine!=null){
			Commande cmd = new Commande(cmdLine);
			return cmd;
		}

		Commande cmd = new Commande(cmdLine);
		return cmd;
	}
	
	/**
	* initialise : ouvre les différents fichiers de lecture et écriture
	 * @throws IOException 
	*/
	

	public void initialise(String fichCommandes, String fichSortie) throws IOException{		
		InputStream ips=new FileInputStream(fichCommandes); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		//BufferedReader read =new BufferedReader(ipsr);
		
		/*
		String ligne;
		String chaine ="";
		while ((ligne=read.readLine())!=null){
			System.out.println(ligne);
			chaine+=ligne+"\n";
		}
		read.close();
		System.out.println(chaine);
		*/
		
		FileWriter fw = new FileWriter (fichSortie);		
		BufferedWriter write = new BufferedWriter (fw);
		/*
		PrintWriter fichierSortie = new PrintWriter (write); 
		fichierSortie.println ("heyy"+"\n test de lecture et écriture !!"); 
		fichierSortie.close();
		*/
	}
	
	public static void main(String argv[]) throws Exception { 
		
          ApplicationClient client = new ApplicationClient("localhost", 6789);
          //client.traiteCommande(null);
          //client.initialise("commandes.txt", "resultats.txt");
          /*
          Commande cmd = new Commande();
          cmd = client.saisisCommande(client.read);
          cmd.toString();
          */
      } 

}
