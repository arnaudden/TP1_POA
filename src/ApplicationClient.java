import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ApplicationClient {
	
	
	public static void main(String argv[]) throws Exception { 
        String sentence; 
        String modifiedSentence; 
        
        
        
        //System.out.println("Enter message to send it to SERVER: " );
        
        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        
        Socket clientSocket = new Socket("localhost", 6789); 
        
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        sentence = "Serveur fonctionnel";
          
        outToServer.writeBytes(sentence + '\n'); 
          
        modifiedSentence = inFromServer.readLine(); 
          
        System.out.println("FROM SERVER: " + modifiedSentence); 
          
        clientSocket.close();              
      } 

}
