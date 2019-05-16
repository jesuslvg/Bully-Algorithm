package servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Hilo extends Thread {
	
	private int id = 0;
	private int operacion = 0;
	//operacion 1 => mandar eleccion
	//operacion 2 => avisar al resto de que mando yo
	
	
	private List<String> ips = new ArrayList<String>();
	private static final int NUM_PROCESOS = 2;

	
	
	
	public Hilo(int id, int operacion) {
		this.id = id;
		this.operacion = operacion;
	}
	
	
	public long getId() {
		return id;
	}


	public int getOperacion() {
		return operacion;
	}

	
	public void run(){
								
		//OBTENER IPS DE LOS PROCESOS
		System.out.println("HILO	RUN	OBTENER	IP	" + getId());

		for(int i=1; i <=NUM_PROCESOS; i++) { 
			
			try {				

				URL url = new URL("http://localhost:8080/Servicio/rest/servicios/ip_proceso?p=" + i);
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
					System.out.println("Guardo la IP nº" + i + output);
					ips.add(output);

				}
									
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			
		}//	FIN FOR OBTENER IPS
		
		
		
		
		
		
		

		
		
		
		
		
		if(operacion == 1) {		//MANDAR ELEECION A TODOS LOS PROCESOS CON ID MAYOR AL MIO
			System.out.println("HILO	RUN	MANDAR	ELECCION	" + getId());

			for(int i=id; i<ips.size(); i++) { //COMO IPS EMPIEZA EN 0, ELECCION DESDE ID, NO ID+1
				
				try {
					//PERO ENVIO ELECCION AL I+1...
					URL url = new URL("http://" + ips.get(i) + ":8080/Servicio/rest/servicios/eleccion?p=" + (i+1) + "&id=" + getId());
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
						System.out.println("ELECCION ME DEVUELVE " + output);
					}
					
					
						
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
			}//FIN BUCLE FOR
			
		
			
		}
		else if(operacion == 2) {		//AVISAR QUE YO SOY COORDINADOR
			System.out.println("HILO	RUN	MULTIDIFUNDIR	YO COORDINADOR	" + getId());

				
			for(int i=0; i<ips.size(); i=i+2) {
				
				try {
					System.out.println(ips.get(i));


					URL url = new URL("http://" + ips.get(i) + ":8080/Servicio/rest/servicios/coordinador?coor=" + getId());
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
						System.out.println("COORDINADOR ME DEVUELVE " +output);
					}
					
						
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
			}	
				
		}
	}

}