
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.ibm.broker.config.proxy.*"%>
<%@ page import="java.util.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

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
			<center><h3> DataPower Environment</h3></center>
			<form action='DPEnvSave.jsp' method="get">

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Datapower Symbolic Name</b></th>
					<th><b>Datapower Host</b></th>
					<th><b>Datapower UserID</b></th>
					<th><b>Datapower Password</b></th>
					<th><b>Datapower Admin Port</b></th>
				</tr>
				<tr>
					<td><input type="text" name="dpSymName" /></td>
					<td><input type="text" name="dpHost" /></td>
					<td><input type="text" name="dpUserID" /></td>
					<td><input type="password" name="dpPasswd" /></td>
					<td><input type="text" name="dpAdminPort" /></td>
				</tr>
				<tr>
					<td>Example - DevelopmentDP</td>
					<td>Example - dpdev.domain.com</td>
					<td>Example - admin</td>
					<td>Example - password</td>
					<td>Example - 5500</td>
				</tr>

				<tr>
					<td colspan=5><center><input type="submit" value="Save"/></center></td>
				</tr>
			</table>
	<%	
}		
%>


</body>
</html>