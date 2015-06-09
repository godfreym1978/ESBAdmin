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
<title>Get Topic List</title>
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
			String topicStr = request.getParameter("topicStr");
			
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
					List<Map> topicDtls = test.ListTopicStatus(qHost, Integer.parseInt(qPort), topicStr, qChannel);
					
					%>

		<center><b><u>List of Topics in Queue Manager - <%=qMgr %></u></b></center><br>
		<table border=1 align=center class="gridtable">
			<tr><td>Cluster Name</td>
				<td><%=topicDtls.get(0).get("MQCA_CLUSTER_NAME")%></td>
				</tr>
				<tr>
				<td>Default Persistence</td>
				<td><%=topicDtls.get(0).get("MQIA_TOPIC_DEF_PERSISTENCE")%></td>
				</tr>
				<tr>
				<td>Default Put Response Type</td>
				<td><%=topicDtls.get(0).get("MQIA_DEF_PUT_RESPONSE_TYPE")%></td>
				</tr>
				<tr>
				<td>Def Priority</td>
				<td><%=topicDtls.get(0).get("MQIA_DEF_PRIORITY")%></td>
				</tr>
				<tr>
				<td>Durable Sub</td>
				<td><%=topicDtls.get(0).get("MQIA_DURABLE_SUB")%></td>
				</tr>
				<tr>
				<td>Inhibit Pub</td>
				<td><%=topicDtls.get(0).get("MQIA_INHIBIT_PUB")%></td>
				</tr>
				<tr>
				<td>Inhibit Sub</td>
				<td><%=topicDtls.get(0).get("MQIA_INHIBIT_SUB")%></td>
				</tr>
				<tr>
				<td>Admin Topic Name</td>
				<td><%=topicDtls.get(0).get("MQCA_ADMIN_TOPIC_NAME")%></td>
				</tr>
				<tr>
				<td>Durable Queue</td>
				<td><%=topicDtls.get(0).get("MQCA_MODEL_DURABLE_Q")%></td>
				</tr>
				<tr>
				<td>Non Durable Queue</td>
				<td><%=topicDtls.get(0).get("MQCA_MODEL_NON_DURABLE_Q")%></td>
				</tr>
				<tr>
				<td>PM Delivery</td>
				<td><%=topicDtls.get(0).get("MQIA_PM_DELIVERY")%></td>
				</tr>
				<tr>
				<td>NPM Delivery</td>
				<td><%=topicDtls.get(0).get("MQIA_NPM_DELIVERY")%></td>
				</tr>
				<tr>
				<td>Retained Pub</td>
				<td><%=topicDtls.get(0).get("MQIACF_RETAINED_PUBLICATION")%></td>
				</tr>
				<tr>
				<td>Pub Count</td>
				<td><%=topicDtls.get(0).get("MQIA_PUB_COUNT")%></td>
				</tr>
				<tr>
				<td>Sub Count</td>
				<td><%=topicDtls.get(0).get("MQIA_SUB_COUNT")%></td>
				</tr>
				<tr>
				<td>Sub Scope</td>
				<td><%=topicDtls.get(0).get("MQIA_SUB_SCOPE")%></td>
				</tr>
				<tr>
				<td>Pub Scope</td>
				<td><%=topicDtls.get(0).get("MQIA_PUB_SCOPE")%></td>
				</tr>
				<tr>
				<td>Use Dead Letter Queue</td>
				<td><%=topicDtls.get(0).get("MQIA_USE_DEAD_LETTER_Q")%></td>
				</tr>
				<tr>
				<td>Sub ID</td>
				<td><%=topicDtls.get(0).get("MQBACF_SUB_ID")%></td>
				</tr>
				<tr>
				<td>Sub User ID</td>
				<td><%=topicDtls.get(0).get("MQCACF_SUB_USER_ID")%></td>
				</tr>
				<tr>
				<td>Durable Subscription</td>
				<td><%=topicDtls.get(0).get("MQIACF_DURABLE_SUBSCRIPTION")%></td>
				</tr>
				<tr>
				<td>Sub Type</td>
				<td><%=topicDtls.get(0).get("MQIACF_SUB_TYPE")%></td>
				</tr>
				<tr>
				<td>Resume Date</td>
				<td><%=topicDtls.get(0).get("MQCA_RESUME_DATE")%></td>
				</tr>
				<tr>
				<td>Resume Time</td>
				<td><%=topicDtls.get(0).get("MQCA_RESUME_TIME")%></td>
				</tr>
				<tr>
				<td>Last Message Date</td>
				<td><%=topicDtls.get(0).get("MQCACF_LAST_MSG_DATE")%></td>
				</tr>
				<tr>
				<td>Last Message Time</td>
				<td><%=topicDtls.get(0).get("MQCACF_LAST_MSG_TIME")%></td>
				</tr>
				<tr>
				<td>Message Count</td>
				<td><%=topicDtls.get(0).get("MQIACF_MESSAGE_COUNT")%></td>
				</tr>
				<tr>
				<td>Connection ID</td>
				<td><%=topicDtls.get(0).get("MQBACF_CONNECTION_ID")%></td>
				</tr>
				<tr>
				<td>Last Pub Date</td>
				<td><%=topicDtls.get(0).get("MQCACF_LAST_PUB_DATE")%></td>
				</tr>
				<tr>
				<td>Last Pub Time</td>
				<td><%=topicDtls.get(0).get("MQCACF_LAST_PUB_TIME")%></td>
				</tr>
				<tr>
				<td>Publish Count</td>
				<td><%=topicDtls.get(0).get("MQIACF_PUBLISH_COUNT")%></td>
				</tr>
				<tr>
				<td>Connection ID</td>
				<td><%=topicDtls.get(0).get("MQBACF_CONNECTION_ID")%></td>
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
