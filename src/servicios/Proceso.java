package servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;





public class Proceso extends Thread {
	private String IP_Coordinador;







	private int identificador;
	private int coordinador;
	private String estado_eleccion;
	int contador;
	
	
	public Proceso (int i) {
		identificador = i;
		coordinador = 0;
		estado_eleccion = "ELECCION_ACTIVA";
		
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
	
	public String getIP_Coordinador() {
		return IP_Coordinador;
	}

	public void setIP_Coordinador(String iP_Coordinador) {
		IP_Coordinador = iP_Coordinador;
	}
	
	
	
	
	public String computar() throws InterruptedException 
	{

		Thread.sleep((long)Math.random()*(300-100));
		return "1";
		
	}
	
	
	public void eleccion() throws InterruptedException  
	{
		/* operacion 1 eleccion, operacion 2 informar que yo mando*/
		do 
		{
			setEstado_eleccion("ELECCION_ACTIVA");
			System.out.println("ELECCION 	PROCESO	" + getIdentificador() +  "   " + getEstado_eleccion());
			Hilo h1 = new Hilo(getIdentificador(), 1);
			h1.start();
			Thread.sleep(1000);
	

			//SI RECIBO OK ==> ELECCION PASIVA ==> ESPERAR Y CONSULTAR NUEVO COORDIANDOR
			if(getEstado_eleccion().equalsIgnoreCase("ELECCION_PASIVA"))
			{		
				Thread.sleep(5000);
				System.out.println("ELECCION 	PROCESO	" + getIdentificador() +  "   " + getEstado_eleccion());

			}
			
			
			//SI NO HAY COORDINADOR Y ALGUIEN A DICHO OK ==> VUELVO ARRIBA
		}while((getCoordinador() == 0) && 
					(getEstado_eleccion().equalsIgnoreCase("ELECCION_PASIVA")));

		
		
		//SI NO HAY COORDINADOR Y NINGUN PROCESO ME CONTESTA CON OK ==>YO SOY EL ID MAYOR
		if((getCoordinador() == 0) && 
				(getEstado_eleccion().equalsIgnoreCase("ELECCION_ACTIVA"))) 
		{	//YO SERÉ EL COORDINADOR
			System.out.println("	ELECCION PROCESO	" + getIdentificador() +  "   " + getEstado_eleccion());

				setCoordinador(getIdentificador());
				Hilo h2 = new Hilo(getIdentificador(), 2);
				h2.start();
		}
			
			
	}
	
	public void ok() {
		setEstado_eleccion("ELECCION_PASIVA");
	}

	
	
	
	
	
	public void run() {
		
		/*try {
			this.eleccion();
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/
		
		while(true) {
		
			//System.out.println("PROCESO	" + identificador + "	RUN	"  + coordinador +"\t" + estado_eleccion);
	
			
			//SI ESTADO = PARADO ==> TERMINAR
			if(estado_eleccion.equalsIgnoreCase("PARADO")) {				
				try {
					
					System.out.println("PROCESO	" + getIdentificador()+ "	RUN		" + getEstado_eleccion() + "	" + getCoordinador());
					
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
				
				/*if(estado_eleccion.equalsIgnoreCase("ELECCION_ACTIVA")) {				
					try {
						eleccion();
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				System.out.println("PROCESO	" + getIdentificador() + "	RUN		" + getEstado_eleccion() + "	" + getCoordinador());
	
				try {
					Thread.sleep((long) (Math.random()*(1000-500)+500));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				//SI HAY COORDINADOR Y ACUERDO ==> OBTENER SU IP
				if(getCoordinador()!=0 && getEstado_eleccion().equalsIgnoreCase("ACUERDO")) { 	
					
					
										
					if(getCoordinador() != getIdentificador()) { //SI NO SOY EL COORDIANDOR ==> OBTENER SU IP
						
						try {
							
							System.out.println("PROCESO	" + getIdentificador() + "   " + getEstado_eleccion() +  "	RUN		CONSULTAR_IP_COORDINADOR");
		
		
							URL url = new URL("http://localhost:8080/Servicio/rest/servicios/ip_proceso?p=" + getCoordinador());
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
								IP_Coordinador= output;
							}
						
							System.out.println(IP_Coordinador);
							
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
						
						
						
						
						// CUANDO TENGO SU IP LLAMO A COORDINADOR.COMPUTAR (LLAMO AL SERVIDOR DEL COORDINADOR)
						
						System.out.println("PROCESO	" + getIdentificador() +  "   " + getEstado_eleccion() + "	RUN		COMPUTAR");
						try {
						
							URL url = new URL("http://"+ "localhost" +":8080/Servicio/rest/servicios/computar?coor="+this.getCoordinador());
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
							int result =-1;	

							while((output = br.readLine()) != null) 
							{
								System.out.println(output);
								result = Integer.parseInt(output);	

							}

							//SI COMPUTAR DEVUELVE !=1 ERROR EN COORDINADOR ==> ELECCIONES.
							if(result != 1) {
								setCoordinador(0);
								setEstado_eleccion("ELECCION_ACTIVA");
								eleccion();
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
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {//SI SOY EL COORDINADOR ==> MI METODO COMPUTAR()
					
						try {
							String r= this.computar();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}//FIN YO SOY EL COORDINADOR
					
				} //FIN IF COORDINADOR != 0
				
			} //FIN ELSE ESATOD != PARADO	
			
		} //FIN WHILE
	
	}

}