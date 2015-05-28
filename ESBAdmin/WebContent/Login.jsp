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
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.IOException"%>
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
<%@ include file="Style.css" %>
</style>
<title>Login Page</title>

</head>
<body>
	<%
		try {
			Util newUtil = new Util();
			String UserID = new String();

			String queryString = new String();
			Map<String, Object> map = new HashMap<String, Object>();
			String[] qMgrDtl = new String[3];
			if (session.getAttribute("UserID") != null) {

				UserID = session.getAttribute("UserID").toString();
				%>
				<h4>Welcome <%=UserID %></h4>
				<HR>			
				<h4>WebSphere MQ Environment</h4>
				<%
				File userFile = new File(
						System.getProperty("catalina.base")
								+ File.separator+"ESBAdmin"+File.separator+ session.getAttribute("UserID").toString()+File.separator+"QMEnv.txt");

				if (userFile.exists()){
				
					if(UserID.equals("admin")){%>	
					<a href='MQAdm/QMgrSetup.jsp'
										target='dynamic'>Setup Queue Manager Environment</a><br>
					<%}
						Map qMgrDtlMap = new HashMap();
						String QMgr = null;
						String Host = null;							
						for (String line : FileUtils.readLines(userFile)) {
							CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
							String qMgr = null;
							String qMgrHost = null;
						
							for (CSVRecord csvRecord : parser) {
								qMgrHost = csvRecord.get(0);
								qMgr = csvRecord.get(1);
							}
										
					%>

							<a href="javascript:unhide('<%=qMgr%>');"> 
														<b>Queue Manager - <%=qMgr%> / 
														Host - <%=qMgrHost%>
														
							</b><br>
							</a>
							<div id="col2">
								<div id="<%=qMgr%>" class="hidden">
										<a
											href='MQAdm/UserQMgrQList.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Queues for Admin</a><br>
										<a
											href='MQAdm/QueueList.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Qs in QMgr</a><br>
										<%if(UserID.equals("admin")){%>	
										<a
											href='MQAdm/ChannelList.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Channels in QMgr</a><br>
										<a
											href='MQAdm/ListenerList.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Listeners in QMgr</a><br>
	
										<a
											href='MQAdm/CreateObject.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Create Objects in QMgr</a><br>
										<%}%>	
											
										<%if(UserID.equals("admin") || UserID.indexOf("dev")==0){ %>	
	
										<a
											href='MQAdm/QMgrTopicList.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Topics in QMgr </a><br> 
										<a
											href='MQAdm/QMgrSubList.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Subscriptions in QMgr </a><br> 
										<a
											href='MQAdm/MQDataMove.jsp?qMgr=<%=qMgr%>'
											target='dynamic'> Data Move </a><br>
	
										<a
											href='DownloadQMgr?qMgr=<%=qMgr%>'
											target='dynamic'> Download QMgr </a><br>
	
										<%} %>	
								</div>
						</div>
				<HR>

					<%
						}
					}else{
					%>
						<%if(UserID.equals("admin")){%>	
						<a
							href='MQAdm/QMgrSetup.jsp'
							target='dynamic'>Setup Queue Manager Environment</a><br>
						<%}%>
					
					No Queue Manager has been set up for you.
					<%
						}
					%>
					
					
					<h4>Message Broker Environment</h4>
					<a href='MBAdm/MBEnvSetup.jsp'  target='dynamic'>Setup Message Brokers Environment</a>
					<br>
									
					<a href='MBAdm/MBEnvDtl.jsp' target='dynamic'>Message Brokers Environment</a>
					<br>
<!-- 
					<a href='MBAdm/AuditQuery.jsp' target='dynamic'>Check ESB Audit</a>
					<br>
 -->
					<HR>
					<h4>Datapower Environment</h4>
					<a href='DPAdm/DPEnvSetup.jsp'  target='dynamic'>Setup Datapower Environment</a>
					<br>
									
					<a href='DPAdm/DPEnvDtl.jsp' target='dynamic'>Datapower Environment</a>
					<br>

					<a href='DPAdm/DPDevices.jsp' target='dynamic'>Datapower Devices</a>
					<br>

					<%if(UserID.equals("admin")){%>

					<HR>
					<h4>User Management</h4>

						<a href='UsrAdm/CreateUser.jsp' target='dynamic'>Create Users for this Site</a>
						<br>

						<a href='UsrAdm/UserList.jsp' target='dynamic'>List of Users for this Site</a>
						<br>
					<%}%>
<!-- 
					<%if(UserID.equals("admin")||UserID.indexOf("dev-")>-1){%>
					<HR>
					<h4>Syslog Check</h4>

					<a href='SysAdm/SyslogQuery.jsp' target='dynamic'>Check Syslog</a>
					<br>

					<%}%>
 -->
					<HR>
					<h4>User Access </h4>

					<a href='UsrAdm/ChangePwd.jsp' target='dynamic'>Change Password</a>
					<br>
					
					<a href='Logout.jsp' target='_top'>Logout from this site</a>
					<br>
		
				<%
					}
				} catch(FileNotFoundException ex){
				%>
					
			<%}%>
</body>
</html>