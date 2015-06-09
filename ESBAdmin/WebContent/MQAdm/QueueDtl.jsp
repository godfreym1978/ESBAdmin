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
<%@ page import="com.ibm.ESBAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<%@ page import="org.apache.commons.csv.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Queue Details</title>
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

			String UserID = session.getAttribute("UserID").toString();
			File userQMFile = new File(
							System.getProperty("catalina.base")
									+ File.separator+"ESBAdmin"+File.separator+UserID+File.separator+"QMEnv.txt");
			String qMgr = request.getParameter("qMgr");
			String qPort = null;
			String qHost = null;
			String qChannel = null;

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

		String qName = request.getParameter("qName").toString();

		Util newUtil = new Util();
		
		PCFCommons test = new PCFCommons();
		List<Map> queueDtl = test.queueDetails(qHost, Integer.parseInt(qPort),qName, qChannel);
		List<Map> queueStatus = test.queueStatus(qHost, Integer.parseInt(qPort),qName, qChannel);
		
		%>
		<center><b><u>Queue Details - <%=qName%></u></b></center><br>
	 	<center>
	 	<a
			href='../DownloadQObject?qMgr=<%=qMgr%>&objType=QUEUE&objName=<%=qName%>'
			> Download MQSC Script For This Queue</a>
		</center><br>
		
		<table border=1 align=center class="gridtable">
			<tr>
				<th><b>Property</b></th>
	    		<th><b>Property Value</b></th>
			</tr>        

			<tr>
				<td>Queue Name</td>
				<td><%=queueDtl.get(0).get("MQCA_Q_NAME")%></td>
			</tr>        

			<tr>
				<td>Last Get Date</td>
				<td><%=queueStatus.get(0).get("MQCACF_LAST_GET_DATE")%></td>
			</tr>        
			<tr>
				<td>Last Get Time</td>
				<td><%=queueStatus.get(0).get("MQCACF_LAST_GET_TIME")%></td>
			</tr>        
			<tr>
				<td>Last Put Date</td>
				<td><%=queueStatus.get(0).get("MQCACF_LAST_PUT_DATE")%></td>
			</tr>        
			<tr>
				<td>Last Put Time</td>
				<td><%=queueStatus.get(0).get("MQCACF_LAST_PUT_TIME")%></td>
			</tr>
			<tr>
				<td>Current Queue Depth</td>
				<td><%=queueDtl.get(0).get("MQIA_CURRENT_Q_DEPTH")%></td>
			</tr>        
			<tr>
				<td>Open Input Count</td>
				<td><%=queueDtl.get(0).get("MQIA_OPEN_INPUT_COUNT")%></td>
			</tr>        
			<tr>
				<td>Open Output Count</td>
				<td><%=queueDtl.get(0).get("MQIA_OPEN_OUTPUT_COUNT")%></td>
			</tr>        
			<tr>
				<td>Oldest Message on Queue Age</td>
				<td><%=queueStatus.get(0).get("MQIACF_OLDEST_MSG_AGE")%></td>
			</tr>        
			<tr>
				<td>Uncommited Message Count</td>
				<td><%=queueStatus.get(0).get("MQIACF_UNCOMMITTED_MSGS")%></td>
			</tr>        
			<tr>
				<td>Backout Queue Name</td>
				<td><%=queueDtl.get(0).get("MQCA_BACKOUT_REQ_Q_NAME")%></td>
			</tr>        
			<tr>
				<td>Backout Threshold</td>
				<td><%=queueDtl.get(0).get("MQIA_BACKOUT_THRESHOLD")%></td>
			</tr>        
			<tr>
				<td>Default Persistence</td>
				<td><%=queueDtl.get(0).get("MQIA_DEF_PERSISTENCE")%></td>
			</tr>        
			<tr>
				<td>Max Queue Depth</td>
				<td><%=queueDtl.get(0).get("MQIA_MAX_Q_DEPTH")%></td>
			</tr>        
			<tr>
				<td>Queue Creation Date</td>
				<td><%=queueDtl.get(0).get("MQCA_CREATION_DATE")%></td>
			</tr>        
			<tr>
				<td>Queue Creation Time</td>
				<td><%=queueDtl.get(0).get("MQCA_CREATION_TIME")%></td>
			</tr>        
			<tr>
				<td>Queue Alteration Date</td>
				<td><%=queueDtl.get(0).get("MQCA_ALTERATION_DATE")%></td>
			</tr>        
			<tr>
				<td>Queue Alteration Time</td>
				<td><%=queueDtl.get(0).get("MQCA_ALTERATION_TIME")%></td>
			</tr>        
			<tr>
				<td>Cluster Name</td>
				<td><%=queueDtl.get(0).get("MQCA_CLUSTER_NAME")%></td>
			</tr>        
			<tr>
				<td>Max Message Length</td>
				<td><%=queueDtl.get(0).get("MQIA_MAX_MSG_LENGTH")%></td>
			</tr>        
			<tr>
				<td>Queue Depth High Limit</td>
				<td><%=queueDtl.get(0).get("MQIA_Q_DEPTH_HIGH_LIMIT")%></td>
			</tr>        
			<tr>
				<td>Queue Depth Low Limit</td>
				<td><%=queueDtl.get(0).get("MQIA_Q_DEPTH_LOW_LIMIT")%></td>
			</tr>        
			<tr>
				<td>Queue Depth High Event</td>
				<td><%=queueDtl.get(0).get("MQIA_Q_DEPTH_HIGH_EVENT")%></td>
			</tr>        
			<tr>
				<td>Queue Depth Low Event</td>
				<td><%=queueDtl.get(0).get("MQIA_Q_DEPTH_LOW_EVENT")%></td>
			</tr>        
			<tr>
				<td>Queue Depth Max Event</td>
				<td><%=queueDtl.get(0).get("MQIA_Q_DEPTH_MAX_EVENT")%></td>
			</tr>        
			<tr>
				<td>Queue Retention Interval</td>
				<td><%=queueDtl.get(0).get("MQIA_RETENTION_INTERVAL")%></td>
			</tr>        
			<tr>
				<td>Queue Trigger Depth</td>
				<td><%=queueDtl.get(0).get("MQIA_TRIGGER_DEPTH")%></td>
			</tr>        
			<tr>
		</table>

	<center><FORM><INPUT Type="button" VALUE="Back" onClick="history.go(-1);return true;"></FORM></center>
	<% 
	System.out.println(qName);
	System.out.println(qMgr);
	%>
	
	<center><FORM action="QueueDtl.jsp" >
		<Input type=hidden name=qName value=<%=qName%> >
		<Input type=hidden name=qMgr value=<%=qMgr%>>
		<INPUT Type="submit" VALUE="Refresh" ></FORM></center>
	 

<%
		}
//System.gc();
	 %>
</body>
</html>
