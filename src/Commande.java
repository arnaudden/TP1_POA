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
	String nomDeClasse;
	// Creation & lecture & ecriture
	String identificateur;
	//Lecture & ecriture 
	String nomAttribut;
	// ecriture
	String valeur;
	
	public Commande(String line){
		int indice = line.indexOf("#");
		String fonction = line.substring(0, indice);
		//System.out.println(fonction);
		fct = fonction;
		String tmp = new String();
		switch (fonction){
		
		case "compilation": break;
		
		case "chargement": nomQualifieDeClasse = line.substring(indice + 1); 
			break;
		
		case "creation": tmp = line.substring(indice + 1);
		indice = tmp.indexOf("#");
		nomDeClasse = tmp.substring(0, indice);
		identificateur = tmp.substring(indice +1);
			break;
		
		case "lecture": tmp = new String(line.substring(indice + 1));
		indice = tmp.indexOf("#");
		identificateur = tmp.substring(0, indice);
		nomAttribut = tmp.substring(indice +1);
			break;
			
		case "ecriture": break;	
		
		case "fonction": break;
		default: System.out.println("Fonction Inconnue");
		}
	}
	
	public String toString(){
		
		String result = new String();
		result = "Fonctison: "+fct;
		switch (fct){
				
				case "compilation": break;
				
				case "chargement":  result += ", nom qualifie de classe: "+ nomQualifieDeClasse;
					break;
				
				case "creation": result += ", nom de classe: "+ nomDeClasse+", identificateur: "+ identificateur;
					break;
				
				case "lecture": result += ", identificateur: "+ identificateur+", nom d'attribut: "+nomAttribut;
					break;
					
				case "ecriture": break;	
				
				case "fonction": break;
				default: System.out.println("Fonction Inconnue");
				}
		return result;
	}

	public static void main(String argv[]) throws Exception 
    { 
		String txt = "ecriture#8inf853#titre#Architecture des applications";
		Commande cmd = new Commande(txt);
		System.out.println(cmd.toString());	
    }
	
}
