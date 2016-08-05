package ServMain;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.conexion;
import utils.usuario;
/**
 * Servlet implementation class ServMain
 */
@WebServlet(description = "Servlet principal", urlPatterns = { "/ServMain" })

public class ServMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private static List<usuario> sesiones = new ArrayList <usuario>();
	
	private static String cfgIdSevidor = "tomcatId4Persistencia";	
	private static String cfgValidezTimeStamp = "validezTimeStamp";
	private static  String idSevidor = "";
	
	private static long valiezTimeStamp = 0;
	
	private static  Connection conn = null;       
	private static boolean bINITT = false;
	  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServMain() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException{
    	
    	System.out.println("ServMain::INIT  -- CONFIGURACIONES SERVIDOR AL ARRANCAR");

    	initDB();

    	setValidezTimeStamp(getServletContext());
    	setIdServ(getServletContext());        
        
    	getServletContext().setAttribute("INIT_ICRTI", String.valueOf(limpiaConexionesActivas(idSevidor)));
    	
    }
    
    private static void setIdServ(ServletContext servletContext){
    	idSevidor = servletContext.getInitParameter(cfgIdSevidor);        
        System.out.println("ServMain::INIT cfgIdSevidor = " + idSevidor  );
        System.out.println("ServMain::INIT  -- CARGA INICIALIZACIÓN WEB.XML");  
    }
    
    private static void setValidezTimeStamp(ServletContext servletContext){
    	valiezTimeStamp = Long.parseLong(servletContext.getInitParameter(cfgValidezTimeStamp));        
        System.out.println("ServMain::INIT setValidezTimeStamp = " + idSevidor  );
        
    }
    
    private static void initDB(){
    	
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream input = classLoader.getResourceAsStream("system_icrti.properties");
    	Properties properties = new Properties();

    	
		Context envContext;
		try {
			properties.load(input);
			// ctx = new InitialContext();
			// envContext = (Context) ctx.lookup("java:comp/env");
			// DataSource ds = (DataSource) envContext.lookup("jdbc/sqlLocal");
			Class.forName (properties.getProperty("driverClassName")).newInstance ();			
			conn = DriverManager.getConnection (properties.getProperty("url") + "/" + properties.getProperty("database"), properties.getProperty("username"), properties.getProperty("password"));			
			System.out.println("ServMain::INIT  -- CONEXIÓN A LA BBDD REALIZADA");    	
			
			
		} catch (IOException e1) {
			System.out.println("ServMain::INIT  -- ERROR AL REALIZAR LECTURA FICHERO PROPERTIES ");
		
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ServMain::INIT  -- ERROR AL ESTABLECER driverClassName DESDE FICHERO PROPERTIES ");
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ServMain::INIT  -- ERROR AL ESTABLECER conexión DESDE FICHERO PROPERTIES ");
			e.printStackTrace();
		}
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 

		//response.getWriter().append("Served at: ").append(request.getContextPath());
		if (bINITT){
			if(validarRequest(request)){
				
			// IDEA a ver qué hacemos
				
		        if(request.getParameter("operacion") != null){
		        
		        	if("pingLogin".equals(request.getParameter("operacion"))){
		        		Long res = gestionaPing(request, response); 
		        		if ( res == Long.parseLong("0")){
		        			logOUT(request);
		        			response.getOutputStream().write("logOut.html".getBytes());
		        		}else{
		        			String respuesta = "LoginOK!!" + String.valueOf(res);
		        			response.getOutputStream().write(respuesta.getBytes());
		        		}
		        		
		        		 
		        	}else if("logOut".equals(request.getParameter("operacion"))){
		        		logOUT(request);
		        		response.sendRedirect("logOut.html");
		        	}else{
		        		response.sendRedirect("index.jsp");
		        	}	        
		        }
			}else{
				if("pingLogin".equals(request.getParameter("operacion"))){
		    		//logOUT(request);
	        		response.getOutputStream().write("logOut.html".getBytes());
				}else{
					response.sendRedirect("logOut.html");
				}
	    		//response.sendRedirect("logOut.html");
				
				//response.getOutputStream().write("logOut.html".getBytes());
			}
		}else{
			//PROBLEMA INICIALIZACIÓN 
			//ENVIAR A PÁGINA DE ERROR
			response.sendRedirect("error.html");
		}
		//response.sendRedirect("index.jsp");
        //request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * Proporciona información relacionada con la conexión vía AJAX
	 * @param request
	 * @param response
	 */
	private Long gestionaPing(HttpServletRequest request, HttpServletResponse response){
		
		HttpSession curSesion = request.getSession();
		Long tiempoActividad = 	Long.parseLong(curSesion.getAttribute("timeStamped").toString()) - 
								Long.parseLong(curSesion.getAttribute("timeStamp").toString()); 
		if(tiempoActividad 	> 	valiezTimeStamp	){
			System.out.println("ServMain::gestionaPing TIEMPO INACTIVIDAD SUPERADO. LOGOUT USUARIO");
    		return Long.parseLong("0");
		}else{
			System.out.println("ServMain::gestionaPing TIEMPO INACTIVIDAD CORRECTO.");
			return tiempoActividad;			
		}	
	}
	
	
    private boolean login(HttpServletRequest request){
    	
    	System.out.println("ServMain::login SOLICITUD DE LOGIN");
    	
    	if(request.getParameter("usuario") != null && request.getParameter("password") != null ){
    		
    		try {
				Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				usuario usr = new usuario(sentencia, request.getParameter("usuario").toString(),request.getParameter("password").toString());
				
	    		if (usr.isUserOk()){
	    			HttpSession curSesion = request.getSession();
		        	String idSesion = curSesion.getId();
		        	usr.setIdSesion(idSesion);
	    			//sesiones.add(usr);
	        		System.out.println("ServMain::login  -- Login ok");
	        		curSesion.setAttribute("Login", "true");
	        		
	        		//NUEVA VERSIÓN CONTROL SESIONES CON PERSISTENCIA
	        		conexion c = new conexion(sentencia, usr,idSevidor,true);
	        		curSesion.setAttribute("sello", usr.getFirma());
	        		sentencia.close();	
	        		return true;

	    		}else{
	    			// return false;
	    		}
	    		
	    		sentencia.close();
	    		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
   		 	
   		 	


		}else{
			//request.setAttribute("loginError","error");
			System.out.println("ServMain::login  -- Login errorrrr. FALTAN CREDENCIALES\nUsuario=" + request.getParameter("usuario")  + " pwd=" + request.getParameter("password") );
			//redirect+="?loginError";
		}		
    	
    	//response.sendRedirect(redirect);
    	return false;
    }
           
    /**
     * DETERMINAMOS SI EL USUARIO ESTÁ LOGADO. 
     * PARA ELLO DETERMINAMOS 
     * 		SI TIENE EN LA REQUEST EL PARÁMETRO USUARIO
     * 			BUSCAMOS EL USUARIO EN LA LISTA DE sesiones Y VALIDAMOS EL ID_SESIÓN
     * 		SI NO LO TIENE DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN (obligatorio)
     * 
     * @param request
     * @return boolean
     * @since 05/07/2016
     */
    private boolean validarRequest(HttpServletRequest request){    	
    
    	HttpSession session = request.getSession(true);
    	// DETERMINAMOS LLEGADA DEL SELLO
    	if (null != session.getAttribute("sello") && !"".equalsIgnoreCase(session.getAttribute("sello").toString())){
        	
    		//BÚSQUEDA POR PERSISTENCIA
    		try {
				Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				conexion c = new conexion(sentencia, session.getAttribute("sello").toString());
				session.setAttribute("timeStamp", c.getTimeStamp());
				session.setAttribute("timeStamped", String.valueOf(System.currentTimeMillis()));
				sentencia.close();
	    		if (c.isUserOk()){
	    			return true;
	    		}else{
	    			System.out.println("ServMain::validarRequest  -- No se identifia REQUEST recibida");
	    		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    	
    	

    	//LLEGADOS A ESTE PUNTO NO SE ENCUENTRA AL USUARIO LOGADO POR SESIÓN.
    	//DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN. CASO CONTRARIO, PASAMOS DE LA PETICIÓN
    	
    	System.out.println("ServMain::validarRequest  -- Acceso no logado. Se determinará si pide Login");

    	if(request.getParameter("operacion") != null && !"".equalsIgnoreCase(request.getParameter("operacion"))){
    		String operacion = request.getParameter("operacion");		        	
    		if(operacion.equals("login") || operacion.equals("login4Admin")){ // NOPMD by Íñigo on 4/07/16 16:19
    			return login(request);		        			
    		}else{
    			System.out.println("ServMain::validarRequest  -- Usuario No Logado y Request operación que no es de LOGIN");
    		}
    	}else{
        	System.out.println("ServMain::validarRequest  -- Usuario No Logado y Request sin solicitud de LOGIN");
    	}	        	

	  
	      return false;
    }
    
    
    /** 
     * ELIMINA SESIÓN ACTUAL DE LA LISTA DE SESIONES
     * @param request
     */
    private void logOUT(HttpServletRequest request){

    	conexion c = new conexion();
    	Statement sentencia;
		try {
			if (null != request.getSession(true).getAttribute("sello") && !"".equalsIgnoreCase(request.getSession(true).getAttribute("sello").toString())){			
				sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				c.removeSesion(sentencia, request.getSession(true).getAttribute("sello").toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	request.getSession(true).setMaxInactiveInterval(1);
    	request.getSession(true).invalidate();    	
    	
    }
    
	private static boolean limpiaConexionesActivas(String idServidor){
		
		String sql = "select count(*) as cuenta from conexionesactivas where idServidor = '" + idServidor + "'";
		Statement sentencia;
		ResultSet resultado;
		try {
			sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultado = sentencia.executeQuery(sql);
			resultado.first();
			System.out.println("ServMain::INITT::limpiaConexionesActivas("+idServidor+")  -  Se borrarán " + resultado.getString("cuenta") + " conexiones activas");
			resultado.close();
			sentencia.close();
			sql = "delete from conexionesactivas where idServidor = '" + idServidor + "'";
			try {
				sentencia = conn.createStatement();
				sentencia.execute(sql);
				System.out.println("ServMain::INITT::limpiaConexionesActivas("+idServidor+")  - Borrado de conexiones activas instancia + " + idServidor + "  OK !!!");
				sentencia.close();
				bINITT =true;
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return false;
	}
    
}
