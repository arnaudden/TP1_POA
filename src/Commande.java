import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Cette classe est utilis�e pour emmagasiner la description d�une commande.
 * @author Anujan
 */

public class Commande implements Serializable {

	/**
	 * Param�tre n�cessaire pour impl�menter l'interface Serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *  fonction possible {Compilation, Chargement, Creation, Lecture, Fonction}
	 */
	private String fonction;
	

	/**
	* Champ appel� lors de la compilation d'un classe correspondant au chemln du fichier � compiler
	*/
	private String cheminRelatifDesFichiers;
	
	/**
	 * ArrayList correspondant aux chemins des fichiers � compiler lors de l'appel de la fonction : Compilation
	 */
	private ArrayList<String> path;
	
	/**
	 * String contenant le nom de la classe a charger lors de l'appel de la fonction Chargement
	 */
	private String nomQualifieDeClasse;
	
	/**
	 * String contenant le nomm de la classe � cr�er lors de l'appel de la fonction : Cr�ation
	 */
	private String nomDeClasse;
	
	/*
	* Identificateur de l'objet appel� par les fonctions, cr�ation, �criture, lecture et fonction
    */
	private String identificateur;
	
	/*
	* String correspondant � l'attribut d'un objet � lire ou �crire appel� par les fonctions �criture et lecture
	*/
	private String nomAttribut;	

	/*
	* String correspondant � la valeur de l'attribut. Appel� par la fonction �criture
	*/
	private String valeur;
	
	/*
	* String correspondant au nom de la fonction appel�e sur un objet cr�er sur le serveur
	*/
	private String nom_fonction;

	/**
	 * ArrayList correspondant aux param�tres appel�s par une fonction
	 */
	private ArrayList<Couple> liste_parametres;
	
	/*
	* String correspondant au type d'un autre objet appel� par une fonction
	*/
	private String type;

	/*
	* String correspondant � l'identificateur d'un objet appel� par une fonction
	*/
	private String id_identificateur;
	
	/*
	* Constructeur de la classe Commande
	*/
	public Commande(){
		fonction = "aucune fonction";
	}
	
	/*
	* Constructeur qui prend une ligne et qui redonne la commande associ�
	*/
	public Commande(String line){
		
		int indice = line.indexOf("#");
		fonction = line.substring(0, indice);
		
		String tmp = new String();
		String tmp1 = new String();
		switch (fonction){
		
		// cas de la compilation
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
		
		// Cas du chargement
		case "chargement": nomQualifieDeClasse = line.substring(indice+1); 
			break;
		
		// Cas de la cr�ation
		case "creation": tmp = line.substring(indice + 1);
			indice = tmp.indexOf("#");
			nomDeClasse = tmp.substring(0, indice);
			identificateur = tmp.substring(indice +1);
			break;
		

		// Cas de la lecture
		case "lecture": tmp = line.substring(indice + 1);
			indice = tmp.indexOf("#");
			identificateur = tmp.substring(0, indice);
			nomAttribut = tmp.substring(indice +1);
			break;
			
		// Cas de l'�criture	
		case "ecriture": tmp = line.substring(indice + 1);
			indice = tmp.indexOf("#");		
			identificateur = tmp.substring(0, indice);		
			tmp = tmp.substring(indice +1);		
			indice = tmp.indexOf("#");		
			nomAttribut = tmp.substring(0, indice);
			valeur = tmp.substring(indice+1);
			break;	
		

		// Cas de la fonction
		case "fonction":
			tmp = line.substring(indice + 1);
			indice = tmp.indexOf("#");
			identificateur = tmp.substring(0, indice);
			tmp = tmp.substring(indice +1);		
			indice = tmp.indexOf("#");	
			nom_fonction = tmp.substring(0, indice);
			tmp = tmp.substring(indice+1);
			// Trouver si il y a "ID"
			indice = tmp.indexOf("ID");
			int indice2;
			//Cas ou on trouve "ID" dans la fonction
			if(indice > 0){
				indice = tmp.indexOf(":");
				type = tmp.substring(0, indice);
				tmp = tmp.substring(indice + 4);
				indice2 = tmp.indexOf(")");
				id_identificateur = tmp.substring(0, indice2);
			}
			// Autre Cas
				Couple cpl = new Couple();
				liste_parametres = new ArrayList<Couple>();
				
				while(true){
					indice = tmp.indexOf(",");
					if(indice <0){
						indice2 = tmp.indexOf(":");
						if(indice2 > 0){
							cpl.setType(tmp.substring(0, indice2));
							cpl.setValeur(tmp.substring(indice2+1));
							liste_parametres.add(cpl);
						}
						break;
					}
					tmp1 = tmp.substring(0, indice);
					indice2 = tmp1.indexOf(":");
					if(indice2>0){
					cpl.setType(tmp1.substring(0, indice2));
					System.out.println(cpl.getType());
					cpl.setValeur(tmp1.substring(indice2));
					liste_parametres.add(cpl);
					}
					tmp = tmp.substring(indice+1);
			}
			break;
		default: System.out.println("Fonction Inconnue");
		}
	}
	

	/*
	* Fonction toString Utiliser pour afficher une "commande" 
	*/
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
				
				case "fonction": result += ", identificateur: "+ identificateur +", nom_fonction: "+nom_fonction;
					if(type != null){
						result += ",type: "+type+", ID: "+id_identificateur;
					}
					result += ",parametres : ";
					for (Iterator<Couple> i =liste_parametres.iterator(); i.hasNext();)
					{
						Couple cpl = i.next();
						result += "type: "+cpl.getType() +", valeur: " + cpl.getValeur();
					}
					
					break;
				default: System.out.println("Fonction Inconnue");
				}
		return result;
	}	
	
	/**
	 * Retourne la fonction de la commande � ex�cuter sur le serveur
	 * @return
	 */
	public String getFonction() {
		return fonction;
	}

	/**
	 * Retourne une ArrayList de chemin de fichier � compiler
	 * @return
	 */
	public ArrayList<String> getPath() {
		return path;
	}
	
	/**
	 * Retourne le nom de la classe � charger
	 * @return
	 */
	public String getNomQualifieDeClasse() {
		return nomQualifieDeClasse;
	}
	
	/**
	 * Retourne de le nom de la classe � cr�er
	 * @return
	 */

	public String getNomDeClasse() {
		return nomDeClasse;
	}
	
	/**
	 * Retourne l'identificateur de la classe � cr�er
	 * @return
	 */

	public String getIdentificateur() {
		return identificateur;
	}

	/**
	 * Retourne le nom de l'attribut � remplir/modifier
	 * @return
	 */
	
	public String getNomAttribut() {
		return nomAttribut;
	}

	/**
	 * Retourne la valeur de la classe � cr�er
	 * @return
	 */
	
	public String getValeur() {
		return valeur;
	}
	
	/**
	 * Retourne la liste des param�tres
	 * @return
	 */
	public ArrayList<Couple> getListe_parametres() {
		return liste_parametres;
	}
	
	/**
	 * Retourne le nom de la fonction de la classe
	 * @return
	 */
	public String getNom_fonction() {
		return nom_fonction;
	}
	
	/**
	 * Retourne le type de la classe
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Retourne l'id identificateur pour utiliser pour une autre fonction
	 * @return
	 */
	public String getId_identificateur() {
		return id_identificateur;
	}
}
