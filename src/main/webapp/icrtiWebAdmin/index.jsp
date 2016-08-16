<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<%

ServletContext sc = request.getServletContext();
String init = (String)sc.getAttribute("INIT_iCRTiWeb");
if(null == init){
	init="notINIT";
}

if("false".equalsIgnoreCase(init)){
%>

	
<%	
	
}else{

		// DETERMINAMOS SI EL USUARIO HA INICIADO SESIÓN CONTRA EL SERVIDOR
		if(null != session.getAttribute("sello") && !"".equalsIgnoreCase((String) session.getAttribute("sello"))){
			
			%>
			
				<%@ include file="incl/bodyLoged.jsp" %>	

			<%

		}else{
	
			%>
			
				<%@ include file="incl/loginform.jsp" %>	
	
			<%
		}
		
		if(null == session.getAttribute("Login") || !"getLogin".equalsIgnoreCase((String)session.getAttribute("Login")) ){
			// GENERAMOS PETICIÓN AJAX PARA REGISTRAR EL ACCESO
			
			%>
			
			    <script>

			       var params="operacion=X";       
			       $.ajax({url:"<%=request.getContextPath()%>/ServMain",
			           type:"POST",
			           data:params,
			           success: function(result){              
			           }
			       });       
	    
	    		</script>
			
			<%
			
		}
}

%>

<!-- init=<%=init %> -->
</html>	
