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
<%@ page import="java.net.*,java.io.*"%>  
<%@ page import="org.apache.commons.fileupload.*,org.apache.commons.io.*" %>
<%@ page import="com.ibm.broker.config.proxy.*"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Deploy BAR file to EG</title>
</head>

<body>
		<center>
			<%
if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>
<%}else{

	String UserID = session.getAttribute("UserID").toString();
	// Create a new file upload handler 
	DiskFileUpload upload = new DiskFileUpload();

	// parse request
	List items = upload.parseRequest(request);

	Util newUtil = new Util();
	MBCommons newMBCmn = new MBCommons();
	List<Map<String, String>> MBList = newMBCmn.getMBEnv(UserID);
				
	String hostName = new String();
	String env = null;
	String egName = request.getParameter("egName");
	String brokerName = request.getParameter("brokerName");
	int portNum=0;
	BrokerProxy brkProxy  = null;
				
	for (int i=0; i<MBList.size(); i++) {
		if(MBList.get(i).get("MBName").toString().equals(brokerName)){
			hostName = MBList.get(i).get("MBHost").toString();
			portNum = Integer.parseInt(MBList.get(i).get("MBPort").toString());
			brkProxy = newMBCmn.getBrokerProxy(hostName, portNum);
			break;
		}
	}

	//get uploaded file 
	FileItem file = (FileItem) items.get(0);
	String source = file.getName();

	File outfile = new File(System.getProperty("catalina.base")+"\\"+source);
	file.write(outfile);

	String returnMsg = 
						newMBCmn.deployBARFileToEG(brkProxy, egName, outfile.getAbsolutePath());
	if(returnMsg.equals("success")){
		%>
		<Center>The BAR file <b><%=source%></b> has been successfully deplolyed to Execution Group <b><%=egName%></b> on Broker <b><%=brokerName%>.</b></Center>					
		<%	
	}else{
		%>
					
		<%	
	}
	System.gc();
	brkProxy.disconnect();
}
			%>
</body>
</html>