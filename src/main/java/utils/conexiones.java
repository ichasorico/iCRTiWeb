package utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class conexiones {


	  boolean conexion = false;
	  boolean loginOK = false;
	  
	
	public conexiones(){
	    
	  }
	
	public void clearAll(Statement sentencia, String idServidor){
		
		String sql = "select count(*) as cuenta from conexionesactivas where idServidor = '" + idServidor + "'";
		
		ResultSet resultado;
		try {
			// sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultado = sentencia.executeQuery(sql);
			resultado.first();
			System.out.println("conexiones::clearAll  -  Se borrarán " + resultado.getString("cuenta") + " conexiones activas");
			resultado.close();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "delete from conexionesactivas where idServidor = '" + idServidor + "'";
		try {
			// sentencia = conn.createStatement();
			sentencia.execute(sql);
			System.out.println("conexiones::clearAll  - Borrado de conexiones activas instancia + " + idServidor + "  OK !!!");
			sentencia.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
