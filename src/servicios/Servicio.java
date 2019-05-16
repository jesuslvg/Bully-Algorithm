package servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/servicios")
public class Servicio {
	
		static List<Proceso> Lista_Procesos = Collections.synchronizedList(new ArrayList<Proceso>());
		static HashMap<Integer, String> tabla_enrutamiento = new HashMap<Integer, String>();
		static Object bloqueo1 = new Object();
		static Object bloqueo2 = new Object();

		
		
		//crear recibe las IPs de las 3 máquinas y crea 2 procesos, uno con id=p y otro con id = p+1
		
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/crear")
		public String crear(@QueryParam(value="p") int p, @QueryParam(value="ip1") String ip1, 
				@QueryParam(value="ip2") String ip2, @QueryParam(value="ip3") String ip3) throws InterruptedException  
		{
			tabla_enrutamiento.put(1, ip1);
			tabla_enrutamiento.put(2, ip1);
			tabla_enrutamiento.put(3, ip2);
			tabla_enrutamiento.put(4, ip2);
			//tabla_enrutamiento.put(5, ip3);
			//tabla_enrutamiento.put(6, ip3);

			
			System.out.println("SERVICIO	CREAR: "+ p +"\t" + ip1 +"\t" + ip2 + "\t" + ip3);

			
			Lista_Procesos.add(new Proceso(p));
			p++;
			Lista_Procesos.add(new Proceso(p));
			
			for(int i=0;i<Lista_Procesos.size();i++)
			{
				Lista_Procesos.get(i).start();
			}
			
			
			for(int i=0;i<Lista_Procesos.size();i++)
			{
				Lista_Procesos.get(i).eleccion();
			}
		
			return "Creado: " + p +"\t" + ip1 +"\t" + ip2 + "\t" + ip3;
		}
		
		
		//RECIBE LA ORDEN DE PARAR UN PROCESO,EL PARÁMETRO, SERVICIO USADO POR EL GESTOR
		//http://localhost:8080/Servicio/rest/servicios/parar?p=
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/parar")
		public static String parar(@QueryParam(value="p") int p)  
		{
			System.out.println("SERVICIO "+ p + " 	PARAR");

			if(p==1 || p==3 || p==5) {
				Lista_Procesos.get(0).setEstado_eleccion("PARADO");		
				System.out.println("SERVICIO "+ p + " 	PARAR 1 3 o 5");


			}else if(p==2 || p==4 || p==6) {
				Lista_Procesos.get(1).setEstado_eleccion("PARADO");	
				System.out.println("SERVICIO "+ p + " 	PARAR 2 4 o 6");

			}
			
			System.out.println(Lista_Procesos.get(0).getEstado_eleccion() +" + "+  Lista_Procesos.get(1).getEstado_eleccion());
			return "PARAR";
		}
		
		//BLOQUEA AL PROCESO HACIENDO UN WAIT, SIMULANDO ASÍ SU DETENCIÓN, SERVICIO USADO POR EL LOS PROCESOS
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/terminar")
		public static String terminar(@QueryParam(value="p") int p) throws InterruptedException 
		{
			System.out.println("SERVICIO " + p + " TERMINAR");

			if(p==1 || p==3 || p==5) {
				
				synchronized(bloqueo1) {
					
					bloqueo1.wait();	
					
				}
			}else if(p==2 || p==4 || p==6) {
				
				synchronized(bloqueo2) {
					
					bloqueo2.wait();	
					
				}
			}
		

			return "terminar";
		}
		
		//DESBLOQUE EL PROCESO QUE RECIBE COMO PARÁMETRO
		//http://localhost:8080/Servicio/rest/servicios/arrancar?p=
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/arrancar")
		public static String arrancar(@QueryParam(value="p") int p) throws InterruptedException 
		{
			System.out.println("SERVICIO "+ p + " 	ARRANCAR");

			if(p==1 || p==3 || p==5) {
				
				Lista_Procesos.get(0).setEstado_eleccion("ELECCION_PASIVA");	
				
				synchronized(bloqueo1) {
					
					bloqueo1.notifyAll();	
					
					}
				Lista_Procesos.get(0).eleccion();

				

			}else if(p==2 || p==4 || p==6) {
				
				Lista_Procesos.get(1).setEstado_eleccion("ELECCION_PASIVA");	
				
				synchronized(bloqueo2) {
					
					bloqueo2.notifyAll();	
					
				}
				Lista_Procesos.get(1).eleccion();

			}

			return "ARRANCAR";
		}
		
		
		//DEVUELCE LA IP DEL PROCESO QUE RECIBE COMO PARAMETRO
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/ip_proceso")
		public static String ip_proceso(@QueryParam(value="p") int p) throws InterruptedException 
		{
			System.out.println("SERVICIO CONSULTAR	IP-PROCESO");			
			
			//consultar hashmap para saber la IP del coordinador
			String IP = tabla_enrutamiento.get(p);

			
			return IP;
		}
		
