
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@include file="../Style.css" %>
</style>
</head>
<body>
<%
if (session.getAttribute("UserID") != null&&session.getAttribute("UserID").toString().equals("admin")) {
	String userID = request.getParameter("userID").toString();
	String userList = System.getProperty("catalina.base")+File.separator+"ESBAdmin"+File.separator+"Users";
	File userFile = new File(System.getProperty("catalina.base")
			+File.separator+ "ESBAdmin"+File.separator+userID);
	//remove the directory to remove user
	Util newUtil = new Util();
	newUtil.deleteDirectory(userFile);
	boolean flag = newUtil.updateUser(userList,userID);
	System.out.println(flag);
	if(flag){
		%>
		<center>User <%=userID %> removed from the system successfully.</center>
		<%
	}
	
}else{
	%>
	<center><b>You don't have access to this page.<br> 
			Please login with a valid user id <a href='../Index.html'><b>Here</b> </a></b><center>
	<%
}
		%>
</body>
</html>