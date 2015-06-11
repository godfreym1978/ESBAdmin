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
<%@ page import="com.ibm.ESBAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.csv.*"%>
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
	File qmFile = new File(System.getProperty("catalina.base")
			+ File.separator+"ESBAdmin"+File.separator+"admin"+File.separator+"QMEnv.txt");
	File mbFile = new File(System.getProperty("catalina.base")
			+ File.separator+"ESBAdmin"+File.separator+"admin"+File.separator+"MBEnv.txt");
	
%>
	<form action='CreateAccessUser.jsp' method="post">
		<table align=center borders=1 class="gridtable">
			<tr>
				<td>User ID</td>
				<td><input type="text" name="UserID" /></td>
			</tr>
			<tr>
				<td>User Password</td>
				<td><input type="password" name="Pwd" /></td>
			</tr>
			<tr>
				<td>Queue Manager(Host)</td>
				<td>Allow Access</td>
			</tr>
			<%
			
			String UserID = session.getAttribute("UserID").toString();
			String qMgr = null;
			String qPort = null;
			String qHost = null;
			String qChannel = null;

			MQAdminUtil newMQAdUtil = new MQAdminUtil();
			List<Map> MQList = newMQAdUtil.getQMEnv(UserID);

			for (int i=0; i<MQList.size(); i++) {
					qMgr = MQList.get(i).get("QMName").toString();
					qHost = MQList.get(i).get("QMHost").toString();
			%>
			<tr>
				<td>Queue Manager - <%=qMgr%> , Host - <%=qHost%></td>
				<td><input type="checkbox" name="QueueMgr" value="<%=qMgr%>"></td>
			</tr>
			<%
				} 
			%>
			<tr>
				<td>Broker (Host)</td>
				<td>Allow Access</td>
			</tr>
			<%

			for (String line : FileUtils.readLines(mbFile)) {
				String env = null;
				String mbHost = null;
				String mbMgr = null;
				
				CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
				for (CSVRecord csvRecord : parser) {
					env = csvRecord.get(0);
					mbHost = csvRecord.get(2);
					mbMgr = csvRecord.get(3);
					}							
			%>
			<tr>
				<td>Environment - <%=env%> , Host - <%=mbHost%> , QM Port <%=mbMgr%></td>
				<td><input type="checkbox" name="Broker" value="<%=line%>"></td>
			</tr>
			<%
				} 
			%>
			<tr>
				<td colspan=2><input type="submit" value="CreateUser" /></td>
			</tr>
		</table>
		</form>
		<%
}else{
	%>
		<center>
			<b>You don't have access to this page.<br> Please login with
				a valid user id <a href='../Index.html'><b>Here</b> </a></b>
			<center>
				<%
}

		%>
			
</body>
</html>