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
<%@ page import="java.sql.*" %>
<%@ page import="sun.misc.BASE64Decoder" %>
<%@ page import="com.jcraft.jsch.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.jcraft.jsch.Session" %>
<%@ page import="java.io.*" %>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
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
%>
<body>

	<table border="1" align=center class="gridtable">
	<tr>
	<td><b>Event Timestamp</b></td>
	<td><b>Event Brief</b></td>
	<td><b>Event Message</b></td>
	
	</tr>
	
	<%

	BASE64Decoder decoder = new BASE64Decoder();
	
	final String HOST = request.getParameter("Env").toString();
	final String USER = "menezesg";
	final int PORT = 22;
	final String PASS = new String(decoder.decodeBuffer("cGEzM3dvcmQ="));
	Channel channel = null;
	com.jcraft.jsch.Session jschSession = null;

	String date = null;
	String newStr = null;
	String logType = null;
	String logMsg = null;
	String workStr = new String("");

	try{
		JSch jsch = new JSch();
		jschSession = jsch.getSession(USER, HOST, PORT);
		jschSession.setPassword(PASS);
		java.util.Properties propConfig = new java.util.Properties();
		propConfig.put("StrictHostKeyChecking", "no");
		propConfig.put("PreferredAuthentications", "password");
		jschSession.setConfig(propConfig);
		jschSession.connect();
		String command = new String();
		command=new String("tail -f /var/mqsi/syslog/syslog.user");
	
		channel = jschSession.openChannel("exec");
		((ChannelExec)channel).setCommand(command);
		channel.setInputStream(null);
		InputStream in=channel.getInputStream();
		channel.connect();
		StringBuffer sbOld = new StringBuffer("");
		StringBuffer sbNew = new StringBuffer("");
		
		Util newUtil= new Util();  
		int whileCtr=1;
		
		byte[] tmp=new byte[1024];
		while(true){
			
			try{
				Thread.sleep(1000);
				whileCtr++;
				if(whileCtr>300){
					break;
				}
			}catch(Exception ee){
			}

			while(in.available()>0){
				int i=in.read(tmp, 0, 1024);
		    	sbNew.append(new String(tmp, 0, i));
		    	while(sbNew.indexOf("\n")>0){
		    		sbOld = new StringBuffer(sbNew.substring(0,sbNew.indexOf("\n")));
		    		workStr = sbOld.toString();

					if(workStr.indexOf(" c")>0){

					date = workStr.substring(0,workStr.indexOf(" c"));
					newStr = workStr.substring(workStr.indexOf(" c")+1,workStr.length());
					
					logType = newStr.substring(newStr.indexOf(" ")+1,newStr.length());
					
					logMsg = logType.substring(logType.indexOf(" ")+1,logType.length());
					
					if(logType.indexOf("error")>0){
						%>
						<tr>
							<td><font color=red><%=date%></font></td>
							<td><font color=red><%=logType.substring(0,logType.indexOf(" "))%></font></td>
							<td><font color=red><%=logMsg%></font></td>
						</tr>
						<%
						
					}else{
						%>
						<tr>
							<td><%=date%></td>
							<td><%=logType.substring(0,logType.indexOf(" "))%></td>
							<td><%=logMsg%></td>
						</tr>
						<%
					}
					out.flush();		
		  	  		sbNew = new StringBuffer(sbNew.substring(sbNew.indexOf("\n")+1, sbNew.length()));
		    	}
		    	if(i<0)break;
			}
			if(channel.isClosed()){
				break;
			}
			
			}
		}
	}catch (Exception e) {
		e.printStackTrace();
	}finally{
		System.out.println("In finally and about to close connection");
		channel.disconnect();
		jschSession.disconnect();
	}
}
System.gc();
%>
</table>
</body>
</html>
