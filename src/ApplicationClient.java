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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;

public class ApplicationClient {
	
	
	private Socket clientSocket;
	
	private ObjectOutputStream outToServer;
	
	private Commande commande;
	
	private ObjectInputStream inFromServer;
	
	private Object retourFromServer;
	
	private BufferedReader read;
	
	private FileWriter write;
	
	private static ArrayList<Commande> listCommande;
	
	
	
	public ApplicationClient(String hostName, int port) throws Exception
	{
		listCommande=new ArrayList<Commande>();
		clientSocket = new Socket(hostName, port);
	}
	
	public void traiteCommande(Commande uneCommande) throws IOException 
	{
		outToServer = new ObjectOutputStream(clientSocket.getOutputStream()); 
		
		inFromServer = new ObjectInputStream(clientSocket.getInputStream());
		
		//commande = "serveur fonctionnel";
		
        //outToServer.writeBytes(commande + '\n'); 
		
		outToServer.writeObject(uneCommande);
        
        try {
			retourFromServer = inFromServer.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        System.out.println("FROM SERVER: " + retourFromServer.toString()); 
        
               
		
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
			System.out.println(cmd);
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
		read =new BufferedReader(ipsr);
		
		
		String ligne;
		while ((ligne=read.readLine())!=null)
		{
			commande = new Commande(ligne);
			System.out.println(commande.toString());
			listCommande.add(commande);
			
		}
		read.close();
		
	
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
          client.initialise("commandes.txt", "resultats.txt");
          
          Commande cmd = listCommande.get(1);
          
          client.traiteCommande(cmd);
          
      } 

}
