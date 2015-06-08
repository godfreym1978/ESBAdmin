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
<%@ page import="com.ibm.MQAdmin.*" %>
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
	//List<String> setupQueue = new ArrayList<String>();
	System.out.println(qMgr[0]);
	
	int lineCtr = 0;
	boolean isUser=true;
	Util newUtil = new Util();
	File userFile = new File(System.getProperty("catalina.base")+File.separator+ "ESBAdmin"+File.separator+"Users");
	
	if(!userFile.exists()){
		userFile.createNewFile();
	}
	
	File userDir = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID);
	File userQMFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID+File.separator+"QMEnv.txt");
	File userBrkFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID+File.separator+"MBEnv.txt");
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

		for (newQMCtr=0;newQMCtr<qMgr.length;newQMCtr++){
			FileUtils.writeStringToFile(userQMFile, qMgr[newQMCtr].toString().trim()+"\n", true);
			CSVParser parser = CSVParser.parse(qMgr[newQMCtr].toString().trim(), CSVFormat.RFC4180);
					
			for (CSVRecord csvRecord : parser) {
				qHost = csvRecord.get(0);
				qMgrName = csvRecord.get(1);
			}							
				%>
				<center>Queue Manager - <b><%=qMgrName%></b> on Host - <b><%=qHost%></b> - is setup for administration for user <%=UsrID%><br></center>
				<%
		}
	
		int newMBCtr;
		String mbHost = new String();
		String mbQMgrPort = new String();
	
		for (newMBCtr=0;newMBCtr<broker.length;newMBCtr++){
			FileUtils.writeStringToFile(userBrkFile, broker[newMBCtr].toString().trim()+"\n", true);
			
			CSVParser parser = CSVParser.parse(broker[newMBCtr].toString().trim(), CSVFormat.RFC4180);
			
			for (CSVRecord csvRecord : parser) {
				mbHost = csvRecord.get(1);
				mbQMgrPort = csvRecord.get(3);
			}							
				%>
				<center>Broker Host - <b><%=mbHost%></b>  with Queue Manager port <%=mbQMgrPort%>- is setup for administration for user <%=UsrID%><br></center>
				<%
			}
		}
}
%>

</body>
</html>