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
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>iCRTiWeb</title>
	</head>
	<body>
	
		<script type=text/javascrip">
		window.location.href="error.html";
	</script>
	</body>
	
<%	
	
}else{
	
	%>
		
	<%@ include file="WEB-INF/incl/home.jsp" %>

	<%		
	
	if(null == session.getAttribute("Login") || !"getIndex".equalsIgnoreCase((String)session.getAttribute("Login")) ){
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
