import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;


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
	private String fonction;
	

	//Fonction
	private String cheminRelatifDesFichiers;
	
	//Compilation
	private ArrayList<String> path;
	
	// Chargement
	private String nomQualifieDeClasse;
	
	// Creation 
	private String nomDeClasse;
	
	// Creation & lecture & ecriture
	private String identificateur;
	
	//Lecture & ecriture 
	private String nomAttribut;
	
	// ecriture
	private String valeur;
	
	public Commande(String line){
		
		int indice = line.indexOf("#");
		
		fonction = line.substring(0, indice);
		
		//System.out.println(fonction);
		
		String tmp = new String();
		String tmp1 = new String();
		switch (fonction){
		
		case "compilation": tmp = line.substring(indice +1);
		indice = tmp.indexOf("#");
		
		cheminRelatifDesFichiers = tmp.substring(indice+1);
		tmp = tmp.substring(0,indice);
		
		path = new ArrayList<String>();
		
		while(true){
			indice = tmp.indexOf(",");
			if(indice <0){
				tmp1 = tmp.substring(0);
				path.add(tmp1);
				break;
			}
			tmp1 = tmp.substring(0, indice);
			path.add(tmp1);
			tmp = tmp.substring(indice+1);
		}
		
			break;
		
		case "chargement": nomQualifieDeClasse = line.substring(indice + 1); 
			break;
		
		case "creation": tmp = line.substring(indice + 1);
		indice = tmp.indexOf("#");
		nomDeClasse = tmp.substring(0, indice);
		identificateur = tmp.substring(indice +1);
			break;
		
		case "lecture": tmp = line.substring(indice + 1);
		indice = tmp.indexOf("#");
		identificateur = tmp.substring(0, indice);
		nomAttribut = tmp.substring(indice +1);
		
			break;
			
		case "ecriture": tmp = line.substring(indice + 1);
		
		indice = tmp.indexOf("#");
		
		identificateur = tmp.substring(0, indice);
		
		tmp = tmp.substring(indice +1);
		
		indice = tmp.indexOf("#");
		
		nomAttribut = tmp.substring(0, indice);
		valeur = tmp.substring(indice+1);
		break;	
		
		case "fonction": break;
		default: System.out.println("Fonction Inconnue");
		}
	}
	
	public String toString(){
		
		String result = new String();
		result = "Fonction: "+fonction;
		switch (fonction){
				
				case "compilation":result += ", chemin relatif :";
				
				for (Iterator<String> i =path.iterator(); i.hasNext();)
				{
					String chemin = i.next();
					result += chemin+", ";
				}
				result += " Chemin relatif des fichiers : " + cheminRelatifDesFichiers;
						break;
				
				case "chargement":  result += ", nom qualifie de classe: "+ nomQualifieDeClasse;
					break;
				
				case "creation": result += ", nom de classe: "+ nomDeClasse+", identificateur: "+ identificateur;
					break;
				
				case "lecture": result += ", identificateur: "+ identificateur+", nom d'attribut: "+nomAttribut;
					break;
					
				case "ecriture": result += ", identificateur : "+ identificateur +", nom d'attribut: "+nomAttribut+", valeur: "+valeur;
					break;	
				
				case "fonction": break;
				default: System.out.println("Fonction Inconnue");
				}
		return result;
	}

	public static void main(String argv[]) throws Exception 
    { 
		String txt = "compilation#./src/ca/uqac/registraire/Cours.java,./src/ca/uqac/registraire/Etudiant.java#./classes";
		Commande cmd = new Commande(txt);
		System.out.println(cmd.toString());	
		
		/*
		Files.walk(Paths.get("./classes")).forEach(filePath -> {
		    if (Files.isRegularFile(filePath)) 
		    {
		        System.out.println(filePath);
		    }
		    
		});
		*/
    }
	
	
	public String getFonction() {
		return fonction;
	}

	public void setFonction(String fct) {
		this.fonction = fct;
	}

	public ArrayList<String> getPath() {
		return path;
	}
	
	

}
