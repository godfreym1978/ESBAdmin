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
<%@ page import="java.util.*" %>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<%@ page import="org.apache.commons.csv.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@include file="../Style.css" %>
</style>
<title>Write Message to Port</title>
</head>
<body>
<center>

<%if(session.getAttribute("UserID")==null){
%>

	<center>
	Looks like you are not logged in.<br>
	
	Please login with a valid user id <a href='Index.html'><b>Here</b> </a>
	</center>
<%}else{
	
	String UsrID = new String(request.getParameter("UserID"));
	String UsrPwd = new String(request.getParameter("Pwd"));
	
	String []qMgr = request.getParameterValues("QueueMgr");
	String []broker = request.getParameterValues("Broker");
	
	int lineCtr = 0;
	boolean isUser=true;
	Util newUtil = new Util();
	File userFile = new File(System.getProperty("catalina.base")+File.separator+ "ESBAdmin"+File.separator+"Users");
	
	if(!userFile.exists()){
		userFile.createNewFile();
	}
	
	File userDir = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID);
	File userQMFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID+File.separator+"MQEnvironment.xml");
	File userBrkFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID+File.separator+"MBEnvironment.xml");
	for (String line : FileUtils.readLines(userFile)) {
		if(line.toString().equals(UsrID)){
			isUser=false;	
			%>
			<center>User <b><%=UsrID %></b> already exists.</center>
			<%			
		};
	}

	
	if(isUser){
		userDir.mkdir();
		boolean flag = newUtil.createUser(UsrID, UsrPwd);
		if (flag) { 
		%>	
		 	<center>User <b><%=UsrID %></b> created successfully.</center>
		<%} 

		//add userid to user file
		FileUtils.writeStringToFile(userFile, UsrID+"\n",true);
		
		int newQMCtr;
		String qHost = new String();
		String qMgrName = new String();

		MQAdminUtil newMQAdUtil = new MQAdminUtil();
		MBCommons newMBCmn = new MBCommons();
		String UserID = session.getAttribute("UserID").toString();
		List<Map<String, String>> MBList = newMBCmn.getMBEnv(UserID);
		List<Map<String, String>> MQList = newMQAdUtil.getQMEnv(UserID);
		
		Map<String,String> qmMap = new HashMap<String, String>();
		List<Map<String, String>> mqList = new ArrayList();
		
		for (newQMCtr=0;newQMCtr<qMgr.length;newQMCtr++){
			
			for(int i=0;i<MQList.size();i++){
				if(qMgr[newQMCtr].equals(MQList.get(i).get("QMTimeID"))){
					qmMap = new HashMap<String, String>();
					qmMap.put("QMTimeID", MQList.get(i).get("QMTimeID").toString());
					qmMap.put("QMName", MQList.get(i).get("QMName").toString());
					qmMap.put("QMHost", MQList.get(i).get("QMHost").toString());
					qmMap.put("QMPort", MQList.get(i).get("QMPort").toString());
					qmMap.put("QMChannel", MQList.get(i).get("QMChannel").toString());
					mqList.add(qmMap);
				}
			}
				%>
				<center>Queue Manager - <b><%=qMgrName%></b> on Host - <b><%=qHost%></b> - is setup for administration for user <%=UsrID%><br></center>
				<%
		}
		newUtil.writeXML(userQMFile.getAbsolutePath(), "MQEnvironment", mqList); 	

		int newMBCtr;
		String mbHost = new String();
		String mbQMgrPort = new String();

		Map<String,String> mbMap = new HashMap<String, String>();
		List<Map<String, String>> mbList = new ArrayList();

		for (newMBCtr=0;newMBCtr<broker.length;newMBCtr++){
			//FileUtils.writeStringToFile(userBrkFile, broker[newMBCtr].toString().trim()+"\n", true);
			
			for(int i=0;i<MBList.size();i++){
				if(broker[newMBCtr].equals(MBList.get(i).get("MBTimeID"))){
					mbMap = new HashMap<String, String>();
					mbMap.put("MBTimeID", MBList.get(i).get("MBTimeID").toString());
					mbMap.put("MBHost", MBList.get(i).get("MBHost").toString());
					mbMap.put("MBEnv", MBList.get(i).get("MBEnv").toString());
					mbMap.put("MBPort", MBList.get(i).get("MBPort").toString());
					mbMap.put("MBName", MBList.get(i).get("MBName").toString());
					mbList.add(mbMap);
				}
			}
				%>
				<center>Broker Host - <b><%=mbHost%></b>  with Queue Manager port <%=mbQMgrPort%>- is setup for administration for user <%=UsrID%><br></center>
				<%
			}
		newUtil.writeXML(userBrkFile.getAbsolutePath(), "MBEnvironment", mbList);
		}
		
}
%>

</body>
</html>