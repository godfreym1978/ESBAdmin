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
					<th><b>Domain Name</b></th>
					<th><b>Services</b></th>
				</tr>
	<%	
		String dpHostName = request.getParameter("dpHostName");
		DPUtil newDPUtil = new DPUtil();
		List<Map<String, Object>> deviceListDtl = newDPUtil.getDomains(dpHostName);

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
				<td><%=deviceListDtl.get(s).get("DPHostName")%></td>
				<td><%=deviceListDtl.get(s).get("DomainName")%></td>
				<td><a
						href="DPDomainSvcs.jsp?dpHostName=<%=deviceListDtl.get(s).get("DPHostName")%>
									&domainName=<%=deviceListDtl.get(s).get("DomainName")%>">
						Services Detail</a></td>
				
			</tr>
		<%
		}
	}
%>
</table>

</body>
</html>