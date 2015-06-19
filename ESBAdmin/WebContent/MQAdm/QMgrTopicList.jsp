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
<title>Topic List</title>
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
		List<Map> MQList = newMQAdUtil.getQMEnv(UserID);
	
		for (int i=0; i<MQList.size(); i++) {
			if(MQList.get(i).get("QMName").toString().equals(qMgr)){
				qHost = MQList.get(i).get("QMHost").toString();
				qPort = MQList.get(i).get("QMPort").toString();
				qChannel = MQList.get(i).get("QMChannel").toString();
				break;
			}
		}
		Util newUtil = new Util();
					
		PCFCommons newPCFCmn = new PCFCommons();
		List<Map> topicDtls = newPCFCmn.ListTopicNames(qHost, Integer.parseInt(qPort), qChannel);
		int listCtr =0;
		int listCount =topicDtls.size();
		%>

		<center><b><u>List of Topics in Queue Manager - <%=qMgr %></u></b></center><br>
		<table border=1 align=center class="gridtable">
			<tr>
				<th><b>Topic Name</b></th>
				<th><b>Topic String</b></th>
				<th><b>Topic Description</b></th>
				<th><b>Topic Alter Date</b></th>
				<th><b>Topic Alter Time</b></th>
				<th><b>Download MQSC Script</b></th>
			</tr>
			<%
					while(listCtr<listCount) {
			%>
			<tr>
				<td><%=topicDtls.get(listCtr).get("MQCA_TOPIC_NAME")%></td>
				<td>			 	<a
					href='QMgrTopicDtl.jsp?qMgr=<%=qMgr%>&topicStr=<%=topicDtls.get(listCtr).get("MQCA_TOPIC_STRING").toString()%>'
					><%=topicDtls.get(listCtr).get("MQCA_TOPIC_STRING")%></a>
				</td>

				<td><%=topicDtls.get(listCtr).get("MQCA_TOPIC_DESC")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_ALTERATION_DATE")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_ALTERATION_TIME")%></td>
				<td>			 	<a
					href='../DownloadQObject?qMgr=<%=qMgr%>&objType=TOPIC&objName=<%=topicDtls.get(listCtr).get("MQCA_TOPIC_NAME").toString()%>'
					> Download MQSC Script For This Topic</a>
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
