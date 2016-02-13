import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class ApplicationServer {

	private ServerSocket welcomeSocket;
	
	private ObjectInputStream objectFromClient;
	
	private ObjectOutputStream objectToClient;
	
	private Socket connectionSocket;
	
	private Commande commandeFromClient;
	
	private String serverToClientSentence;
	
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
	 * @throws IOException 
	 */
	
	public void TraiteCommande(Commande uneCommande) throws IOException
	{
		switch(uneCommande.getFonction())
		
		{
		
		case "compilation": 
			
			ArrayList<String> path = uneCommande.getPath();
			TraiterCompilation(path);
			
			
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
			
			traiterFonction(tabObject.get(uneCommande.getIdentificateur()), uneCommande.getNom_fonction(), uneCommande.getListe_parametres(), uneCommande.getType(), uneCommande.getId_identificateur());
			
			
			break;
			
			
		default: System.out.println("Fonction Inconnue");
			
		
		}
		
	}
	
	/**
	 * Lecture d'un attribut d'une classe
	 * @param objet : objet correspondant à une classe
	 * @param attribut : attribut de la classe
	 * @throws IOException 
	 */
	
	public void traiterLecture(Object objet, String attribut) throws IOException
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
		serverToClientSentence = "Le champ " + attribut + " vaut " + result;
		System.out.println(serverToClientSentence);
		//sendMessageToClient();
	}
	
	
	/**
	 * Fonction qui compile une classe java
	 * @param cheminFichierSource : chemin du fichier à compiler
	 * @return un int qui permet de savoir si la compilation s'est bien déroulé
	 * @throws IOException 
	 */
	
	public void TraiterCompilation(ArrayList<String> path)
	
	{
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.7.0_80");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		
		for (Iterator<String> i =path.iterator(); i.hasNext();)
		{
			
			String cheminFichierSource = i.next();
			compiler.run(null, null, null, cheminFichierSource);
		}
		
		serverToClientSentence =  "Les classes ont été compilé";
		System.out.println(serverToClientSentence);
		try {
			sendMessageToClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		
	
	/**
	 * Charge une classe qui aura été préalablement compilée
	 * @param nomClasse : correspond au nom de la classe à charger
	 * @throws IOException 
	 */
	
	public void traiterChargement(String nomClasse) throws IOException 
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
		
		serverToClientSentence = nomClasse + "a été chargée";
		System.out.println(serverToClientSentence);
		//sendMessageToClient();
		
	}
	
	/**
	 * Classe qui gère la création d'un objet
	 * @param classeDeLobjet
	 * @param identificateur
	 * @throws IOException 
	 */
	
	public void traiterCreation(Class classeDeLobjet, String identificateur) throws IOException 
	{
		System.out.println("Lancement de la création de l'objet " + identificateur + " qui est une classe " + classeDeLobjet.getName());
			try 
			{
				Object objet = classeDeLobjet.newInstance();
				tabObject.put(identificateur, objet);
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
			
			serverToClientSentence = "L'objet " + tabObject.get(identificateur).getClass() + " a bien été créé avec l'identifiant " + identificateur;
			System.out.println(serverToClientSentence);
			//sendMessageToClient();
	}
	
	
	/**
	 * Permet d'écrire une valeur dans un champ donnée dans une classe donnée
	 * @param pointeurObjet : objet que nous devons écrire
	 * @param attribut : attribut à modifier de l'objet
	 * @param valeur : valeur à écrire dans l'attribut
	 * @throws IOException 
	 */
	public void traiterEcriture(Object pointeurObjet, String attribut, Object valeur) throws IOException 
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
		
		serverToClientSentence ="L'attribut " + attribut + " a bien été écrit avec la valeur " + valeur;
		System.out.println(serverToClientSentence);
		//sendMessageToClient();
	}
	
	
	
	public void traiterFonction(Object pointeurObjet, String nomFonction, ArrayList<Couple> listeParametre, String typeObj, String valObj) 
	{

		System.out.println("Lancement de la fonction " + nomFonction + " sur l'objet " + pointeurObjet);
		
		// Récupération des méthodes de la classe
		Method[] methods = pointeurObjet.getClass().getMethods();
		Method m;
		
		Object objInFunction = new Object();
		Object resultFunction = new Object();
		
		for(int l =0; l<methods.length;l++)
		{
			m = methods[l];
			if(m.getName().equals(nomFonction))
			{
				if(typeObj == null)
				{
					try {
						resultFunction = m.invoke(pointeurObjet);
						serverToClientSentence = "La fonction " + m.getName() + " renvoie " + resultFunction;
						System.out.println(serverToClientSentence);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (listeParametre.isEmpty())
				{
					objInFunction = tabObject.get(valObj);
					try {
						resultFunction = m.invoke(pointeurObjet, objInFunction);
						serverToClientSentence = "La fonction " + m.getName() + " renvoie " + resultFunction;
						System.out.println(serverToClientSentence);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					objInFunction = tabObject.get(valObj);
					Couple params = listeParametre.get(0);

					try {
						if(params.getType().equals("float"))
						{
							float param = Float.parseFloat(params.getValeur());
							resultFunction = m.invoke(pointeurObjet, objInFunction, param);
							serverToClientSentence = "La fonction " + m.getName() + " renvoie " + resultFunction;
							System.out.println(serverToClientSentence);
						}
						} catch (IllegalAccessException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
				}
			}
		}
	}
	
	
	
	public void aVosOrdres() throws IOException, ClassNotFoundException 
	{
		
		while(true) { 
			System.out.println("server : test0");
			connectionSocket = welcomeSocket.accept();
			System.out.println("server : test1");
		    objectFromClient =  new ObjectInputStream(connectionSocket.getInputStream());
		
		    objectToClient = new ObjectOutputStream(connectionSocket.getOutputStream());

		    System.out.println("server : test2");
			Object obj = objectFromClient.readObject();
			commandeFromClient = (Commande) obj;
			System.out.println("Mesasge from Client : " + commandeFromClient);
			
			
			TraiteCommande(commandeFromClient);
	    	
	    	
		}
		
	}
	
	
	

	
	
	public void sendMessageToClient() throws IOException
	
	{
		try 
		{
			objectToClient.writeObject(serverToClientSentence);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String argv[]) throws Exception 
    { 
		ApplicationServer server = new ApplicationServer(6789);
		//server.aVosOrdres();
		
		
		Commande newCommande  = new Commande("chargement#ca.uqac.registraire.Cours");
		Commande newCommande2  = new Commande("chargement#ca.uqac.registraire.Etudiant");
		Commande newCommande3  = new Commande("creation#ca.uqac.registraire.Cours#8inf853");
		Commande newCommande10  = new Commande("creation#ca.uqac.registraire.Cours#8inf843");
		Commande newCommande4  = new Commande("creation#ca.uqac.registraire.Etudiant#raymond");
		Commande newCommande5  = new Commande("ecriture#raymond#nom#Raymond Sauve");
		Commande newCommande6  = new Commande("lecture#raymond#nom");
		Commande newCommande7  = new Commande("fonction#raymond#inscrisDansCours#ca.uqac.registraire.Cours:ID(8inf853))");
		Commande newCommande8  = new Commande("fonction#raymond#getMoyenne#");
		Commande newCommande9  = new Commande("fonction#raymond#inscrisDansCours#ca.uqac.registraire.Cours:ID(8inf843)");
		Commande newCommande11  = new Commande("fonction#8inf853#attributeNote#ca.uqac.registraire.Etudiant:ID(raymond),float:3.0");
		Commande newCommande12  = new Commande("fonction#8inf843#attributeNote#ca.uqac.registraire.Etudiant:ID(raymond),float:2.7");
		Commande newCommande13 = new Commande("fonction#raymond#getMoyenne#");
		Commande newCommande14 = new Commande("fonction#8inf853#toString#");
		server.TraiteCommande(newCommande);
		server.TraiteCommande(newCommande2);
		server.TraiteCommande(newCommande3);
		server.TraiteCommande(newCommande4);
		server.TraiteCommande(newCommande10);
		server.TraiteCommande(newCommande5);
		server.TraiteCommande(newCommande6);
		server.TraiteCommande(newCommande7);
		server.TraiteCommande(newCommande8);
		server.TraiteCommande(newCommande9);
		server.TraiteCommande(newCommande11);
		server.TraiteCommande(newCommande12);
		server.TraiteCommande(newCommande13);
		server.TraiteCommande(newCommande14);
		
    }
	
	    	 
	    	
	      
	      
	    	
        
}
