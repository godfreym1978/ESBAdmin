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
<%@ page import="com.ibm.ESBAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file ="Style.css" %>
</style>

<title>Main Dashboard</title>
</head>

<%
Util newUtil = new Util();
String actualPassword = new String();

try{
	
	String UserID = request.getParameter("UserID").toString();
	File passwordFile = new File((System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator + UserID+File.separator + UserID));

	if (!passwordFile.exists()&&UserID.equalsIgnoreCase("admin")){
		System.out.println("admin user not found");
		File userDir = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+"admin");
		File userQMFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+"admin"+File.separator+"qMgr");
		File userBrkFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+"admin"+File.separator+"MBEnv.txt");
		userDir.mkdir();
		boolean flag = newUtil.createUser("admin", request.getParameter("Pwd").toString());
		passwordFile = new File((System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator + UserID+File.separator + UserID));
	}

	actualPassword = FileUtils.readFileToString(passwordFile);
	String enteredPassword = newUtil.md5Digest(request.getParameter("Pwd").toString());

	if (!actualPassword.equals(enteredPassword)) {
		%>
		<center>
		Sorry Password does not match!!!!<br>
		
		Please login <a href='Index.html'><b>Here</b> </a>
		</center>
		<%
		
	}else{
		session.setAttribute("UserID", UserID);
		System.out.println("about to check user"+request.getParameter("UserID"));
	%>
	<FRAMESET cols="20%, 80%">
	      <FRAME name="static" src='Login.jsp?UserID=<%=request.getParameter("UserID")%>'>/>
	      <FRAME name="dynamic" src='Readme.html'/>
	</FRAMESET>
	<%} 
	}catch(FileNotFoundException ex){
		%>
		<center>
		Sorry No user by that name exist!!!!<br>
		
		Please login with a valid user id <a href='Index.html'><b>Here</b> </a>
		</center>
	<%	
	}catch(NullPointerException ex){
	%>
	<center>
	Did you enter an user id?<br>
	
	Please login with a valid user id <a href='Index.html'><b>Here</b> </a>
	</center>
	<%	
}
%>
</html>