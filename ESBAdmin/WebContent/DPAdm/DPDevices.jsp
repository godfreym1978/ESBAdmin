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
<%@ page import="com.ibm.ESBAdmin.*"%>
<%@ page import="java.io.*"%>
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

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Sr. No.</b></th>
					<th><b>Symbolic Name</b></th>
					<th><b>Host Name</b></th>
					<th><b>Actual Firmware</b></th>
					<th><b>Current AMP Version</b></th>
					<th><b>GUI Port</b></th>
					<th><b>HLM Port</b></th>
					<th><b>Quiesce Timeout</b></th>
					<th><b>Serial Number</b></th>
					<th><b>User ID</b></th>
					<th><b>Is Device Reachable?</b></th>
					<th><b>Is Primary?</b></th>
					<th><b>Is Source Backup Supported</b></th>
					<th><b>Device Type</b></th>
					<th><b>Model Type</b></th>
					<th><b>Source Firmware</b></th>
				</tr>
	<%	
		DPUtil newDPUtil = new DPUtil();
		List<Map> deviceListDtl = newDPUtil.getDeviceDtl();

		int i = 0;
		int fileCtr = 0;
		int configCtr = 0;
		String strDomain = new String();
		int SrNo = 0;
		for (int s=0;s<deviceListDtl.size();s++){
			SrNo++;
		%>			
			<tr>
				<td><%=SrNo%></td>
				<td><%=deviceListDtl.get(s).get("SymbolicName")%></td>
				
				<td><a
						href="DPDevice.jsp?dpHostName=<%=deviceListDtl.get(s).get("HostName")%>">
						<%=deviceListDtl.get(s).get("HostName")%></a></td>
				
				<td><%=deviceListDtl.get(s).get("ActualFirmware")%></td>
				<td><%=deviceListDtl.get(s).get("CurrentAMPVersion")%></td>
				<td><%=deviceListDtl.get(s).get("GUIPort")%></td>
				<td><%=deviceListDtl.get(s).get("HLMPort")%></td>
				
				<td><%=deviceListDtl.get(s).get("QuiesceTimeout")%></td>
				<td><%=deviceListDtl.get(s).get("SerialNumber")%></td>
				
				<td><%=deviceListDtl.get(s).get("GetUserID")%></td>
				<td><%=deviceListDtl.get(s).get("IsDeviceReachable")%></td>
				<td><%=deviceListDtl.get(s).get("IsPrimary")%></td>
				<td><%=deviceListDtl.get(s).get("IsSourceBackupSupported")%></td>
				<td><%=deviceListDtl.get(s).get("DeviceType")%></td>
				<td><%=deviceListDtl.get(s).get("ModelType")%></td>
				<td><%=deviceListDtl.get(s).get("SourceFirmware")%></td>
			</tr>
		<%
		}
	}
%>
</table>

</body>
</html>