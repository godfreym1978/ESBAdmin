<!-- 
/********************************************************************************/
/* */
/* Project: ESBAdmin */
/* Author: Godfrey Peter Menezes */
/* 
Copyright � 2015 Godfrey P Menezes
All rights reserved. This code or any portion thereof
may not be reproduced or used in any manner whatsoever
without the express written permission of Godfrey P Menezes(godfreym@gmail.com).

*/
/********************************************************************************/
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.esbadmin.*"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>User List</title>
</head>
<body>
<%if(session.getAttribute("UserID")==null){
%>
	<center>
	Looks like you are not logged in.<br>
		
	Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
	</center>
<%	
}else{
	if(session.getAttribute("UserID").toString().equals("admin")){
		try{ 
			Util newUtil = new Util();
							
			File userFile = new File(System.getProperty("catalina.base")
									+ File.separator+"ESBAdmin"+File.separator+"Users");
			if(userFile.exists()){
				%>
				<center>The list of users for this site.</center>
				<table border=1 align=center class="gridtable">
					<tr>
						<td><b>User Name</b></td>
						<td><b>Remove User</b></td>
					</tr>
		
					<%
					for (String line : FileUtils.readLines(userFile)) {
					%>
					<tr>
						<td><%=line%></td>
						<td><a href='RemUser.jsp?userID=<%=line%>'> <b>YES</b>
						</a></td> 
						<%
						} 
				}
			%>
				</table>
			<%
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
%>
</body>
</html>
