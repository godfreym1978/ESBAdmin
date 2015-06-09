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
<%@ page import="com.ibm.ESBAdmin.*" %>
<%@ page import="java.util.*" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
</head>
<%if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>

<%	
}else{
%>

<body>
	<center>
		<%
			String qMgr = request.getParameter("qMgr");
			String qName = request.getParameter("qName");
			String message = request.getParameter("message");

			Util newUtil = new Util();
			//String returnMsg = 
			newUtil.readMessage(qMgr, qName, message);
		%>
		<table class="gridtable">
			<tr>
				<td>Queue Manager</td>
				<td><%=request.getParameter("qMgr")%></td>
			</tr>
			<tr>
				<td>Queue Name</td>
				<td><%=request.getParameter("qName")%></td>
			</tr>
			<tr>
				<td>Message</td>
				<td><%=request.getParameter("message")%></td>
			</tr>
		</table>
<%} 
System.gc();
%>
</body>
</html>