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
<%@ page import="com.ibm.ESBAdmin.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.ibm.mq.MQEnvironment"%>
<%@ page import="com.ibm.mq.MQQueueManager"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Queue Manager Environment</title>
</head>
<body>
	<%
	if(session.getAttribute("UserID")==null){
	%>
		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>
	
	<%	
	}else{
	%>
			<center><h3> Queue Manager Environment</h3></center>
			

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Queue Manager HostName</b></th>
					<th><b>Queue Manager Name</b></th>
					<th><b>Queue Manager Port</b></th>
					<th><b>Queue Manager Channel</b></th>
				</tr>
				<tr>
					<td><%=request.getParameter("qmgrHost")%></td>
					<td><%=request.getParameter("qmgrName")%></td>
					<td><%=request.getParameter("qmgrPort")%></td>
					<td><%=request.getParameter("qmgrChl")%></td>
				</tr>
			</table>

	<%	
				
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();
		
		//MBCommons newMBCmn = new MBCommons();
		File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+
									File.separator+session.getAttribute("UserID").toString()+File.separator+"QMEnv.txt");
		if(!userFile.exists()){
			try{
				MQEnvironment.channel = request.getParameter("qmgrChl");
				MQEnvironment.port = Integer.parseInt(request.getParameter("qmgrPort"));
				MQEnvironment.hostname = request.getParameter("qmgrHost");
				
				MQQueueManager qmgr = new MQQueueManager(request.getParameter("qmgrName"));
				qmgr.disconnect();

				userFile.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(userFile));

				output.write(request.getParameter("qmgrHost")+","+
				request.getParameter("qmgrName")+","+
				request.getParameter("qmgrPort")+","+
				request.getParameter("qmgrChl"));
				
				output.close();
				%>
					<center>
    				The Queue Manager with above details has been successfully registered.<br>
    				</center>
					<hr>
    			<%	
				
				}catch(Exception e){
				%>
					<center>
    				Cannot register with the following details.<br>
    				<%=e.getMessage()%>
    				</center>
					<hr>
    			<%	
				}
		}else{
			String newQMgrEntry = new String(request.getParameter("qmgrHost")+","+
							request.getParameter("qmgrName")+","+
							request.getParameter("qmgrPort")+","+
							request.getParameter("qmgrChl"));

			if(FileUtils.readFileToString(userFile).contains(newQMgrEntry)){
			%>
				<center>
    			The Queue Manager with above details has already been registered.<br>
    			</center>
				<hr>
			<%					
			}else{
				try{
					MQEnvironment.channel = request.getParameter("qmgrChl");
					MQEnvironment.port = Integer.parseInt(request.getParameter("qmgrPort"));
					MQEnvironment.hostname = request.getParameter("qmgrHost");
					MQQueueManager qmgr = new MQQueueManager(request.getParameter("qmgrName"));
					qmgr.disconnect();
		
					BufferedWriter output = new BufferedWriter(new FileWriter(userFile,true));
					output.write("\n"+newQMgrEntry);
					output.close();
					%>
						<center>
	    				The Queue Manager with above details has been successfully registered.<br>
	    				</center>
						<hr>
	    			<%	
					}catch(Exception e){
					%>
						<center>
	    				Cannot register with the following details.<br>
	    				<%=e.getMessage()%>
	    				</center>
						<hr>
	    			<%	
					}
				}
			}
		System.gc();
		}
%>
</body>
</html>