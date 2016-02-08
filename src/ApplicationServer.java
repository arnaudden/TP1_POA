import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class ApplicationServer {

	private ServerSocket welcomeSocket;
	
	private BufferedReader bufferFromClient;
	
	private DataOutputStream outToClient;
	
	private Socket connectionSocket;
	
	private Commande commandeFromClient;
	
	private String clientSentence;
	
	private String serverSentence;
	
	
	public ApplicationServer(int port) throws Exception
	{
		
		welcomeSocket = new ServerSocket(port); 
	    System.out.println("SERVER Is Ready!" );
	    
	    
	}
	
	/**
	 * Traite une commande reçu par le serveur en appelant la méthode spécifiée
	 * @param uneCommande : commande reçu par le serveur
	 */
	
	public void TraiteCommande(Commande uneCommande)
	{
		switch(uneCommande.getFonction())
		
		{
		
		case "compilation": 
			
			ArrayList<String> path = uneCommande.getPath();
			
			for (Iterator<String> i =path.iterator(); i.hasNext();)
			{
				
				String chemin = i.next();
				int compil = TraiteCompilation(chemin);
				if(compil ==0)
				{
					System.out.println(chemin + " a ete compile");
				}
				
			}
			break;
			
		case "chargement":
			
			
			break;
		case "creation":
			
			break;
			
		case "lecture" :
			
			break;
			
		case "ecriture":
			
			break;
			
		default: System.out.println("Fonction Inconnue");
			
		
		}
		
	}
	
	/**
	 * Lecture d'un attribut d'une classe
	 * @param objet : objet correspondant à une classe
	 * @param attribut : attribut de la classe
	 */
	
	public void TraiteLecture(Object objet, String attribut)
	{
		
		
		
	}
	
	
	
	public int TraiteCompilation(String cheminFichierSource)
	
	{
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.7.0_80");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		int result = compiler.run(null, null, null, cheminFichierSource);
		System.out.println(result);
		return result;
	}
	
	
	
	
	public void aVosOrdres() throws IOException 
	{
		while(true) { 
			
			connectionSocket = welcomeSocket.accept(); 
		    
		    bufferFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			clientSentence = bufferFromClient.readLine(); 
			
			commandeFromClient = new Commande(clientSentence);
			
			System.out.println("Mesasge from Client : " + clientSentence);
			
			TraiteCommande(commandeFromClient);
	    	
	    	serverSentence = clientSentence.toUpperCase() + '\n'; 
	    	
	    	outToClient.writeBytes(serverSentence); 
	    	
		}
		
	}
	
	
	
	
	public static void main(String argv[]) throws Exception 
    { 
		ApplicationServer server = new ApplicationServer(6789);
		//server.aVosOrdres();
		
		
		Commande newCommande  = new Commande("compilation#./src/ca/uqac/registraire/Cours.java,./src/ca/uqac/registraire/Etudiant.java#./classes");
		server.TraiteCommande(newCommande);
    }
	
	    	 
	    	
	      
	      
	    	
        
}
