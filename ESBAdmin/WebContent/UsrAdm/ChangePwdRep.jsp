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
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

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

<%	
}else{

	Util newUtil = new Util();
	
	String UsrID = new String(request.getParameter("UserID"));
	String oldPwd = new String(request.getParameter("oldPwd"));
	String newPwd = new String(request.getParameter("newPwd"));
	
	File passwordFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"+File.separator+UsrID+File.separator+UsrID);
	
	//File passwordFile = new File(("C:\\IBM\\apache-tomcat-7.0.42\\MQAdmin\\" + UsrID));
	String actualPassword = FileUtils.readFileToString(passwordFile);
	String enteredPassword = newUtil.md5Digest(request.getParameter("oldPwd").toString());
	
	if (actualPassword.equals(enteredPassword)){
		boolean flag = newUtil.changePasswd(UsrID, oldPwd, newPwd);
		if (flag) { %>
		 Password Changed successfully.
		 <%} 
	}else{%>
	 Old Password does not match with entered Old Password.
	 <%} 
}
%>
</body>
</html>