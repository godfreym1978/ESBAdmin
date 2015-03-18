
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<html>
<head>
<title>Create User Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@include file="../Style.css" %>
</style>
</head>
<body>
<%
if (session.getAttribute("UserID") != null) {
%>

		<form action='ChangePwdRep.jsp?UserID=<%=session.getAttribute("UserID").toString()%>' method="post">
			<table align=center borders=1 class="gridtable">
				<tr>
					<td>Old Password</td>
					<td><input type="password" name="oldPwd" /></td>
				</tr>
				<tr>
					<td>New Password</td>
					<td><input type="password" name="newPwd" /></td>
				</tr>
				<tr>
					<td><input type="submit" value="Change Password" /></td>
				</tr>
			</table>
		</form>
		<%
}else{
	%>
		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>
	<%
}
		%>
</body>
</html>