package users;


import java.net.InetAddress;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class webUser  {

	private Logger LOGGER = LogManager.getLogger(webUser.class);
	
	public webUser(){		
	}
	
	
	public webUser(HttpServletRequest request, boolean outPutConsole){
		
		if(outPutConsole ){
			
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
	      
	      Enumeration headerNames = httpRequest.getHeaderNames();
	      while(headerNames.hasMoreElements()) {
	        String headerName = (String)headerNames.nextElement();
	        
	        if("host".equalsIgnoreCase(headerName)){
	        	try{
	        		String tmp = httpRequest.getHeader(headerName);
	        		if(tmp.indexOf(":") != -1){
	        			String tmp_[] = tmp.split(":");
	        			tmp = tmp_[0];
	        		}
	        		InetAddress address = InetAddress.getByName(tmp);
	        		LOGGER.info("Header IP - " + address.getHostAddress());
	        	}catch(Exception e){
	        		LOGGER.info("Header IP - No se ha podido determinar");
	        	}
	        }	         
	        
	        LOGGER.info("Header Name - " + headerName + ", Value - " + httpRequest.getHeader(headerName));
	      }
	      
	      
	      
	      Enumeration attrs = httpRequest.getAttributeNames();
	      while(attrs.hasMoreElements()){
		       String paramName = (String)attrs.nextElement();
		       LOGGER.info("Attribute Name - "+paramName+", Value - "+httpRequest.getAttribute((paramName)));
		      }
		      
	      
	      Enumeration params = httpRequest.getParameterNames(); 
	      while(params.hasMoreElements()){
	       String paramName = (String)params.nextElement();
	       LOGGER.info("Parameter Name - "+paramName+", Value - "+httpRequest.getParameter(paramName));
	      }
		}
		
		// TODO -> TENEMOS QUE VOLCAR AL SISTEMA DE FICHEROS LA REQUEST
		
	}

	public void setLog(HttpServletRequest request){
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
	      
	      Enumeration headerNames = httpRequest.getHeaderNames();
	      while(headerNames.hasMoreElements()) {
	        String headerName = (String)headerNames.nextElement();
	        
	        if("host".equalsIgnoreCase(headerName)){
	        	try{
	        		String tmp = httpRequest.getHeader(headerName);
	        		if(tmp.indexOf(":") != -1){
	        			String tmp_[] = tmp.split(":");
	        			tmp = tmp_[0];
	        		}
	        		InetAddress address = InetAddress.getByName(tmp);
	        		LOGGER.info("Header IP - " + address.getHostAddress());
	        	}catch(Exception e){
	        		LOGGER.info("Header IP - No se ha podido determinar");
	        	}
	        }	         
	        
	        LOGGER.info("Header Name - " + headerName + ", Value - " + httpRequest.getHeader(headerName));
	      }
	      
	      
	      
	      Enumeration attrs = httpRequest.getAttributeNames();
	      while(attrs.hasMoreElements()){
		       String paramName = (String)attrs.nextElement();
		       LOGGER.info("Attribute Name - "+paramName+", Value - "+httpRequest.getAttribute((paramName)));
		      }
		      
	      
	      Enumeration params = httpRequest.getParameterNames(); 
	      while(params.hasMoreElements()){
	       String paramName = (String)params.nextElement();
	       LOGGER.info("Parameter Name - "+paramName+", Value - "+httpRequest.getParameter(paramName));
	      }
		
	}

	public void doEco(){
		LOGGER.info("Usuario registrado");
	}
	
}
