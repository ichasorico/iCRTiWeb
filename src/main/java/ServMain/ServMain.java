package ServMain;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import users.admUser;
import users.webUser;
import utils.TwoFactorAuthUtil;
import utils.conexion;

import iCRTiConfig.getCfg;



/**
 * Servlet implementation class ServMain
 */
@WebServlet(description = "Servlet principal", urlPatterns = { "/ServMain" })

public class ServMain extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String base32Secret = "iCRTiWevChasoRic";
	private static final Logger LOGGER = LogManager.getLogger(ServMain.class);
	
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
    	
    	LOGGER.info("INIT  -- CONFIGURACIONES SERVIDOR AL ARRANCAR");

    	
    	
    	try {
			initDB();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			
			LOGGER.error("	Problema inicializando BBDD",e);
	        ServletException servletEx = new ServletException( e.getMessage(  ) );
	        servletEx.initCause( e );
	        throw servletEx;
			
		}
		

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
    
    private static void initDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
    	
/*
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream input = classLoader.getResourceAsStream("system_icrtiweb.properties");
    	Properties properties = new Properties();
*/
    	
    	getCfg cfgTool = new getCfg("icrtiweb");
		try {
//			properties.load(input);
//			Class.forName (properties.getProperty("driverClassName")).newInstance ();
			Class.forName (cfgTool.read("driverClassName")).newInstance ();
//			conn = DriverManager.getConnection (properties.getProperty("url") + "/" + properties.getProperty("database"), properties.getProperty("username"), properties.getProperty("password"));
			conn = DriverManager.getConnection (cfgTool.read("url") + "/" + cfgTool.read("database"), cfgTool.read("username"), cfgTool.read("password"));
			
//			LOGGER.info("INIT  -- CONEXIÓN A LA BBDD REALIZADA" + properties.getProperty("url") + "/" + properties.getProperty("database") + " " + properties.getProperty("username") + ":" + properties.getProperty("password"));    	
			LOGGER.info("INIT  -- CONEXIÓN A LA BBDD REALIZADA" + cfgTool.read("url") + "/" + cfgTool.read("database") + " " + cfgTool.read("username") + ":" + cfgTool.read("password"));
			

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			LOGGER.error("INIT  -- ERROR AL ESTABLECER driverClassName DESDE FICHERO PROPERTIES \n" + properties.getProperty("url") + "/" + properties.getProperty("database") + " " + properties.getProperty("username") + ":" + properties.getProperty("password") ,e);
			LOGGER.error("INIT  -- ERROR AL ESTABLECER driverClassName DESDE FICHERO PROPERTIES \n" + cfgTool.read("url") + "/" + cfgTool.read("database") + " " + cfgTool.read("username") + ":" + cfgTool.read("password") ,e);

			
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
			if(validarAdminRequest(request)){
				
			// IDEA a ver qué hacemos
				
		        if(request.getParameter("operacion") != null){
		        
		        	if("pingLogin".equals(request.getParameter("operacion"))){
		        		Long res = gestionaPing(request, response); 
		        		if ( res == Long.parseLong("0")){
		        			logOUT(request);
		        			response.getOutputStream().write((request.getContextPath()+"/icrtiWebAdmin/logOut.jsp").getBytes());
		        		}else{
		        			String respuesta = "LoginOK!!" + String.valueOf(res);
		        			response.getOutputStream().write(respuesta.getBytes());
		        		}
		        		
		        	}else if("login".equals(request.getParameter("operacion")) || "loginSecret".equals(request.getParameter("operacion"))){	
		        		LOGGER.info("USUARIO LOGADO. REDIRECCIÓN A LA INDEX DE ADMIN.");
		        		response.sendRedirect(request.getContextPath()+"/icrtiWebAdmin/index.jsp");
		        		
		        	}else if("logOut".equals(request.getParameter("operacion"))){
		        		logOUT(request);
		        		LOGGER.info("USUARIO LOGADO. PETICIÓN DE LOGOUT. REDIRECCIÓN A LA logout.jsp.");
		        		response.sendRedirect(request.getContextPath()+"/icrtiWebAdmin/logOut.jsp");
		        	}else{
		        		LOGGER.info("USUARIO LOGADO. NO SABEMOS TIPO PETICIÓN. REDIRECCIÓN A LA INDEX DE iCRTiWeb.");
		        		response.sendRedirect(request.getContextPath()+"/index.jsp");
		        	}	        
		        }
			}else{
				
				webUser wUser = new webUser(request,false);
				wUser.doEco();
				
				//DETERMINAMOS SI ES UNA NAVEGACIÓN POR LOS RECURSOS DE LA WEB
				if("X".equals(request.getParameter("operacion"))){
					LOGGER.info("SOLICITUD AJAX REGISTRO PÁGINA NO SERVIDA POR EL SERVLET .");
					//SE CORRESPONDE A UNA PETICIÓN AJAX SOLICITANDO REGISTRO ACCESO
	        		response.getOutputStream().write((request.getContextPath()+"OK!!!!").getBytes());

	        		//DETERMINAMOS SI ES UNA NAVEGACIÓN POR LOS RECURSOS DE LA WEB
				}else if("pingLogin".equals(request.getParameter("operacion"))){
					//SE CORRESPONDE A UNA PETICIÓN AJAX QUE HA PERDIDO LA SESIÓN
	        		response.getOutputStream().write((request.getContextPath()+"/logOut.jsp").getBytes());
	        		
				}else if("regStatic".equals(request.getParameter("operacion"))){
					//SE CORRESPONDE CON UNA NAVEGACIÓN POR LA WEB ESTÁTICA. PETICIÓN AJAX
					response.getOutputStream().write("regStaticOK!!".getBytes());
					
				}else if("login".equals(request.getParameter("operacion"))){
					LOGGER.info("REDIRECCIÓN A LA PÁGINA DE LOGIN.");
					HttpSession curSesion = request.getSession();
					curSesion.setAttribute("Login", "getLogin");
					response.sendRedirect(request.getContextPath()+"/icrtiWebAdmin/index.jsp");
					
				}else if("getIndex".equals(request.getParameter("operacion"))){
					LOGGER.info("REDIRECCIÓN A LA INDEX.");
					HttpSession curSesion = request.getSession();
					curSesion.setAttribute("Login", "getIndex");
					response.sendRedirect(request.getContextPath()+"/index.jsp");					
				}else{
					//
					response.sendRedirect(request.getContextPath()+"/icrtiWebAdmin/logOut.jsp");
				}
			}
		}else{
			//PROBLEMA INICIALIZACIÓN SERVLET
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
	 * 
	 * Proporciona información relacionada con la conexión administrador vía AJAX
	 * 
	 * @param request
	 * @param response
	 */
	private Long gestionaPing(HttpServletRequest request, HttpServletResponse response){
		
		HttpSession curSesion = request.getSession();
		Long tiempoActividad = 	Long.parseLong(curSesion.getAttribute("timeStamped").toString()) - 
								Long.parseLong(curSesion.getAttribute("timeStamp").toString()); 
		if(tiempoActividad 	> 	valiezTimeStamp	){
			LOGGER.warn("ServMain::gestionaPing TIEMPO INACTIVIDAD SUPERADO. LOGOUT USUARIO");
			curSesion.removeAttribute("sello");
			curSesion.removeAttribute("timeStamped");
			curSesion.removeAttribute("timeStamp");
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
				admUser usr = new admUser(sentencia, request.getParameter("usuario").toString(),request.getParameter("password").toString());
				
	    		if (usr.isUserOk()){
	    			HttpSession curSesion = request.getSession();
		        	String idSesion = curSesion.getId();
		        	usr.setIdSesion(idSesion);
		        	
	    			if(null != request.getParameter("secret") && !"".equalsIgnoreCase((String)request.getParameter("secret"))){
		    			LOGGER.info("login  -- Validamos SECRET");
		    			try{
			    			TwoFactorAuthUtil twoFactorAuthUtil = new TwoFactorAuthUtil();			    			
			    			String code = twoFactorAuthUtil.generateCurrentNumber(base32Secret);
			    			if (code.equals(request.getParameter("secret").toString())){
			    				LOGGER.info("validarAdminRequest  -- SECRET Validada ");
				        		curSesion.removeAttribute("loginOK!!");
				        		curSesion.removeAttribute("username");
				        		curSesion.removeAttribute("password");
				        		//NUEVA VERSIÓN CONTROL SESIONES CON PERSISTENCIA
				        		conexion c = new conexion(sentencia, usr,idSevidor,true);
				        		curSesion.setAttribute("sello", usr.getFirma());
				        		return true;
			    			}else{
			    				LOGGER.warn("login  -- SECRET errónea: se esperaba " + code +  " se recibe " + request.getParameter("secret").toString());
			    			}
		    			}catch(Exception e){
		    				LOGGER.error("login",e);
		    			}
	    			}else{
		        	
		        	
		        		LOGGER.info("login  -- Login ok. A la espera de la segunda vuelta");
		        		curSesion.setAttribute("loginOK!!", "loginOK!!");
		        		curSesion.setAttribute("username", usr.getNombre());
		        		curSesion.setAttribute("password", usr.getPwd());
		        		return true;
	    			}
	    			
	        		sentencia.close();	        		

	    		}else{
	    			// return false;
	    			LOGGER.warn("login  -- error login usuario");
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
     * DETERMINA SI EL USUARIO ESTÁ LOGADO A LA ADMINISTRACIÓN DE LA APLICACIÓN. 
     * PARA ELLO DETERMINA 
     * 		SI TIENE EN LA REQUEST EL PARÁMETRO SELLO
     * 			BUSCAMOS EL USUARIO EN LA LISTA DE sesionesActivas Y VALIDAMOS EL ID_SESIÓN
     * 		SI NO LO TIENE DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN 
     * 
     * @param request
     * @return boolean
     * @since 05/07/2016
     */
    private boolean validarAdminRequest(HttpServletRequest request){    	
    
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
	    			LOGGER.warn("validarAdminRequest  -- No se identifia REQUEST recibida");
	    		}
			} catch (SQLException e) {
				LOGGER.error("validarAdminRequest ",e);
			}
    	}

    	
    	

    	//LLEGADOS A ESTE PUNTO NO SE ENCUENTRA AL USUARIO LOGADO POR SESIÓN.
    	//DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN. CASO CONTRARIO, PASAMOS DE LA PETICIÓN
    	
    	LOGGER.info("validarAdminRequest  -- Acceso no logado. Se determinará si pide Login");

    	if(request.getParameter("operacion") != null && !"".equalsIgnoreCase(request.getParameter("operacion"))){
    		String operacion = request.getParameter("operacion");		        	
    		if(operacion.equals("login")){ // NOPMD by Íñigo on 4/07/16 16:19
    			return login(request);	        			

    		}else{
    			LOGGER.warn("validarAdminRequest  -- Usuario No Logado y Request operación que no es de LOGIN");
    		}
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
