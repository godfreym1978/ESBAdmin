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
<%@ page import="com.ibm.ESBAdmin.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
</head>
<body>

	<form action='AuditRep.jsp' method="post">

		<table align=center border="1" class="gridtable">

			<tr>
				<td>Details</td>
				<td>
				</td>
			</tr>

			<tr>
				<td>Environment</td>
				<td>
				<select name="Env">
					<option value="BRKPRD01">Production</option>
					<option value="BRKSTG01">Staging</option>
					<option value="BRKQA01">QA</option>
					<!-- 
					<option value="DEV">Development</option>
					 -->
				</select>
				</td>
			</tr>
			<tr>
			<%
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	        Date date=new Date();
	        String gmtString = sdf.format(date);
			%>
				<td>Event Date in GMT <b>(Current - <%=gmtString%>)</b></td>
				<td>
				<select name="date">
				<%
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				for(int dateCtr=0;dateCtr>-90;dateCtr--){
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE,dateCtr);
				%>
					<option value="<%=dateFormat.format(cal.getTime()) %>"><%=dateFormat.format(cal.getTime()) %></option>
				<%					
				}
 				%>
				</select>
				<select name="hour">
					<option value="">Hour Marker</option>
					<option value="00">00:00:00</option>
					<option value="01">01:00:00</option>
					<option value="02">02:00:00</option>
					<option value="03">03:00:00</option>
					<option value="04">04:00:00</option>
					<option value="05">05:00:00</option>
					<option value="06">06:00:00</option>
					<option value="07">07:00:00</option>
					<option value="08">08:00:00</option>
					<option value="09">09:00:00</option>
					<option value="10">10:00:00</option>
					<option value="11">11:00:00</option>
					<option value="12">12:00:00</option>
					<option value="13">13:00:00</option>
					<option value="14">14:00:00</option>
					<option value="15">15:00:00</option>
					<option value="16">16:00:00</option>
					<option value="17">17:00:00</option>
					<option value="18">18:00:00</option>
					<option value="19">19:00:00</option>
					<option value="20">20:00:00</option>
					<option value="21">21:00:00</option>
					<option value="22">22:00:00</option>
					<option value="23">23:00:00</option>
					<option value="24">24:00:00</option>
 				</select>
				</td>
			</tr>
			<tr>
				<td>Global Transaction ID</td>
				<td><input type="text" name="GlobTranID" /></td>
			</tr>
			<tr>
				<td>Message FlowName</td>
				<td><input type="text" name="MFName" /></td>
			</tr>
			<tr>
				<td>Event Type</td>
				<td><input type="text" name="EventType" /></td>
			</tr>
			<tr>
				<td>Event Name</td>
				<td><input type="text" name="EventName" /></td>
			</tr>

			<tr>
				<td colspan=2><input type="submit" value="Submit" /></td>
			</tr>
			
		</table>
	</form>
</body>
</html>