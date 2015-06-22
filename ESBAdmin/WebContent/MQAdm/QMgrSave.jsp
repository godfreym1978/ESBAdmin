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
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.ibm.mq.MQEnvironment"%>
<%@ page import="com.ibm.mq.MQQueueManager"%>
<%@ page import="java.sql.Timestamp"%>
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
		
		File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+
									File.separator+session.getAttribute("UserID").toString()+File.separator+"MQEnvironment.xml");
		
		MQAdminUtil newMQAdUtil = new MQAdminUtil();
		PCFCommons newPCFCom = new PCFCommons();

		if(!userFile.exists()){
			try{
				MQEnvironment.channel = request.getParameter("qmgrChl");
				MQEnvironment.port = Integer.parseInt(request.getParameter("qmgrPort"));
				MQEnvironment.hostname = request.getParameter("qmgrHost");
				
				MQQueueManager qmgr = new MQQueueManager(request.getParameter("qmgrName"));
				qmgr.disconnect();

				userFile.createNewFile();
				
				java.util.Date date= new java.util.Date();
				Timestamp newTimeStmp = new Timestamp(date.getTime());
				String newTimeID = newTimeStmp.toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");

				Map<String,String> qmMap = new HashMap<String, String>();
				qmMap.put("QMTimeID", newTimeID);
				qmMap.put("QMName", request.getParameter("qmgrName"));
				qmMap.put("QMHost", request.getParameter("qmgrHost"));
				qmMap.put("QMPort", request.getParameter("qmgrPort"));
				qmMap.put("QMChannel", request.getParameter("qmgrChl"));
				
				List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
				newList.add(qmMap);
				newUtil.writeXML(userFile.getAbsolutePath(), "MQEnvironment", newList); 	
				
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
			
			List<Map<String, String>> MQList = newMQAdUtil.getQMEnv(UserID);
			
			boolean isSetupFlag = false;
			
			for(int i=0;i<MQList.size();i++){
				if(MQList.get(i).get("QMName").equals(request.getParameter("qmgrName"))&&
						MQList.get(i).get("QMHost").equals(request.getParameter("qmgrHost"))&&
						MQList.get(i).get("QMPort").equals(request.getParameter("qmgrPort"))&&
						MQList.get(i).get("QMChannel").equals(request.getParameter("qmgrChl"))){
					isSetupFlag = true;
				}
			}
			if(isSetupFlag){
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

					
					java.util.Date date= new java.util.Date();
					Timestamp newTimeStmp = new Timestamp(date.getTime());
					String newTimeID = newTimeStmp.toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");

					Map<String, String> qmMap = new HashMap<String, String>();
					qmMap.put("QMTimeID", newTimeID);
					qmMap.put("QMName", request.getParameter("qmgrName"));
					qmMap.put("QMHost", request.getParameter("qmgrHost"));
					qmMap.put("QMPort", request.getParameter("qmgrPort"));
					qmMap.put("QMChannel", request.getParameter("qmgrChl"));
					
					MQList.add(qmMap);
					newUtil.writeXML(userFile.getAbsolutePath(), "MQEnvironment", MQList); 	
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