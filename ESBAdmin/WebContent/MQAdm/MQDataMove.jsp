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
<%@ page import="com.ibm.esbadmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*,java.io.*"%>
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@include file="../Style.css"%>
</style>
<title>Move Messages from one Queue to another</title>
</head>

<body>
	<center>
<%
if(session.getAttribute("UserID")==null){%>
<center>
	Looks like you are not logged in.<br> Please login with a valid
	user id <a href='../Index.html'><b>Here</b> </a>
</center>
<%	
}else{
	String UserID = session.getAttribute("UserID").toString();
	
	String qMgr = request.getParameter("qMgr");
	String qPort = null;
	String qHost = null;
	String qChannel = null;

	MQAdminUtil newMQAdUtil = new MQAdminUtil();
	List<Map<String, String>> MQList = newMQAdUtil.getQMEnv(UserID);

	for (int i=0; i<MQList.size(); i++) {
		if(MQList.get(i).get("QMName").toString().equals(qMgr)){
			qHost = MQList.get(i).get("QMHost").toString();
			qPort = MQList.get(i).get("QMPort").toString();
			qChannel = MQList.get(i).get("QMChannel").toString();
			break;
		}
	}

		Util newUtil = new Util();

		PCFCommons newPFCCM = new PCFCommons();
	
		List<Map<String, Object>> alQueueList = newPFCCM.ListQueueNamesDtl(
	 			qHost, Integer.parseInt(qPort),qChannel);

			%>
			<h3> Move messages from Source Queue to Target Queue and the message count</h3>
		<form action='MQDataMoveRep.jsp'>	
		<table border=1 align=center width=50% class="gridtable">
			<tr>
				<td>Queue Mgr</td>
				<td><input type=hidden name=qMgr value=<%=qMgr%>><%=qMgr%></td>
			</tr>
			<tr>
				<td>Source Queue</td>
				<td><select name=srcQueueName>


					<%
					int inCrement = 0;
					int iCount = 0;
					int inMsgCtr = 0;
					iCount = alQueueList.size();

					while (inCrement < iCount) {
						%>
						<option value="<%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%>"><%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%></option>
						<%
						inCrement++;
					}
					%>

				</select></td>
			</tr>
			<tr>
				<td>Target Queue</td>
				<td><select name=tarQueueName>

					<%
					inCrement = 0;
					iCount = 0;
					inMsgCtr = 0;
					iCount = alQueueList.size();

					while (inCrement < iCount) {
					%>
						<option value="<%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%>"><%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%></option>
					<%
						inCrement++;
					}
					
					%>
									</select></td>
			</tr>
			<tr><td>Message Count</td><td><input type="text" name="msgCount"></td></tr>
			<tr><td  align=center colspan=2><input type="Submit" name="Submit" value="Submit"></td></tr>
			
		</table>
					
					<%
	}
					%>

</body>
</html>