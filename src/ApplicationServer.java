import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.sun.org.apache.bcel.internal.util.ClassSet;


public class ApplicationServer {

	private ServerSocket welcomeSocket;
	
	private BufferedReader bufferFromClient;
	
	private DataOutputStream outToClient;
	
	private Socket connectionSocket;
	
	private Commande commandeFromClient;
	
	private String clientSentence;
	
	private String serverSentence;
	
	private HashMap<String, Class> tabClass;
	
	private HashMap<String, Object> tabObject;
	
	
	public ApplicationServer(int port) throws Exception
	{
		
		welcomeSocket = new ServerSocket(port); 
	    System.out.println("SERVER Is Ready!" );
	    tabClass = new HashMap<String, Class>();
	    tabObject = new HashMap<String, Object>();
	    
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
				else
					System.err.println("Erreur de compilation de la classe " + chemin);
				
			}
			break;
			
		case "chargement":
			String classe = uneCommande.getNomQualifieDeClasse();
			traiterChargement(classe);
			
			
			break;
		case "creation":
			
			for(HashMap.Entry<String,Class> entry: tabClass.entrySet())
			{
				if(entry.getKey().equals(uneCommande.getNomDeClasse()))
				{
					traiterCreation(entry.getValue(), uneCommande.getIdentificateur());
				}
				
			}
			
			break;
			
		case "lecture" :
			
			break;
			
		case "ecriture":
			
			for(HashMap.Entry<String,Object> entry: tabObject.entrySet())
			{
				if(entry.getKey().equals(uneCommande.getIdentificateur()))
				{
					traiterEcriture(entry, uneCommande.getNomAttribut(), uneCommande.getValeur());
				}
			}
			
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
	
	
	/**
	 * Fonction qui compile une classe java
	 * @param cheminFichierSource : chemin du fichier à compiler
	 * @return un int qui permet de savoir si la compilation s'est bien déroulé
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
	 * Charge une classe qui aura été préalablement compilée
	 * @param nomClasse : correspond au nom de la classe à charger
	 */
	
	public void traiterChargement(String nomClasse) 
	{
		
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Class newClass;
		try {
	         newClass= classLoader.loadClass(nomClasse);
	        System.out.println("newClass.getName() = " + newClass.getName());
	        tabClass.put(nomClasse, newClass);

	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
		
		for(HashMap.Entry<String,Class> entry: tabClass.entrySet())
		{
			System.out.println("nom de la clé : " + entry.getKey() + " nom de la classe à partir de la valeur : " + entry.getValue().getName());
		}
		
	}
	
	/**
	 * Classe qui gère la création d'un objet
	 * @param classeDeLobjet
	 * @param identificateur
	 */
	
	public void traiterCreation(Class classeDeLobjet, String identificateur) 
	{
		switch(classeDeLobjet.getName())
		
		{
		case "ca.uqac.registraire.Cours": 
			try 
			{
				Object cours = classeDeLobjet.newInstance();
				tabObject.put(identificateur, cours);
				
			} 
			catch (InstantiationException e) 
			{
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} 
			catch (IllegalAccessException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case "ca.uqac.registraire.Etudiant" : 
			
			try 
			{
				Object student = classeDeLobjet.newInstance();
				tabObject.put(identificateur, student);
			} 
			
			catch (InstantiationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			catch (IllegalAccessException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
			
		default: System.out.println("Classe non chargée");
		}
	}
	
	
	/**
	 * Permet d'écrire une valeur dans un champ donnée dans une classe donnée
	 * @param pointeurObjet : objet que nous devons écrire
	 * @param attribut : attribut à modifier de l'objet
	 * @param valeur : valeur à écrire dans l'attribut
	 */
	public void traiterEcriture(Object pointeurObjet, String attribut, Object valeur) 
	{
		
		
		
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
		Commande newCommande2  = new Commande("chargement#ca.uqac.registraire.Etudiant");
		Commande newCommande3  = new Commande("creation#ca.uqac.registraire.Cours#8inf853");
		Commande newCommande4  = new Commande("creation#ca.uqac.registraire.Cours#8inf843");
		Commande newCommande5  = new Commande("creation#ca.uqac.registraire.Etudiant#mathilde");
		server.TraiteCommande(newCommande);
		server.TraiteCommande(newCommande2);
		server.TraiteCommande(newCommande3);
		server.TraiteCommande(newCommande4);
		server.TraiteCommande(newCommande5);
    }
	
	    	 
	    	
	      
	      
	    	
        
}
