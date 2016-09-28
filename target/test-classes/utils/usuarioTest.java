package utils;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import iCRTiConfig.getCfg;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import users.admUser;


public class usuarioTest {

	private static Logger LOGGER = null;
	
		private String usuario1 = "ingo";
		private String usuario2 = "macario";
		private String pwdUsuario1 = "asteroide";
		private String pwdUsuario2 = "asteroida";
		private boolean usuario1IsAdmin = false;
		private boolean usuario2IsAdmin = false;
		
		// USUARIO TEST ERROR
		private String usuario3 = "felipe";
		private String pwdUsuario3 = "asteroidX";

	
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
	        getCfg cfgTool = new getCfg("icrtiweb");


	    	try  {
	    		InputStream input = classLoader.getResourceAsStream("system_icrtiweb.properties");
	    		
				Class.forName (cfgTool.read("driverClassName")).newInstance ();
				Connection connection = DriverManager.getConnection (cfgTool.read("url") + "/" + cfgTool.read("database"), cfgTool.read("username"), cfgTool.read("password"));
				
				sentencia = connection.createStatement();
				LOGGER.info("initialize::Conexión a la BBDD realizada:  url="+cfgTool.read("url") + "/database=" + cfgTool.read("database") + "/username="+ cfgTool.read("username") + "/password=" + cfgTool.read("password"));
				
	    		// OBTENEMOS idUsuario PARA USUARIO1
	    		String sql = "select * from usuarios where nombre = '" + usuario1 +"'";
	    		ResultSet resultado = sentencia.executeQuery(sql);
	    		resultado.first();
	    		idUsuario1 = resultado.getString("idUsuario");
	    		// OBTENEMOS idUsuario PARA USUARIO2
	    		sql = "select * from usuarios where nombre = '" + usuario2 +"'";
	    		resultado = sentencia.executeQuery(sql);
	    		resultado.first();
	    		idUsuario2 = resultado.getString("idUsuario");


	    		// OBTENEMOS isUserAdmin PARA USUARIO1
	    		sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario in(SELECT idUsuario FROM usuarios where nombre = '"+ usuario1 +"'"+")) and admin = 1";
	    		resultado = sentencia.executeQuery(sql);
	    		try{
		    		resultado.first();
		    		if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
						usuario1IsAdmin = true;
					}else{				
						usuario1IsAdmin = false;
					}
	    		}catch(Exception e){
	    			usuario1IsAdmin = false;
	    		}
	    		
	    		// OBTENEMOS isUserAdmin PARA USUARIO2
	    		sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario in(SELECT idUsuario FROM usuarios where nombre = '"+ usuario2 +"'"+")) and admin = 1";
	    		resultado = sentencia.executeQuery(sql);
	    		try{
		    		resultado.first();
		    		if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
						usuario2IsAdmin = true;
					}else{				
						usuario2IsAdmin = false;
					}
	    			
	    		}catch(Exception e){
	    			usuario2IsAdmin = false;
	    		}
	    		

	    	} catch (SQLException e) {
	    	    throw new IllegalStateException("Cannot connect the database!", e);	    	    
	    		
			} catch (InstantiationException e1) {
				LOGGER.error("initialize::",e1);
				
			} catch (IllegalAccessException e1) {
				LOGGER.error("initialize::",e1);
				
			} catch (ClassNotFoundException e1) {
				LOGGER.error("initialize::",e1);
				
			}
	    }
	    
	    @Test 
	    public void testUsers()
	    {
	    	

	    	admUser u1 = new admUser(sentencia,usuario1, pwdUsuario1);
    		admUser u2 = new admUser(sentencia, usuario2, pwdUsuario2);	  
	    	//assertEquals(u1,u2);
	    	//assertEquals(u1.getIdUsuario(), u2.getIdUsuario());
	    	assertEquals(u1.isUserOk(), u2.isUserOk());

	    }
	    
	    @Test 
	    public void testUser1()
	    {	    	

    		admUser u1 = new admUser(sentencia,usuario1, pwdUsuario1);	    	
    		assertEquals( "validación idUsuario para Usuario.u1 ", u1.getIdUsuario(), idUsuario1);	       
	    }
	    
	    @Test 
	    public void testUser1isAdmin()
	    {	    	

    		admUser u1 = new admUser(sentencia,usuario1, pwdUsuario1);	    	
    		assertEquals( "validación userIsAdmin para Usuario.u1 ", u1.isAdmin(), usuario1IsAdmin);	       
	    }
	    
	    @Test 
	    public void testUser2isAdmin()
	    {	    	

    		admUser u2 = new admUser(sentencia,usuario2, pwdUsuario2);	    	
    		assertEquals( "validación userIsAdmin para Usuario.u2 ", u2.isAdmin(), usuario2IsAdmin);	       
	    }
	    
	    @Test 
	    public void testUser2()
	    {

    		admUser u2 = new admUser(sentencia, usuario2, pwdUsuario2);
    		assertEquals( "validación idUsuario para Usuario.u2 ", u2.getIdUsuario(), idUsuario2);
	       
	    }

	    @Test 
	    public void testUserLoginFail()
	    {

    		admUser u3 = new admUser(sentencia, usuario3, pwdUsuario3);
	    	assertEquals(u3.isUserOk(), false);
	       
	    }

}