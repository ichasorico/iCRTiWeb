package ServMain;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.conexion;
import utils.usuario;
/**
 * Servlet implementation class ServMain
 */
@WebServlet(description = "Servlet principal", urlPatterns = { "/ServMain" })

public class ServMain extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static String cfgIdSevidor = "tomcatId4Persistencia";	
	private static String cfgValidezTimeStamp = "validezTimeStamp";
	private static  String idSevidor = "";
	
	private static final Logger LOGGER = LogManager.getLogger(ServMain.class);
	
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
    	
    	LOGGER.info("INIT  -- CONFIGURACIONES SERVIDOR AL ARRANCAR");

    	
    	
    	initDB();

    	setValidezTimeStamp(getServletContext());
    	setIdServ(getServletContext());        
        
    	getServletContext().setAttribute("INIT_iCRTiWeb", String.valueOf(limpiaConexionesActivas(idSevidor)));
    	
    }
    
    
    
    private static void setIdServ(ServletContext servletContext){
    	idSevidor = servletContext.getInitParameter(cfgIdSevidor);        
    	LOGGER.info("INIT::cfgIdSevidor = " + idSevidor  + "   -- CARGA INICIALIZACIÓN WEB.XML");  
    }
    
    private static void setValidezTimeStamp(ServletContext servletContext){
    	valiezTimeStamp = Long.parseLong(servletContext.getInitParameter(cfgValidezTimeStamp));        
    	LOGGER.info("INIT setValidezTimeStamp = " + idSevidor  );
        
    }
    
    private static void initDB(){
    	
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream input = classLoader.getResourceAsStream("system_icrti.properties");
    	Properties properties = new Properties();

    	

		try {
			properties.load(input);
			Class.forName (properties.getProperty("driverClassName")).newInstance ();			
			conn = DriverManager.getConnection (properties.getProperty("url") + "/" + properties.getProperty("database"), properties.getProperty("username"), properties.getProperty("password"));			
			LOGGER.info("INIT  -- CONEXIÓN A LA BBDD REALIZADA" + properties.getProperty("url") + "/" + properties.getProperty("database") + " " + properties.getProperty("username") + ":" + properties.getProperty("password"));    	
			
			
		} catch (IOException e1) {
			LOGGER.error("INIT  -- ERROR AL REALIZAR LECTURA FICHERO PROPERTIES ", e1);


		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LOGGER.error("INIT  -- ERROR AL ESTABLECER driverClassName DESDE FICHERO PROPERTIES \n" + properties.getProperty("url") + "/" + properties.getProperty("database") + " " + properties.getProperty("username") + ":" + properties.getProperty("password") ,e);

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("INIT  -- ERROR AL ESTABLECER conexión DESDE FICHERO PROPERTIES ",e);

		}
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 


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
					
	        		response.getOutputStream().write("logOut.html".getBytes());
				}else{
					response.sendRedirect("logOut.html");
				}
			}
		}else{
			//PROBLEMA INICIALIZACIÓN 
			//ENVIAR A PÁGINA DE ERROR
			response.sendRedirect("error.html");
		}


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
			LOGGER.warn("ServMain::gestionaPing TIEMPO INACTIVIDAD SUPERADO. LOGOUT USUARIO");
    		return Long.parseLong("0");
		}else{
			LOGGER.info("gestionaPing TIEMPO INACTIVIDAD CORRECTO.");
			return tiempoActividad;			
		}	
	}
	
	
    private boolean login(HttpServletRequest request){
    	
    	LOGGER.warn("login SOLICITUD DE LOGIN");
    	
    	if(request.getParameter("usuario") != null && request.getParameter("password") != null ){
    		
    		try {
				Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				usuario usr = new usuario(sentencia, request.getParameter("usuario").toString(),request.getParameter("password").toString());
				
	    		if (usr.isUserOk()){
	    			HttpSession curSesion = request.getSession();
		        	String idSesion = curSesion.getId();
		        	usr.setIdSesion(idSesion);
	    			
	        		LOGGER.info("login  -- Login ok");
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
				LOGGER.error("login ",e);
			}

		}else{
			LOGGER.warn("login  -- Login errorrrr. FALTAN CREDENCIALES\nUsuario=" + request.getParameter("usuario")  + " pwd=" + request.getParameter("password") );

		}
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
	    			LOGGER.warn("validarRequest  -- No se identifia REQUEST recibida");
	    		}
			} catch (SQLException e) {
				LOGGER.error("validarRequest ",e);
			}
    	}

    	
    	

    	//LLEGADOS A ESTE PUNTO NO SE ENCUENTRA AL USUARIO LOGADO POR SESIÓN.
    	//DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN. CASO CONTRARIO, PASAMOS DE LA PETICIÓN
    	
    	LOGGER.info("validarRequest  -- Acceso no logado. Se determinará si pide Login");

    	if(request.getParameter("operacion") != null && !"".equalsIgnoreCase(request.getParameter("operacion"))){
    		String operacion = request.getParameter("operacion");		        	
    		if(operacion.equals("login") || operacion.equals("login4Admin")){ // NOPMD by Íñigo on 4/07/16 16:19
    			return login(request);		        			
    		}else{
    			LOGGER.warn("validarRequest  -- Usuario No Logado y Request operación que no es de LOGIN");
    		}
    	}else{
    		LOGGER.warn("validarRequest  -- Usuario No Logado y Request sin solicitud de LOGIN");
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
			LOGGER.error("logOUT " ,e);
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
			LOGGER.warn("INITT::limpiaConexionesActivas("+idServidor+")  -  Se borrarán " + resultado.getString("cuenta") + " conexiones activas");
			resultado.close();
			sentencia.close();
			sql = "delete from conexionesactivas where idServidor = '" + idServidor + "'";
			try {
				sentencia = conn.createStatement();
				sentencia.execute(sql);
				LOGGER.warn("INITT::limpiaConexionesActivas("+idServidor+")  - Borrado de conexiones activas instancia + " + idServidor + "  OK !!!");
				sentencia.close();
				bINITT =true;
				return true;
			} catch (SQLException e) {
				LOGGER.error("INIT::limpiaConexionesActivas",e);
			}catch (Exception e){
				LOGGER.error("INIT::limpiaConexionesActivas",e);			}			
		} catch (SQLException e) {
			LOGGER.error("INIT::limpiaConexionesActivas",e);
		}catch (Exception e){
			LOGGER.error("INIT::limpiaConexionesActivas",e);
		}
				
		return false;
	}
    
}
