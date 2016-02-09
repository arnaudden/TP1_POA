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
	
	private ArrayList<Class> listClass;
	
	private ArrayList<Object> listCours;
	
	private ArrayList<Object> listStudent;
	
	
	public ApplicationServer(int port) throws Exception
	{
		
		welcomeSocket = new ServerSocket(port); 
	    System.out.println("SERVER Is Ready!" );
	    listClass = new ArrayList<Class>();
	    listCours = new ArrayList<Object>();
	    listStudent = new ArrayList<Object>();
	    
	}
	
	/**
	 * Traite une commande re�u par le serveur en appelant la m�thode sp�cifi�e
	 * @param uneCommande : commande re�u par le serveur
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
				else
					System.err.println("Erreur de compilation de la classe " + chemin);
				
			}
			break;
			
		case "chargement":
			String classe = uneCommande.getNomQualifieDeClasse();
			traiterChargement(classe);
			
			
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
	 * @param objet : objet correspondant � une classe
	 * @param attribut : attribut de la classe
	 */
	
	public void TraiteLecture(Object objet, String attribut)
	{
		
		
		
	}
	
	
	/**
	 * Fonction qui compile une classe java
	 * @param cheminFichierSource : chemin du fichier � compiler
	 * @return un int qui permet de savoir si la compilation s'est bien d�roul�
	 */
	
	public int TraiteCompilation(String cheminFichierSource)
	
	{
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.7.0_80");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		int result = compiler.run(null, null, null, cheminFichierSource);
		System.out.println(result);
		return result;
	}
	
	/**
	 * Charge une classe qui aura �t� pr�alablement compil�e
	 * @param nomClasse : correspond au nom de la classe � charger
	 */
	
	public void traiterChargement(String nomClasse) 
	{
		
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Class newClass;
		try {
	         newClass= classLoader.loadClass(nomClasse);
	        System.out.println("newClass.getName() = " + newClass.getName());
	        listClass.add(newClass);

	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
		
		Iterator<Class> itr = listClass.iterator();
        
        while (itr.hasNext()) 
        {
            Class element = itr.next();
            System.out.print(element + " ");
            
        }
		
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
		
		
		Commande newCommande  = new Commande("chargement#ca.uqac.registraire.Cours");
		server.TraiteCommande(newCommande);
		
		
        
    }
	
	    	 
	    	
	      
	      
	    	
        
}
