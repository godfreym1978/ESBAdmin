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
<%@ page import="java.io.*"%>
<%@ page import="com.ibm.mq.constants.MQConstants"%>
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@include file="../Style.css"%>
</style>
<title>Get Queue List</title>
</head>
<%
if(session.getAttribute("UserID")==null){%>
	<center>
		Looks like you are not logged in.<br> Please login with a valid
		user id <a href='../Index.html'><b>Here</b> </a>
	</center>
	<%	
}else{
	String UserID = session.getAttribute("UserID").toString();
	try{ 
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
		 			qHost, Integer.parseInt(qPort), qChannel);
%>

	<table border=1 align=center class="gridtable">
		<tr>
			<th><b>Queue Name</b></th>
			<th><b>Setup for Admin</b></th>
		</tr>
		<form action='AddQueueAdmin.jsp'>
			<input type=text name=qMgr value='<%=qMgr%>' hidden>
				<%
				int inCrement = 0;
				int iCount = 0;
				int inMsgCtr = 0;
				iCount = alQueueList.size();
				while (inCrement < iCount) {
				%>
				<tr>
					<td><a
						href="QueueDtl.jsp?qName=<%=alQueueList.get(inCrement).get("MQCA_Q_NAME")%>&qMgr=<%=qMgr%>">
						<%=alQueueList.get(inCrement).get("MQCA_Q_NAME")%></a></td>
					<%if(alQueueList.get(inCrement).get("MQIA_Q_TYPE").equals(MQConstants.MQQT_LOCAL)){%>
					<td><input type="checkbox" name="Queue"
						value="<%=alQueueList.get(inCrement).get("MQCA_Q_NAME")%>"></td>
					<%}%>
				
				</tr>
				<%
					inCrement++;
				}
				%>
				<tr>
					<td align=center colspan=3><input type="Submit" name="Submit"
						value="Submit"></td>
				</tr>
	</table>
	</form>
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
%>
</body>
</html>