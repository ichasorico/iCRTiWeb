package utils;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import utils.usuario;


public class conexionTest {

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
	    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    	Properties properties = new Properties();

	    	try  {
//	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		InputStream input = classLoader.getResourceAsStream("system_icrti.properties");
	    		properties.load(input);
	    		
				Class.forName (properties.getProperty("driverClassName")).newInstance ();			
				Connection connection = DriverManager.getConnection (properties.getProperty("url") + "/" + properties.getProperty("database"), properties.getProperty("username"), properties.getProperty("password"));			
				sentencia = connection.createStatement();
				System.out.println("TEST!!::conexionTest::initialize -> Conexión a la BBDD realizada:  url="+properties.getProperty("url") + "/database=" + properties.getProperty("database") + "/username="+ properties.getProperty("username") + "/password=" + properties.getProperty("password"));
	    	}catch(Exception e){
	    		System.out.println("url="+properties.getProperty("url") + "/database=" + properties.getProperty("database") + "/username="+ properties.getProperty("username") + "/password=" + properties.getProperty("password"));
    			System.out.println(e.toString());
	    	}	    	
	    }
	    
	    
	    @Test 
	    public void testConexionUsuarioValido()
	    {
	    	
	    	usuario u1 = new usuario(sentencia,usuario1, pwdUsuario1);
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
    			System.out.println(sql);
    			System.out.println(e.toString());
    		}
    		assertEquals(selloUsuario1, selloTest);
	    }
	    
	    @Test 
	    public void testConexionUsuario_NO_Valido()
	    {
	    	
	    	usuario u3 = new usuario(sentencia,usuario3, pwdUsuario3);
	    	//selloUsuario3 = u3.getFirma();
	    	//conexion c = new conexion(sentencia, u3,idServidor,true);
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
