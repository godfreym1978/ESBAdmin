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
<%@ page import="java.sql.*" %>
<%@ page import="sun.misc.BASE64Decoder" %>
<%@ page import="com.jcraft.jsch.*" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
</head>
<body>
<center><button type="button" onClick="window.location.reload();">Refresh</button></center>
<%if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>

<%	
}else{

	String hostName = request.getParameter("Env").toString();
	String month = request.getParameter("month");
	String day = request.getParameter("day");
	String hour = request.getParameter("hour");
	String tailCount = request.getParameter("tailCount");
	boolean realTime = false;
	
	Util newUtil= new Util();  
	
	ArrayList<String> sysLogStr = newUtil.ListSyslog(hostName, tailCount, month, day, hour, realTime);
	String workStr = new String("");

%>
<table border="1" align=center class="gridtable">
	<tr>
		<td><b>Event Timestamp</b></td>
		<td><b>Event Brief</b></td>
		<td><b>Event Message</b></td>
	</tr>

<%
	String date = null;
	String newStr = null;
	String logType = null;
	String logMsg = null;

	for (int listCtr = sysLogStr.size()-1; listCtr >=0  ; listCtr--) {
		if(sysLogStr.get(listCtr).indexOf(" c")>0){
			date = sysLogStr.get(listCtr).substring(0,sysLogStr.get(listCtr).indexOf(" c"));
			newStr = sysLogStr.get(listCtr).substring(sysLogStr.get(listCtr).indexOf(" c")+1,sysLogStr.get(listCtr).length());
			logType = newStr.substring(newStr.indexOf(" ")+1,newStr.length());
			logMsg = logType.substring(logType.indexOf(" ")+1,logType.length());
	
			if(logType.indexOf("err|")>0){
%>
		
	<tr>
		<td><font color=red><%=date%></font></td>
		<td><font color=red><%=logType.substring(0,logType.indexOf(" "))%></font></td>
		<td><font color=red><%=logMsg%></font></td>

	</tr>
	
<%
		
			}else{
%>
	<tr>
		<td><%=date%></td>
		<td><%=logType.substring(0,logType.indexOf(" "))%></td>
		<td><%=logMsg%></td>

	</tr>
<%
		
			}
		}
	}
}
System.gc();
%>
</table>
</body>
</html>
