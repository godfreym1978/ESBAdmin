<!-- 
/********************************************************************************/
/* */
/* Project: ESBAdmin */
/* Author: Godfrey Peter Menezes */
/* 
Copyright © 2015 Godfrey P Menezes
All rights reserved. This code or any portion thereof
may not be reproduced or used in any manner whatsoever
without the express written permission of Godfrey P Menezes(godfreym@gmail.com).

*/
/********************************************************************************/
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.esbadmin.*"%>
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