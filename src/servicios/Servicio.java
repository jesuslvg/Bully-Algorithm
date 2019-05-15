package servicios;

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
		public String crear(@QueryParam(value="p") int p, @QueryParam(value="ip1") String ip1, @QueryParam(value="ip2") String ip2, @QueryParam(value="ip3") String ip3)
		{
			/*tabla_enrutamiento.put(1, ip1);
			tabla_enrutamiento.put(2, ip1);
			tabla_enrutamiento.put(3, ip2);
			tabla_enrutamiento.put(4, ip2);
			tabla_enrutamiento.put(5, ip3);
			tabla_enrutamiento.put(6, ip3);*/

			
			System.out.println("SERVICIO	CREAR: "+ p +"\t" + ip1 +"\t" + ip2 + "\t" + ip3);

			
			Lista_Procesos.add(new Proceso(p));
			p++;
			Lista_Procesos.add(new Proceso(p));
			
			for(int i=0;i<Lista_Procesos.size();i++)
			{
				Lista_Procesos.get(i).start();		
			}
		
			return "Creado: " + p +"\t" + ip1 +"\t" + ip2 + "\t" + ip3;
		}
		
		
		//RECIBE LA ORDEN DE PARAR UN PROCESO, SERVICIO USADO POR EL GESTOR
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/parar")
		public static String parar(@QueryParam(value="p") int p) throws InterruptedException 
		{
			System.out.println("SERVICIO "+ p + " 	PARAR");

			if(p==1 || p==3 || p==5) {
				Lista_Procesos.get(0).setEstado_eleccion("parado");		

			}else if(p==2 || p==4 || p==6) {
				Lista_Procesos.get(1).setEstado_eleccion("parado");	
			}

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
		
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/arrancar")
		public static String arrancar(@QueryParam(value="p") int p) throws InterruptedException 
		{
			System.out.println("SERVICIO "+ p + " 	ARRANCAR");

			if(p==1 || p==3 || p==5) {
				
				Lista_Procesos.get(0).setEstado_eleccion("corriendo");	
				
				synchronized(bloqueo1) {
					
					bloqueo1.notifyAll();	
					
					}
				

			}else if(p==2 || p==4 || p==6) {
				
				Lista_Procesos.get(1).setEstado_eleccion("corriendo");	
				
				synchronized(bloqueo2) {
					
					bloqueo2.notifyAll();	
					
				}
			}

			return "ARRANCAR";
		}
		
		
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/consultar_coordinador")
		public static String consultar_coordinador(@QueryParam(value="id") int id, @QueryParam(value="coor") int coor) throws InterruptedException 
		{
			//consultar hashmap para saber la IP del coordinador
			System.out.println("SERVICIO	CONSULTAR COORDINADOR");

			
			return "consultar_coordinador";
		}
		
	
		@GET 
		@Produces(MediaType.TEXT_PLAIN)
		@Path("/ok")
		public String ok(@QueryParam(value="p") int p)
		{
			
			
			return "a";
		}
}
	

