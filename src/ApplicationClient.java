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

import com.sun.corba.se.spi.ior.Writeable;

public class ApplicationClient {
	
	private String hostName;
	
	private int port;
	
	private Socket clientSocket;
	
	private Commande commande;
	
	private Object retourFromServer;
	
	private PrintWriter write;
	
	private static ArrayList<Commande> listCommande;
	
	
	/*
	* Constructeur 
	*/
	public ApplicationClient(String hostn, int pt) throws Exception
	{
		listCommande=new ArrayList<Commande>();
		hostName = hostn;
		port = pt;
	}
	
	/**
	* prend une Commande d�ment format�e, et la fait ex�cuter par le serveur. Le r�sultat de
	* l�ex�cution est retourn�e. Si la commande ne retourne pas de r�sultat, on retourne null.
	* Chaque appel doit ouvrir une connexion, ex�cuter, et fermer la connexion. Si vous le
	* souhaitez, vous pourriez �crire six fonctions sp�cialis�es, une par type de commande
	* d�crit plus haut, qui seront appel�es par traiteCommande(Commande uneCommande)
	* @return
	*/
	public Object traiteCommande(Commande uneCommande) throws IOException 
	{
		
		clientSocket = new Socket(hostName, port);
		System.out.println(clientSocket.isConnected());
		ObjectOutputStream outToServer2 = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream inFromServer2 = new ObjectInputStream(clientSocket.getInputStream());

		
		outToServer2.writeObject(uneCommande);
		
		System.out.println(uneCommande + " a �t� envoy�");
		write.println(uneCommande + " a �t� envoy�");
		
        try {
			retourFromServer = inFromServer2.readObject();
			System.out.println("test 3");
			write.println("FROM SERVER: " + retourFromServer.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        clientSocket.close();
        return retourFromServer;
               
		
	}
	
	
	/**
	* initialise : ouvre les diff�rents fichiers de lecture et �criture
	* @throws IOException 
	*/
	

	public void initialise(String fichCommandes, String fichSortie) throws IOException{		
		InputStream ips=new FileInputStream(fichCommandes); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader read =new BufferedReader(ipsr);
		
		
		String ligne;
		while ((ligne=read.readLine())!=null)
		{
			commande = new Commande(ligne);
			listCommande.add(commande);
			
		}
		read.close();
		
	
		FileWriter fw = new FileWriter (fichSortie);		
		BufferedWriter buffWriter = new BufferedWriter (fw);
		write = new PrintWriter(buffWriter);
	}
	
	/**
	* Permet de fermer le fichier d'�criture
	*/
	public void closeFile()
	{
		write.close();
	}
	
	
	
	public static void main(String argv[]) throws Exception { 
		
          ApplicationClient client = new ApplicationClient("localhost", 6789);
          //client.traiteCommande(null);
          client.initialise("commandes.txt", "resultats.txt");
          
          
          for(int i = 0; i<listCommande.size() ; i++)
          {
        	  Commande cmd = listCommande.get(i);
              System.out.println("traitement de la commande "+ cmd); 
              Object obj = client.traiteCommande(cmd);
              System.out.println("FROM SERVER: " + obj.toString()); 
          }
          
          
          client.closeFile();
          
      } 

}
