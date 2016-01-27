import java.io.Serializable;
import java.util.List;


/**
 * Cette classe est utilisée pour emmagasiner la description d’une commande.
 * @author Anujan
 *
 */

public class Commande implements Serializable {

	/**
	 * Paramètre nécessaire pour implémenter l'interface Serializable
	 */
	private static final long serialVersionUID = 1L;
	
	// fonction possible {Compilation, Chargement, Creation, Lecture, Fonction}
	String fct;
	//Compilation
	List<String> path;
	// Chargement
	String nomQualifieDeClasse;
	// Creation 
	String nomDeClass;
	// Creation & lecture & ecriture
	String identificateur;
	//Lecture & ecriture 
	String nomAttribut;
	// ecriture
	String valeur;
	
	public Commande(String line){
		int indice = line.indexOf("#");
		String fonction = line.substring(0, indice);
		System.out.println(fonction);
		
		switch (fonction){
		
		case "compilation": break;
		
		case "chargement": break;
		
		case "creation": break;
		
		case "lecture": System.out.println("demande de lecture");
			break;
			
		case "ecriture": break;	
		
		case "fonction": break;
		default: System.out.println("Fonction Inconnue");
		}
	}

	public static void main(String argv[]) throws Exception 
    { 
		String txt = "lecture#texte";
		Commande cmd = new Commande(txt);
		
    }
	
}
