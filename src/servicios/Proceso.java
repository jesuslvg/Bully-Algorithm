package servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Proceso extends Thread {
	
	private int identificador;
	private int coordinador;
	private String estado_eleccion;
	
	
	public Proceso (int i) {
		identificador = i;
		coordinador = 0;
		estado_eleccion = "por_defecto";
		
	}
	
	public int getIdentificador() {
		return identificador;
	}

	public int getCoordinador() {
		return coordinador;
	}

	public void setCoordinador(int coordinador) {
		this.coordinador = coordinador;
	}

	public String getEstado_eleccion() {
		return estado_eleccion;
	}

	public void setEstado_eleccion(String estado_eleccion) {
		this.estado_eleccion = estado_eleccion;
	}

	public void run() {
		
		while(true) {
		
			//System.out.println("PROCESO	" + identificador + "	RUN	"  + coordinador +"\t" + estado_eleccion);
	
			
			//SI ESTADO = PARADO ==> TERMINAR
			if(estado_eleccion.equalsIgnoreCase("parado")) {
				
				try {
					
					System.out.println("PROCESO	" + identificador + "	RUN		TERMINAR");
					
					URL url = new URL("http://localhost:8080/Servicio/rest/servicios/terminar?p="+getIdentificador());
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
					System.out.println("Output from Server .... \n");
					
					while((output = br.readLine()) != null) 
					{
						System.out.println(output);
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
				
			}else {	//SI ESTADO != PARADO ==> ESPERAR
	
				System.out.println("PROCESO	" + identificador + "	RUN		ESPERAR");
	
				try {
					Thread.sleep((long) (Math.random()*(1000-500)+500));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(coordinador!=0) {
					
					try {
						
						System.out.println("PROCESO	" + identificador + "	RUN		CONSULTAR_COORDINADOR");
	
	
						URL url = new URL("http://localhost:8080/Servicio/rest/servicios/consultar_coordinador?id=" + this.identificador + "?coor=" + this.coordinador);
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
						System.out.println("Output from Server .... \n");
						
						while((output = br.readLine()) != null) 
						{
							System.out.println(output);
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
					
				} //FIN IF COORDINADOR != 0
				
			} //FIN ELSE ESATOD != PARADO	
			
		} //FIN WHILE
	
	}

}
