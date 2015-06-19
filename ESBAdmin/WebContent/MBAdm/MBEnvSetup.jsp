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
			<form action='MBEnvSave.jsp' method="get">

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Broker Environment</b></th>
					<th><b>Broker Name</b></th>
					<th><b>Broker IP Address/HostName</b></th>
					<th><b>Broker QM Port</b></th>
				</tr>
				<tr>
					<td>
						<select name="brkEnv">
							<option value="DEV">DEV</option>
							<option value="STG">STG</option>
							<option value="QA">QA</option>
							<option value="PROD">PROD</option>
						</select>
					</td>
					<td><input type="text" name="brkName" /></td>
					<td><input type="text" name="brkHost" /></td>
					<td><input type="text" name="brkPort" /></td>
				</tr>
				<tr>
					<td>Example - DEV</td>
					<td>Example - BRKDEV01</td>
					<td>Example - esbdev.domain.com</td>
					<td>Example - 1414</td>
				</tr>

				<tr>
					<td colspan=4><center><input type="submit" value="Save"/></center></td>
				</tr>
			</table>
	<%	
}		
%>


</body>
</html>