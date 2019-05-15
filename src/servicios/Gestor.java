package servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class Gestor {

	static String[] IP = {"localhost", "localhost", "localhost"};
	
	public static void main(String[] args) throws IOException {
		
		String AUX = crearDespliegue();
		System.out.println(AUX);
		

		
		
		
	}

	
	
	private static String crearDespliegue() throws IOException {
		
		int j=1;
		
		//for(int i=0; i<3; i++) {
			
			
			//http://localhost:8080/Servicio/rest/servicios/crear?p=1&ip1=localhost&ip2=localhost&ip3=localhost
			
			//URL url = new URL("http://"+ IP[i] +":8080/Servicio/rest/servicios/crear?p=" + j +"&ip1="+ IP[0] +"&ip2=" + IP[1]+"&ip3=" + IP[2]);
			URL url = new URL("http://localhost:8080/Servicio/rest/servicios/crear?p=" + j +"&ip1="+ IP[0] +"&ip2=" + IP[1]+"&ip3=" + IP[2]);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			try {
				conn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(conn.getResponseCode() != 200) 
			{	
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			BufferedReader br = new	BufferedReader(	new InputStreamReader((conn.getInputStream())));
			String output;
			
			while((output = br.readLine()) != null) 
			{
				System.out.println(output);
			}
			j=j+2;			
		//}
		return "OK";		
	}

}
