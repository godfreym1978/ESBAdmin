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
<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Subscription Detail</title>
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

try{ 
			String UserID = session.getAttribute("UserID").toString();
			File userQMFile = new File(
							System.getProperty("catalina.base")
									+ File.separator+"ESBAdmin"+File.separator+UserID+File.separator+"QMEnv.txt");
			String qMgr = request.getParameter("qMgr");
			String qPort = null;
			String qHost = null;
			String qChannel = null;
			String subName = request.getParameter("subName");
			
			for (String line : FileUtils.readLines(userQMFile)) {
				if (line.indexOf(qMgr)>0){
					CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
					
					for (CSVRecord csvRecord : parser) {
						qHost = csvRecord.get(0);
						qPort = csvRecord.get(2);
						qChannel = csvRecord.get(3);
						}							
				}
			}
					
					Util newUtil = new Util();
					
					File userFile = new File(System.getProperty("catalina.base")
							+File.separator+ "ESBAdmin" + File.separator+qMgr + "." + UserID);
					
					PCFCommons test = new PCFCommons();
					List<Map> subDtls = test.ListSubStatus(qHost, Integer.parseInt(qPort), subName, qChannel);
					
					%>

		<center><b><u>List of Topics in Queue Manager - <%=qMgr %></u></b></center><br>
		<table border=1 align=center class="gridtable">
			<tr><td>Connection ID</td>
				<td><%=subDtls.get(0).get("MQBACF_CONNECTION_ID")%></td>
				</tr>
				<tr>
				<td>Durable Sub</td>
				<td><%=subDtls.get(0).get("MQIACF_DURABLE_SUBSCRIPTION")%></td>
				</tr>
				<tr>
				<td>Default Put Response Type</td>
				<td><%=subDtls.get(0).get("MQCACF_LAST_MSG_DATE")%></td>
				</tr>
				<tr>
				<td>Def Priority</td>
				<td><%=subDtls.get(0).get("MQCACF_LAST_MSG_TIME")%></td>
				</tr>
				<tr>
				<td>Durable Sub</td>
				<td><%=subDtls.get(0).get("MQIACF_PUBLISH_COUNT")%></td>
				</tr>
				<tr>
				<td>Resume Date</td>
				<td><%=subDtls.get(0).get("MQCA_RESUME_DATE")%></td>
				</tr>
				<tr>
				<td>Resume Time</td>
				<td><%=subDtls.get(0).get("MQCA_RESUME_TIME")%></td>
				</tr>
				<tr>
				<td>Sub User ID</td>
				<td><%=subDtls.get(0).get("MQCACF_SUB_USER_ID")%></td>
				</tr>
				<tr>
				<td>Subscription ID</td>
				<td><%=subDtls.get(0).get("MQBACF_SUB_ID")%></td>
				</tr>
		</table>
		<%
		}catch(Exception e){
			%>
			<center>
			We have encountered the following error<br>
			
			<font color=red><b><%=e%></b></font> 
			</center>
			<%
		}
}
System.gc();
%>
</body>
</html>
