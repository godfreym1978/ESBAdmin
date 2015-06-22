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
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Purge Queue</title>
</head>

<body>
	
		<center>
<%if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
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
	
	PCFCommons newPFCCM = new PCFCommons();
			
				String qName = request.getParameter("QName");
				try {
						//String returnMsg = 
						int msgPurged = newPFCCM.purgeQueue(qHost,Integer.parseInt(qPort), qName, qChannel);
						
						if (msgPurged<0){
							msgPurged = newMQAdUtil.purgeQueue(qMgr, qName);
							%>
							<b><%=msgPurged%></b> Messages Purged from the Queue - <b><%=qName%></b>
							- Queue Manager <b><%=qMgr%></b>
							<%
						}else{
							%>
							Messages Cleared from the Queue - <b><%=qName%></b>
							- Queue Manager <b><%=qMgr%></b>
							<%
						}
				} catch (NullPointerException e) {
					System.out.println(session.getAttribute("UserID"));
			%>Are you logged in to system? If not do so in <a
				href='Index.html'>here </a>
			<%
				}
}
			%>
</body>
</html>