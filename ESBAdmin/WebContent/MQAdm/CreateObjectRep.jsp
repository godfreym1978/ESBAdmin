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
<script type="text/javascript">
  function unhide(divID) {
    var item = document.getElementById(divID);
    if (item) {
      item.className=(item.className=='hidden')?'unhidden':'hidden';
    }
  }
</script>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Get Topic List</title>
</head>
<body>
<title>Browse Messages</title>

</head>
<body>	 
<%	if(session.getAttribute("UserID")==null){%>
<center>
	Looks like you are not logged in.<br> Please login with a valid
	user id <a href='../Index.html'><b>Here</b> </a>
</center>
<%	
}else{
	String UserID = session.getAttribute("UserID").toString();
	if(UserID.equals("admin")){
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
			if (request.getParameter("qName") != null){
				String resOutput = newPFCCM.createQueue(qHost, 
						Integer.parseInt(qPort), 
						request.getParameter("qType").toString(), 
						request.getParameter("qName").toString(), 
						Boolean.parseBoolean(request.getParameter("xmitType")),
						request.getParameter("backoutQName").toString(),
						qChannel);
				%>
						<table border=1 align=center class="gridtable">
							<tr><td>Queue Name</td><td><%=request.getParameter("qName")%></td></tr>
							<tr><td>Queue Type</td><td><%=request.getParameter("qType")%></td></tr>
							<tr><td>Backout Queue Name</td><td><%=request.getParameter("backoutQName")%></td></tr>
						</table>
				
						<center><b><%=resOutput%></b></center>
				<%
			}

			if (request.getParameter("chanName")!= null){
				newPFCCM.createChannel(qHost, 
						Integer.parseInt(qPort), 
						request.getParameter("chanType").toString(), 
						request.getParameter("chanName").toString(), 
						request.getParameter("xmitQueue"),
						qChannel);
				%>
				<table border=1 align=center >
						<tr><td>Channel Name</td><td><%=request.getParameter("chanName")%></td></tr>
						<tr><td>Channel Type</td><td><%=request.getParameter("chanType")%></td></tr>
						<tr><td>Connecting Qmgr IP</td><td><%=request.getParameter("targetQmgrIP")%></td></tr>
						<tr><td>Connecting Qmgr Port</td><td><%=request.getParameter("targetQmgrPort")%></td></tr>
						<tr><td>Transmit Queue</td><td><%=request.getParameter("xmitQueue")%></td></tr>
		
				</table>
				<%
			}

			if (request.getParameter("listName")!= null){
				int portNum;
				System.out.println(request.getParameter("portNum"));
				if(request.getParameter("portNum").toString().equals("")){
					portNum = 0; 	
				}else{
					portNum = Integer.parseInt(request.getParameter("portNum").toString());
				}
				
				newPFCCM.createListener(qHost, 
						Integer.parseInt(qPort), 
						request.getParameter("listType").toString(), 
						request.getParameter("listName").toString(), 
						portNum,
						qChannel);
				%>
				<table border=1 align=center >
						<tr><td>Listener Name</td><td><%=request.getParameter("listName")%></td></tr>
						<tr><td>Listener Type</td><td><%=request.getParameter("listType")%></td></tr>
						<tr><td>Port Number</td><td><%=request.getParameter("portNum")%></td></tr>
				</table>
				<%
			}

	
			if (request.getParameter("topicName")!= null){
				newPFCCM.createTopic(qHost, 
						Integer.parseInt(qPort), 
						request.getParameter("topicName").toString(), 
						request.getParameter("topicString").toString(), 
						request.getParameter("topicDesc").toString(),
						qChannel);
		
				%>
				<table border=1 align=center >
						<tr><td>Topic Name</td><td><%=request.getParameter("topicName")%></td></tr>
						<tr><td>Topic String</td><td><%=request.getParameter("topicString")%></td></tr>
						<tr><td>Topic Description</td><td><%=request.getParameter("topicDesc")%></td></tr>
				</table>
				<%
			}
	

			if (request.getParameter("subName")!= null){
				newPFCCM.createSub(qHost, 
						Integer.parseInt(qPort), 
						request.getParameter("subName").toString(), 
						request.getParameter("topicString").toString(), 
						request.getParameter("subTopicName").toString(),
						request.getParameter("subDest").toString(),
						request.getParameter("subDestQM").toString(),
						request.getParameter("subUsrID").toString(),
						qChannel);
				%>
				<table border=1 align=center >
				
						<tr><td>Subscription Name</td><td><%=request.getParameter("subName")%></td></tr>
						<tr><td>Topic String</td><td><%=request.getParameter("topicString")%></td></tr>
						<tr><td>Topic Name</td><td><%=request.getParameter("subTopicName")%></td></tr>
						<tr><td>Subscription Destination</td><td><%=request.getParameter("subDest")%></td></tr>
						<tr><td>Subscription Destination Queue Manager</td><td><%=request.getParameter("subDestQM")%></td></tr>
						<tr><td>Subscription User ID</td><td><%=request.getParameter("subUsrID")%></td></tr>
				</table>
				<%
			}
	
	
		}catch(Exception e){
			e.printStackTrace();
			%>
			<center> <b>Experienced the following error  - </b></center><br>
			<%
		    for (StackTraceElement element : e.getStackTrace()) {
		    	%>
		        <%=element.toString()%><br>
		        <%
		    }
		}
	}
}

System.gc();
	 %>
</body>
</html>