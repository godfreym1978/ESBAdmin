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
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Subscription List</title>
</head>
<body>
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
		Util newUtil = new Util();
		PCFCommons newPCFCom = new PCFCommons();

		List<Map> MQList = newMQAdUtil.getQMEnv(UserID);
	
		for (int i=0; i<MQList.size(); i++) {
			if(MQList.get(i).get("QMName").toString().equals(qMgr)){
				qHost = MQList.get(i).get("QMHost").toString();
				qPort = MQList.get(i).get("QMPort").toString();
				qChannel = MQList.get(i).get("QMChannel").toString();
				break;
			}
		}

		PCFCommons test = new PCFCommons();
		List<Map> topicDtls = newPCFCom.ListSubNames(qHost, Integer.parseInt(qPort), qChannel);
		int listCtr =0;
		int listCount =topicDtls.size();
		%>
		<center><b><u>List of Subscriptions in Queue Manager - <%=qMgr %></u></b></center><br>
		<table border=1 align=center class="gridtable">
			<tr>
				<th><b>Sub Name</b></th>
				<th><b>Sub Topic Name</b></th>
				<th><b>Sub Topic String</b></th>
				<th><b>Destination</b></th>
				<th><b>Sub User ID</b></th>
				<th><b>Sub Creation Date</b></th>
				<th><b>Sub Creation Time</b></th>
				<th><b>Sub Alteration Date</b></th>
				<th><b>Sub Alteration Time</b></th>
				<th><b>Download MQSC Script</b></th>
			</tr>
			<%
					while(listCtr<listCount) {
			%>
			<tr>
				<td><a
					href='QMgrSubDtl.jsp?qMgr=<%=qMgr%>&subName=<%=topicDtls.get(listCtr).get("MQCACF_SUB_NAME").toString()%>'
					> <%=topicDtls.get(listCtr).get("MQCACF_SUB_NAME")%></a>
				</td>

				<td><%=topicDtls.get(listCtr).get("MQCA_TOPIC_NAME")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_TOPIC_STRING")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCACF_DESTINATION")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCACF_SUB_USER_ID")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_CREATION_DATE")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_CREATION_TIME")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_ALTERATION_DATE")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_ALTERATION_TIME")%></td>
				<td><a
					href='../DownloadQObject?qMgr=<%=qMgr%>&objType=SUB&objName=<%=topicDtls.get(listCtr).get("MQCACF_SUB_NAME").toString()%>'
					> Download MQSC Script For This Subscription</a>
				</td>
				
				<%
				out.flush();
				listCtr++;
					}
					
				%>
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
