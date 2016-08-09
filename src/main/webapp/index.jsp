<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<%

ServletContext sc = request.getServletContext();
String init = (String)sc.getAttribute("INIT_iCRTiWeb");
if(null == init){
	init="trueINIT";
}

if("false".equalsIgnoreCase(init)){
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Gestiones con mySQL</title>
</head>
<body>

	<script type=text/javascrip">
		window.location.href="error.html";
	</script>
</body>
	
<%	

}else{
	if (session.getAttribute("Login")==null){
		%>
	
		<%@ include file="WEB-INF/incl/loginform.jsp" %>	

		<%
	}else{
		
		%>		
	
		<%@ include file="WEB-INF/incl/bodyLoged.jsp" %>	
			
		<%
	}
	
}

%>

<!-- init=<%=init %> -->
</html>	
