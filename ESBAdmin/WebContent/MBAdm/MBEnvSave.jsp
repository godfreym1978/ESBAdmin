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
<%@ page import="com.ibm.broker.config.proxy.*"%>
<%@ page import="java.util.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<%@ page import="java.sql.Timestamp"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Message Broker Environment</title>
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
			<center><h3> Message Broker Environment</h3></center>
			<form action='MBEnvSave.jsp' method="post">

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Broker Environment</b></th>
					<th><b>Broker Name</b></th>
					<th><b>Broker IP Address/HostName</b></th>
					<th><b>Broker QM Port</b></th>
				</tr>
				<tr>
					<td><%=request.getParameter("brkEnv")%></td>
					<td><%=request.getParameter("brkName")%></td>
					<td><%=request.getParameter("brkHost")%></td>
					<td><%=request.getParameter("brkPort")%></td>
				</tr>
			</table>
	<%	
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();
		
		MBCommons newMBCmn = new MBCommons();
		File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+
									File.separator+session.getAttribute("UserID").toString()+File.separator+"MBEnvironment.xml");
		if(!userFile.exists()){
			
			try{
				BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
							request.getParameter("brkHost"), Integer.parseInt(request.getParameter("brkPort")), "");
				BrokerProxy brkProxy = BrokerProxy.getInstance(bcp);
				brkProxy.disconnect();
				userFile.createNewFile();
				
				java.util.Date date= new java.util.Date();
				Timestamp newTimeStmp = new Timestamp(date.getTime());
				String newTimeID = newTimeStmp.toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");

				Map<String,String> mbMap = new HashMap<String, String>();
				mbMap.put("MBTimeID", newTimeID);
				mbMap.put("MBName", request.getParameter("brkName"));
				mbMap.put("MBHost", request.getParameter("brkHost"));
				mbMap.put("MBPort", request.getParameter("brkPort"));
				mbMap.put("MBEnv", request.getParameter("brkEnv"));
				
				List<Map> newList = new ArrayList();
				newList.add(mbMap);
				newUtil.writeXML(userFile.getAbsolutePath(), "MBEnvironment", newList); 	
				%>
					<center>
    				The broker runtime with above details has been successfully registered.<br>
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
			
			List<Map> MBList = newMBCmn.getMBEnv(UserID);
			
			boolean isSetupFlag = false;
			
			for(int i=0;i<MBList.size();i++){
				if(MBList.get(i).get("MBEnv").equals(request.getParameter("brkEnv"))&&
						MBList.get(i).get("MBHost").equals(request.getParameter("brkHost"))&&
						MBList.get(i).get("MBPort").equals(request.getParameter("brkPort"))&&
						MBList.get(i).get("MBName").equals(request.getParameter("brkName"))){
					isSetupFlag = true;
				}
			}
			if(isSetupFlag){
			%>
					<center>
    				The broker runtime with above details has already been registered.<br>
    				</center>
					<hr>
			<%					
			}else{
				try{
					BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
								request.getParameter("brkHost"), Integer.parseInt(request.getParameter("brkPort")), "");
					BrokerProxy brkProxy = BrokerProxy.getInstance(bcp);
					brkProxy.disconnect();
					
					java.util.Date date= new java.util.Date();
					Timestamp newTimeStmp = new Timestamp(date.getTime());
					String newTimeID = newTimeStmp.toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");

					Map<String,String> mbMap = new HashMap<String, String>();
					mbMap.put("MBTimeID", newTimeID);
					mbMap.put("MBEnv", request.getParameter("brkEnv"));
					mbMap.put("MBHost", request.getParameter("brkHost"));
					mbMap.put("MBPort", request.getParameter("brkPort"));
					mbMap.put("MBName", request.getParameter("brkName"));
					
					MBList.add(mbMap);
					newUtil.writeXML(userFile.getAbsolutePath(), "MBEnvironment", MBList); 	
					%>
					<center>
    				The broker runtime with above details has been successfully registered.<br>
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
	}
%>


</body>
</html>