		//MANDA COMPUTAR AL PROCESO QUE RECIBE
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/computar")
		public static String computar(@QueryParam(value="coor") int coor) throws InterruptedException 
		{
			System.out.println("SERVICIO COMPUTAR");

			
			if(coor==1 || coor==3 || coor==5) {
				if(Lista_Procesos.get(0).getEstado_eleccion().equalsIgnoreCase("ACUERDO") 
						&& Lista_Procesos.get(0).getId()==Lista_Procesos.get(0).getCoordinador()) {
				
					return Lista_Procesos.get(0).computar();	
				}

			}else if(coor==2 || coor==4 || coor==6) {
				if(Lista_Procesos.get(1).getEstado_eleccion().equalsIgnoreCase("ACUERDO")
						&& Lista_Procesos.get(1).getId()==Lista_Procesos.get(1).getCoordinador()) {

					return Lista_Procesos.get(1).computar();						
				}
			}
			
			return "-1";	//COORDINADOR FALLADO

		}
		
		
		//MANDA ELECCION AL PROCESO QUE RECIBE
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/eleccion")
		public static String eleccion(@QueryParam(value="p") int p, @QueryParam(value="id") int id) 
				throws InterruptedException
		{
			
			System.out.println("SERVICIO "+ p + " 	ELECCION me manda la eleccion" + id);
			
			if(p==id) return " -1 YO HE INICIADO LA ELECCION";
			
			
			if(p==1 || p==3 || p==5) {
				//SI EL PROCESO NO ESTÁ PARADO ==> ELECCION
				if(!Lista_Procesos.get(0).getEstado_eleccion().equalsIgnoreCase("PARADO")) {
					enviarOK(id);
					Lista_Procesos.get(0).eleccion();
					return "ELECCION";
				}
			}else if(p==2 || p==4 || p==6) {
				if(!Lista_Procesos.get(1).getEstado_eleccion().equalsIgnoreCase("PARADO")) {
					enviarOK(id);
					Lista_Procesos.get(1).eleccion();
					return "ELECCION";
				}
			}

			return "PARADO " + p;
		}
		

		
		//Envía OK al Servicio del proceso del que recibe eleccion.
		private static void enviarOK(int p) {
			String IP = tabla_enrutamiento.get(p);
			System.out.println("SERVICIO ENVIAR OK A "+ p);

			try {
				//PERO ENVIO ELECCION AL I+1...
				URL url = new URL("http://"+IP +":8080/Servicio/rest/servicios/ok?p="+p);
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
					System.out.println("OK ME DUELVE "+output);
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


		//PARA RECIBIR UN MENSAJE DE CONTESTACION DE ELECCCION ==> ELECCCION PASIVA
		//SI EL PROCESO ESTÁ PARADO RETORNO -1, SINO 1
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/ok")
		public String ok(@QueryParam(value="p") int p)
		{
			
			System.out.println("SERVICIO "+ p + " 	OK");

			
			if(p==1 || p==3 || p==5) {
				
				if(!Lista_Procesos.get(0).getEstado_eleccion().equalsIgnoreCase("PARADO")) {

					Lista_Procesos.get(0).setEstado_eleccion("ELECCION_PASIVA");	
					return "OK";
				}
			}
			else if(p==2 || p==4 || p==6) {
				
				if(!Lista_Procesos.get(1).getEstado_eleccion().equalsIgnoreCase("PARADO")) {

					Lista_Procesos.get(1).setEstado_eleccion("ELECCION_PASIVA");	
					return "OK";
				}
			}

			return "PARADO " +p;
		}
		
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/coordinador")
		public String coordinador(@QueryParam(value="coor") int coor)
		{
			
			System.out.println("SERVICIO "+ coor + " 	COORDIANDOR");

			for(int i= 0; i<Lista_Procesos.size();i++) 
			{
				
				if(!Lista_Procesos.get(i).getEstado_eleccion().equalsIgnoreCase("PARADO")) {
					
					if(Lista_Procesos.get(i).getIdentificador()<coor) {
						
						Lista_Procesos.get(i).setCoordinador(coor);	
						Lista_Procesos.get(i).setEstado_eleccion("ACUERDO");
					}
				}
			}

			return "COORDINADOR";
		}
		
		
}
	
