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
<%@ page import="java.util.*"%>
<%@ page import="java.net.*,java.io.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@include file="../Style.css"%>
</style>
<title>Write Message to Queue</title>
</head>

<body>
	<center>
		<%
if(session.getAttribute("UserID")==null){
%>

	<center>
		Looks like you are not logged in.<br> Please login with a valid
		user id <a href='../Index.html'><b>Here</b> </a>
	</center>
	<%}else{

		String qmDtls = session.getAttribute(request.getParameter("qMgr")).toString();				
		String qMgr = request.getParameter("qMgr");
		String qPort = qmDtls.substring(qmDtls.indexOf(':') + 1,qmDtls.length());
		String qHost = qmDtls.substring(0, qmDtls.indexOf('|'));
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();
		String srcQueueName = request.getParameter("srcQueueName");
		String tarQueueName = request.getParameter("tarQueueName");
		String msgCount = request.getParameter("msgCount");
		ArrayList<String> messagesMoved = null;
		if (msgCount.equals("")){
			messagesMoved = 
				newUtil.messageMove(qMgr,qPort,qHost,srcQueueName,tarQueueName,"all");
		}else{
			messagesMoved = 
				newUtil.messageMove(qMgr,qPort,qHost,srcQueueName,tarQueueName,msgCount);
		}
		
		

			%>
		<table border=1 align=center width=50% class="gridtable">
			<tr>
				<td>Queue Mgr</td>
				<td><input type=hidden name=qMgr value=<%=qMgr%>><%=qMgr%></td>
			</tr>
			<tr>
				<td>Source Queue</td>
				<td><input type=hidden name=srcQueueName value=<%=srcQueueName%>><%=srcQueueName%></td>
			</tr>
			<tr>
				<td>Source Queue</td>
				<td><input type=hidden name=srcQueueName value=<%=tarQueueName%>><%=tarQueueName%></td>
			</tr>
			<tr>
				<td>Message Count</td>
				<td><input type=hidden name=msgCount value=<%=messagesMoved.size()%>><%=messagesMoved.size()%></td>
			</tr>
		<%
		for(int i=0;i<messagesMoved.size();i++){
			%>
			<tr>
				<td><%=(i+1)%></td>
				<td><%=messagesMoved.get(i)%></td>
			</tr>
			
		<%
		}
	}
		
		
			 %>	
		</table>
		
</body>
</html>