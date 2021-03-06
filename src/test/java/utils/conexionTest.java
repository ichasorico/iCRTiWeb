package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import users.admUser;
import iCRTiConfig.getCfg;


public class conexionTest {

	private static Logger LOGGER = null;
	
		private String usuario1 = "ingo";
		private String usuario2 = "macario";
		private String pwdUsuario1 = "asteroide";
		private String pwdUsuario2 = "asteroida";
		private boolean usuario1IsAdmin = false;
		private boolean usuario2IsAdmin = false;
		
		// SELLOS
		private String selloUsuario1 = "";
		
		private String idServidor = "tomcat1";
		
		// USUARIO TEST ERROR
		private String usuario3 = "felipe";
		private String pwdUsuario3 = "asteroidX";
		private String selloUsuario3 = null;
	
		private String idUsuario1 = "";
		private String idUsuario2 = "";
		
		
		  InitialContext ctx = null;
		  DataSource ds = null;
		  Connection conn = null;       
		  Statement sentencia = null;
		  
	    @Before	    
	    public void initialize()	     
	    {
	    	
	        System.setProperty("log4j.configurationFile","log4j_testing.xml");
	        LOGGER = LogManager.getLogger();
	        
	    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    	//Properties properties = new Properties();
	    	getCfg cfgTool = new getCfg("icrtiweb");

	    	try  {
	    		
				Class.forName (cfgTool.read("driverClassName")).newInstance ();
				Connection connection = DriverManager.getConnection (cfgTool.read("url") + "/" + cfgTool.read("database"), cfgTool.read("username"), cfgTool.read("password"));
				
				sentencia = connection.createStatement();
				LOGGER.info("conexionTest::initialize -> Conexión a la BBDD realizada:  url="+cfgTool.read("url") + "/database=" + cfgTool.read("database") + "/username="+ cfgTool.read("username") + "/password=" + cfgTool.read("password"));
				
	    	}catch(Exception e){
	    		LOGGER.error("initialize url="+cfgTool.read("url") + "/database=" + cfgTool.read("database") + "/username="+ cfgTool.read("username") + "/password=" + cfgTool.read("password"),e);
	    	}	    	
	    }
	    
	    
	    @Test 
	    public void testConexionUsuarioValido()
	    {
	    	
	    	admUser u1 = new admUser(sentencia,usuario1, pwdUsuario1);
	    	selloUsuario1 = u1.getFirma();
	    	conexion c = new conexion(sentencia, u1,idServidor,true);
	    	// VERFICIACIÓN SGBD
	    	String sql = "SELECT * FROM conexionesactivas where idSesion = '"+selloUsuario1+"'";	    	
	    	String selloTest = ""; 
    		try{
    			ResultSet resultado = sentencia.executeQuery(sql);
	    		resultado.first();	    		
	    		selloTest = resultado.getString("idSesion");	    		
    		}catch(Exception e){
    			LOGGER.error("testConexionUsuarioValido \n" +  sql, e);
    		}
    		assertEquals(selloUsuario1, selloTest);
	    }
	    
	    @Test 
	    public void testConexionUsuario_NO_Valido()
	    {
	    	
	    	admUser u3 = new admUser(sentencia,usuario3, pwdUsuario3);
	    	assertNull(u3.getFirma());
	    	/*
	    	// VERFICIACIÓN SGBD
	    	String sql = "SELECT * FROM conexionesactivas where idSesion = '"+selloUsuario1+"'";	    	
	    	String selloTest = ""; 
    		try{
    			ResultSet resultado = sentencia.executeQuery(sql);
	    		resultado.first();	    		
	    		selloTest = resultado.getString("idSesion");	    		
    		}catch(Exception e){
    			System.out.println(sql);
    			System.out.println(e.toString());
    		}
    		//assertEquals(selloUsuario1, selloTest);
    		  */
    		 
	    }
}
