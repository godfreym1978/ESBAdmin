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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Message Broker Environment</title>
</head>
<body>
	<%
	if(session.getAttribute("UserID")==null){
	%>
		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>
	
	<%	
	}else{
	%>
			<center><h3> Message Broker Environment</h3></center>
			<form action='DPEnvSave.jsp' method="post">

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Datapower Symbolic Name</b></th>
					<th><b>Datapower Host</b></th>
					<th><b>Datapower UserID</b></th>
					<th><b>Datapower Admin Port</b></th>
				</tr>
				<tr>
					<td><%=request.getParameter("dpSymName")%></td>
					<td><%=request.getParameter("dpHost")%></td>
					<td><%=request.getParameter("dpUserID")%></td>
					<td><%=request.getParameter("dpAdminPort")%></td>
				</tr>
			</table>

	<%	
		DPUtil newDPUtil = new DPUtil();
		newDPUtil.registerDevice(request.getParameter("dpSymName"),
						request.getParameter("dpHost"),
						request.getParameter("dpUserID"),
						request.getParameter("dpPasswd"),
						Integer.parseInt(request.getParameter("dpAdminPort")));
		

	}
%>


</body>
</html>