import java.io.Serializable;

public class Couple implements Serializable  {

	private String type;
	private String valeur;
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getValeur() {
		return valeur;
	}


	public void setValeur(String valeur) {
		this.valeur = valeur;
	}


	public Couple()
	{
		
	}
}
