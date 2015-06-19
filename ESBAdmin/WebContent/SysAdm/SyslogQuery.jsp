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
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
</head>
<body>
<center>
<%
if(session.getAttribute("UserID")==null){
%>

		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>

<%}else{
	File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+File.separator+session.getAttribute("UserID").toString()+File.separator+"MBEnv.txt");
	String env = null;		
	String hostName = null;
	
	for (String line : FileUtils.readLines(userFile)) {
		%>
		<a href='SyslogRT.jsp?Env=<%=line.substring(line.indexOf("|")+1, line.indexOf(":"))%>'><%=line.substring(0,line.indexOf(";"))%> Syslog Realtime</a> <br>
		<%
	}
%>



	<form action='SyslogRep.jsp' method="post">

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
					<%
					for (String line : FileUtils.readLines(userFile)) {
					%>
						<option value="<%=line.substring(line.indexOf("|")+1, line.indexOf(":"))%>"><%=line.substring(0,line.indexOf(";"))%></option>
					<%
					}
					%>
				</select>
				</td>
			</tr>
			<tr>
				<td>Event Date </td>
				<td>
				<select name="month">
					<option value="">Month</option>
					<option value="Jan">Jan</option>
					<option value="Feb">Feb</option>
					<option value="Mar">Mar</option>
					<option value="Apr">Apr</option>
					<option value="May">May</option>
					<option value="Jun">Jun</option>
					<option value="Jul">Jul</option>
					<option value="Aug">Aug</option>
					<option value="Sep">Sep</option>
					<option value="Oct">Oct</option>
					<option value="Nov">Nov</option>
					<option value="Dec">Dec</option>
				</select>

				<select name="day">
					<option value="">Day</option>
					<option value="00">00</option>
					<option value=" 1">01</option>
					<option value=" 2">02</option>
					<option value=" 3">03</option>
					<option value=" 4">04</option>
					<option value=" 5">05</option>
					<option value=" 6">06</option>
					<option value=" 7">07</option>
					<option value=" 8">08</option>
					<option value=" 9">09</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13</option>
					<option value="14">14</option>
					<option value="15">15</option>
					<option value="16">16</option>
					<option value="17">17</option>
					<option value="18">18</option>
					<option value="19">19</option>
					<option value="20">20</option>
					<option value="21">21</option>
					<option value="22">22</option>
					<option value="23">23</option>
					<option value="24">24</option>
					<option value="25">25</option>
					<option value="26">26</option>
					<option value="27">27</option>
					<option value="28">28</option>
					<option value="29">29</option>
					<option value="30">30</option>
					<option value="31">31</option>
				
				</select>
				<select name="hour">
					<option value="">Hour Marker</option>
					<option value="00">00</option>
					<option value="01">01</option>
					<option value="02">02</option>
					<option value="03">03</option>
					<option value="04">04</option>
					<option value="05">05</option>
					<option value="06">06</option>
					<option value="07">07</option>
					<option value="08">08</option>
					<option value="09">09</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13</option>
					<option value="14">14</option>
					<option value="15">15</option>
					<option value="16">16</option>
					<option value="17">17</option>
					<option value="18">18</option>
					<option value="19">19</option>
					<option value="20">20</option>
					<option value="21">21</option>
					<option value="22">22</option>
					<option value="23">23</option>
					<option value="24">24</option>
 				</select>
				</td>
			</tr>
			<tr>
				<td>Number of Lines</td>
				<td><input type="text" name="tailCount" /></td>
			</tr>

			<tr>
				<td colspan=2><input type="submit" value="Submit" /></td>
			</tr>
			</form>
</table>
<%} 
System.gc();
%>
</body>
</html>