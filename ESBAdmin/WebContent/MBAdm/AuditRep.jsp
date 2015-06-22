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
<%@ page import="java.io.*" %>
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

<center><button type="button" onClick="window.location.reload();">Refresh</button></center>

<% 

String mbName = request.getParameter("Env");
String date = request.getParameter("date");
String hour = request.getParameter("hour");
String GlobTranID = request.getParameter("GlobTranID");
String MFName = request.getParameter("MFName");
String EventType = request.getParameter("EventType");
String EventName = request.getParameter("EventName");
boolean whrFlag = false;
boolean hourFlag = false;



Connection conn = null;
ResultSet rs = null;
Statement st = null;

File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+File.separator+"MBAud.txt");

String userName = new String();
String password = new String();
String url = new String();
String query = new String();
String schema = new String();

for (String line : FileUtils.readLines(userFile)) {
	if(line.substring(0,line.indexOf("|")).equals(mbName)){
	userName = line.substring(line.indexOf("|")+1,line.indexOf(";"));
	password = line.substring(line.indexOf(";")+1,line.indexOf(":"));
	url = line.substring(line.indexOf(":")+1,line.indexOf("/"));
	schema = line.substring(line.indexOf("/")+1,line.length());
	query = "select * from "+schema+".wmb_msgs ";
	break;
	}
}


if(!date.isEmpty()){
	if(!hour.isEmpty()){
		query = query+" WHERE EVENT_TIMESTAMP like '"+date+" "+hour+"%'";
		hourFlag = true;
	}else{
		query = query+" WHERE EVENT_TIMESTAMP like '"+date+"%'";
	}
	whrFlag = true;
}

if(!GlobTranID.isEmpty()){
	if(whrFlag){
		query = query+" AND GLOBAL_TRANSACTION_ID like '%"+GlobTranID+"%'";
	}else{
		query = query+" WHERE GLOBAL_TRANSACTION_ID like '%"+GlobTranID+"%'";
		whrFlag = true;
	}
}

if(!MFName.isEmpty()){
	if(whrFlag){
		query = query+" AND MSGFLOW_NAME like '%"+MFName+"%'";
	}else{
		query = query+" WHERE MSGFLOW_NAME like '%"+MFName+"%'";
		whrFlag = true;
	}
}
if(!GlobTranID.isEmpty()){
	if(whrFlag){
		query = query+" AND MSGFLOW_NAME like '%"+MFName+"%'";
	}else{
		query = query+" WHERE MSGFLOW_NAME like '%"+MFName+"%'";
		whrFlag = true;
	}
}

if(!EventType.isEmpty()){
	if(whrFlag){
		query = query+" AND EVENT_TYPE like '%"+EventType+"%'";
	}else{
		query = query+" WHERE EVENT_TYPE like '%"+EventType+"%'";	
		whrFlag = true;
	}
}

if(!EventName.isEmpty()){
	if(whrFlag){
		query = query+" AND EVENT_NAME like '%"+EventName+"%'";
	}else{
		query = query+" WHERE EVENT_NAME like '%"+EventName+"%'";	
	}
}

/* query = query+" and rownum < 1000 ORDER BY EVENT_TIMESTAMP DESC"; */ 

if(hourFlag){
	query = "select * from ("+query+" ORDER BY EVENT_TIMESTAMP DESC) where rownum < 1000 ";
}else{
	query = "select * from ("+query+" ORDER BY EVENT_TIMESTAMP DESC) where rownum < 1000 ";
}




try {
	
	Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	conn = DriverManager.getConnection(url, userName, password);
	System.out.println("Database connection established");
	st = conn.createStatement();
	rs = st.executeQuery(query);

%>
<center><b><u>Audit Result for Date -<%=date%></u></b> </center>
<table border="1" class="gridtable">
<tr>
<td><b>EVENT_TIMESTAMP</b></td>
<td><b>EVENT_TYPE</b></td>
<td><b>EVENT_NAME</b></td>
<td><b>EVENT_SRCADDR</b></td>
<td><b>EXGRP_NAME</b></td>
<td><b>MSGFLOW_NAME</b></td>
<td><b>GLOBAL_TRANSACTION_ID</b></td>
<td><b>NODE_NAME</b></td>
<td><b>NODE_TYPE</b></td>
<td><b>TERMINAL_NAME</b></td>
<td><b>WMB_MSGKEY</b></td>

</tr>

<%
    //STEP 5: Extract data from result set
    while(rs.next()){

       //Retrieve by column name
%>
<tr>
<td><%=rs.getString("EVENT_TIMESTAMP")%></td>
<td><%=rs.getString("EVENT_TYPE")%></td>
<td><%=rs.getString("EVENT_NAME")%></td>
<td><%=rs.getString("EVENT_SRCADDR")%></td>
<td><%=rs.getString("EXGRP_NAME")%></td>
<td><%=rs.getString("MSGFLOW_NAME")%></td>
<td><%=rs.getString("GLOBAL_TRANSACTION_ID")%></td>
<td><%=rs.getString("NODE_NAME")%></td>
<td><%=rs.getString("NODE_TYPE")%></td>
<td><%=rs.getString("TERMINAL_NAME")%></td>
 <td><a href='../DownloadMsg?brkName=<%=mbName%>&WMB_MSGKEY=<%=rs.getString("WMB_MSGKEY")%>'>GetMessage </a></td>
</tr>

<%
           }
%>
</table>
<%
	
} catch (Exception e) {
	System.err.println("Cannot connect to database server");
	e.printStackTrace();
} finally {
	if (conn != null) {
		try {
			rs.close();
			conn.close ();
			System.out.println("Database connection terminated");
			System.gc();
		} catch (Exception e) { 
		}
	}
}
%>

</body>
</html>
