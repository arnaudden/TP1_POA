import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
				int compil = TraiterCompilation(chemin);
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
			
			traiterLecture(tabObject.get(uneCommande.getIdentificateur()), uneCommande.getNomAttribut());
			break;
			
		case "ecriture":
			
			traiterEcriture(tabObject.get(uneCommande.getIdentificateur()), uneCommande.getNomAttribut(), uneCommande.getValeur());
			
			break;
		case "fonction":
			
			
			
			
			break;
			
			
		default: System.out.println("Fonction Inconnue");
			
		
		}
		
	}
	
	/**
	 * Lecture d'un attribut d'une classe
	 * @param objet : objet correspondant à une classe
	 * @param attribut : attribut de la classe
	 */
	
	public void traiterLecture(Object objet, String attribut)
	{
		System.out.println("Lancement de lecture de l'attribut " + attribut + " de l'objet " + objet);
		
		// Récupération des attributs de la classe
		Field[] fields = objet.getClass().getDeclaredFields();
		Field f;
		
		// Récupération des méthodes de la classe
		Method[] methods = objet.getClass().getMethods();
		Method m;
		
		Object result = new Object();
		

		for(int i=0 ; i< fields.length; i++)
		{
			f = fields[i];
			if(f.getName().equals(attribut))
			{
				if(Modifier.isPublic(f.getModifiers()))
				{
					try {
						
						result = f.get(objet);
						
					} 
					
					catch (IllegalArgumentException e) 
					{
						
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					} 
					
					catch (IllegalAccessException e) 
					
					{
						// TODO Auto-generated catch block
						// Exécuter si le champ est privé
						e.printStackTrace();
						
					}
				}
			}
			else if (Modifier.isPrivate(f.getModifiers()))
			{
				String s1 = attribut.substring(0, 1).toUpperCase() + attribut.substring(1);
				String get = "get" + s1;
				for(int l =0; l<methods.length;l++)
				{
					m = methods[l];
					if(m.getName().equals(get))
					{
						try {
							result = m.invoke(objet);
							System.out.println(result);
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalArgumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			
		}
		
		System.out.println("Le champ " + attribut + " vaut " + result);
	}
	
	
	/**
	 * Fonction qui compile une classe java
	 * @param cheminFichierSource : chemin du fichier à compiler
	 * @return un int qui permet de savoir si la compilation s'est bien déroulé
	 */
	
	public int TraiterCompilation(String cheminFichierSource)
	
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
		System.out.println("Lancement de la création de l'objet " + identificateur + " qui est une classe " + classeDeLobjet.getName());
			try 
			{
				Object objet = classeDeLobjet.newInstance();
				tabObject.put(identificateur, objet);
				System.out.println("L'objet " + tabObject.get(identificateur).getClass() + " a bien été créé avec l'identifiant " + identificateur );
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
			
	}
	
	
	/**
	 * Permet d'écrire une valeur dans un champ donnée dans une classe donnée
	 * @param pointeurObjet : objet que nous devons écrire
	 * @param attribut : attribut à modifier de l'objet
	 * @param valeur : valeur à écrire dans l'attribut
	 */
	public void traiterEcriture(Object pointeurObjet, String attribut, Object valeur) 
	{
		System.out.println("Lancement de Ecriture de l'attribut " + attribut + " avec la valeur " + valeur + " de l'objet " + pointeurObjet);
		
		// Récupération des attributs de la classe
		Field[] fields = pointeurObjet.getClass().getDeclaredFields();
		Field f;
		
		// Récupération des méthodes de la classe
		Method[] methods = pointeurObjet.getClass().getMethods();
		Method m;
		
		

		
		for(int i=0 ; i< fields.length; i++)
		{
			f = fields[i];
			if(f.getName().equals(attribut))
			{
				
				if(Modifier.isPublic(f.getModifiers()))
				{
					try {
						
						f.set(pointeurObjet, valeur);
						Object o = f.get(pointeurObjet);
						System.out.println(o.toString());
						
					} 
					
					catch (IllegalArgumentException e) 
					{
						
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					} 
					
					catch (IllegalAccessException e) 
					
					{
						// TODO Auto-generated catch block
						// Exécuter si le champ est privé
						e.printStackTrace();
						
					}
					
				}
				
				else if (Modifier.isPrivate(f.getModifiers()))
				{
					
					String s1 = attribut.substring(0, 1).toUpperCase() + attribut.substring(1);
					String set = "set" + s1;
					
					for(int j =0; j<methods.length;j++)
					{
						m = methods[j];
						if(m.getName().equals(set))
						{
							try {
								m.invoke(pointeurObjet, valeur);
							} catch (IllegalAccessException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalArgumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					
				}
					
			}
		}
		
		System.out.println("L'attribut " + attribut + " a bien été écrit avec la valeur " + valeur);
	}
	
	
	
	
	
	
	
	public void aVosOrdres() throws IOException 
	{
		while(true) { 
			
			connectionSocket = welcomeSocket.accept(); 
		    
		    bufferFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			clientSentence = bufferFromClient.readLine(); 
			
			//commandeFromClient = new Commande(clientSentence);
			
			System.out.println("Mesasge from Client : " + clientSentence);
			
			TraiteCommande(commandeFromClient);
	    	
	    	serverSentence = clientSentence.toUpperCase() + '\n'; 
	    	
	    	outToClient.writeBytes(serverSentence); 
	    	
		}
		
	}
	
	
	
	
	public static void main(String argv[]) throws Exception 
    { 
		ApplicationServer server = new ApplicationServer(6789);
		server.aVosOrdres();
		
		
		Commande newCommande  = new Commande("chargement#ca.uqac.registraire.Cours");
		Commande newCommande2  = new Commande("chargement#ca.uqac.registraire.Etudiant");
		Commande newCommande3  = new Commande("creation#ca.uqac.registraire.Cours#8inf853");
		Commande newCommande4  = new Commande("creation#ca.uqac.registraire.Etudiant#raymond");
		Commande newCommande5  = new Commande("ecriture#raymond#nom#Raymond Sauve");
		Commande newCommande6  = new Commande("lecture#raymond#nom");
		server.TraiteCommande(newCommande);
		server.TraiteCommande(newCommande2);
		server.TraiteCommande(newCommande3);
		server.TraiteCommande(newCommande4);
		server.TraiteCommande(newCommande5);
		server.TraiteCommande(newCommande6);
		
    }
	
	    	 
	    	
	      
	      
	    	
        
}
